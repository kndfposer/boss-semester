package ru.course.bosssemester.patterns.adapter;

import org.junit.jupiter.api.Test;
import ru.course.bosssemester.config.GigaChatProperties;
import ru.course.bosssemester.patterns.GigaChatImageGeneratorAdapter;
import ru.course.bosssemester.patterns.ImageGenerationResult;
import ru.course.bosssemester.patterns.builder.PromptProduct;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class GigaChatImageGeneratorAdapterTest {
    @Test
    void returnsLocalSvgInDemoMode() {
        GigaChatProperties props = new GigaChatProperties("", "GIGACHAT_API_PERS", "oauth", "api", "GigaChat", true);
        GigaChatImageGeneratorAdapter adapter = new GigaChatImageGeneratorAdapter(null, props);

        ImageGenerationResult result = adapter.generate(new PromptProduct("system", "user"));

        assertEquals("demo-local", result.gigaFileId());
        assertEquals("image/svg+xml", result.contentType());
        assertEquals("svg", result.extension());
        assertTrue(new String(result.content(), StandardCharsets.UTF_8).contains("Босс семестра"));
    }
}