package ru.course.bosssemester.service.gigachat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.course.bosssemester.config.GigaChatProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GigaChatClient {

    private final GigaChatProperties props;
    private final ObjectMapper mapper = new ObjectMapper();

    private WebClient webClient;
    private String accessToken;
    private long expiresAt;

    public GigaChatClient(GigaChatProperties props) {
        this.props = props;
    }

    private WebClient webClient() {
        if (webClient != null) {
            return webClient;
        }

        try {
            var sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            HttpClient httpClient = HttpClient.create()
                    .secure(ssl -> ssl.sslContext(sslContext));

            webClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();

            return webClient;
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось создать WebClient для GigaChat", e);
        }
    }

    public String accessToken() {
        if (props.authKey() == null || props.authKey().isBlank()) {
            throw new IllegalStateException("Не задан ключ GigaChat. Проверь gigachat.auth-key в application.properties");
        }

        long now = System.currentTimeMillis();

        if (accessToken != null && now + 30_000 < expiresAt) {
            return accessToken;
        }

        String body = "scope=" + props.scope();

        String json = webClient().post()
                .uri(props.oauthUrl())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + props.authKey())
                .header("RqUID", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode node = mapper.readTree(json);

            if (!node.has("access_token")) {
                throw new IllegalStateException("GigaChat не вернул access_token. Ответ: " + json);
            }

            accessToken = node.get("access_token").asText();
            expiresAt = node.has("expires_at") ? node.get("expires_at").asLong() : now + 1_700_000;
            return accessToken;
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось прочитать OAuth-ответ GigaChat. Ответ: " + json, e);
        }
    }

    public String createImageAndReturnFileId(String systemPrompt, String userPrompt) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", props.model());
        payload.put("function_call", "auto");
        payload.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        String json = webClient().post()
                .uri(props.apiBaseUrl() + "/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken())
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractFileId(json);
    }

    public byte[] downloadFile(String fileId) {
        return webClient().get()
                .uri(props.apiBaseUrl() + "/files/" + fileId + "/content")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken())
                .accept(MediaType.ALL)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    private String extractFileId(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            String text = root.toString();
            Matcher matcher = Pattern.compile("[0-9a-fA-F-]{20,}").matcher(text);

            if (matcher.find()) {
                return matcher.group();
            }

            throw new IllegalStateException("В ответе GigaChat не найден file_id. Ответ: " + json);
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось извлечь file_id изображения из ответа GigaChat. Ответ: " + json, e);
        }
    }
}