package ru.course.bosssemester.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:boss_api_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "gigachat.demo-mode=true",
        "app.images-dir=target/test-images/boss",
        "app.default-daily-limit=10"
})
class BossGenerationApiIntegrationTest {
    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createBossSaveFavoriteAndReadHistory() throws Exception {
        String username = "boss" + System.nanoTime();
        String authJson = "{\"username\":\"" + username + "\",\"password\":\"1234\"}";

        MvcResult authResult = mvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(authJson))
                .andExpect(status().isOk())
                .andReturn();
        String token = mapper.readTree(authResult.getResponse().getContentAsString()).get("token").asText();

        String requestJson = """
                {
                  "subjects": ["Java", "Базы данных"],
                  "subjectDifficulties": [8, 6],
                  "difficulty": 7,
                  "emotionalBackground": "стресс",
                  "style": "CYBERPUNK"
                }
                """;

        MvcResult createResult = mvc.perform(post("/api/boss")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.imageUrl").isNotEmpty())
                .andExpect(jsonPath("$.difficulty").value(7))
                .andReturn();

        JsonNode created = mapper.readTree(createResult.getResponse().getContentAsString());
        long id = created.get("id").asLong();

        mvc.perform(post("/api/boss/" + id + "/saved?value=true")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saved").value(true));

        mvc.perform(post("/api/boss/" + id + "/favorite?value=true")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(true));

        mvc.perform(get("/api/boss")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));

        mvc.perform(get("/api/boss/saved")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].saved").value(true));

        mvc.perform(get("/api/boss/favorites")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].favorite").value(true));
    }
}