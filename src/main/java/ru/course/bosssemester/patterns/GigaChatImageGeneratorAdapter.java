package ru.course.bosssemester.patterns;

import org.springframework.stereotype.Component;
import ru.course.bosssemester.config.GigaChatProperties;
import ru.course.bosssemester.patterns.builder.PromptProduct;
import ru.course.bosssemester.service.gigachat.GigaChatClient;

import java.nio.charset.StandardCharsets;

@Component
public class GigaChatImageGeneratorAdapter implements ImageGenerator {
    private final GigaChatClient client;
    private final GigaChatProperties props;

    public GigaChatImageGeneratorAdapter(GigaChatClient client, GigaChatProperties props) {
        this.client = client;
        this.props = props;
    }

    @Override
    public ImageGenerationResult generate(PromptProduct prompt) {
        if (props.demoMode() || props.authKey() == null || props.authKey().isBlank()) {
            String svg = "<svg xmlns='http://www.w3.org/2000/svg' width='1024' height='768'>" +
                    "<rect width='100%' height='100%' fill='#151827'/><text x='50%' y='42%' dominant-baseline='middle' text-anchor='middle' fill='white' font-size='44'>Босс семестра</text>" +
                    "<text x='50%' y='52%' dominant-baseline='middle' text-anchor='middle' fill='#b7c7ff' font-size='22'>DEMO MODE: подключите GIGACHAT_AUTH_KEY</text></svg>";
            return new ImageGenerationResult("demo-local", svg.getBytes(StandardCharsets.UTF_8), "image/svg+xml", "svg");
        }
        String fileId = client.createImageAndReturnFileId(prompt.systemPrompt(), prompt.userPrompt());
        byte[] bytes = client.downloadFile(fileId);
        return new ImageGenerationResult(fileId, bytes, "image/jpeg", "jpg");
    }
}