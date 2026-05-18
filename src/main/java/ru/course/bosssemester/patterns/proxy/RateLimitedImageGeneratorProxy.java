package ru.course.bosssemester.patterns.proxy;

import org.springframework.stereotype.Component;
import ru.course.bosssemester.entity.User;
import ru.course.bosssemester.patterns.adapter.GigaChatImageGeneratorAdapter;
import ru.course.bosssemester.patterns.adapter.ImageGenerationResult;
import ru.course.bosssemester.patterns.builder.PromptProduct;

@Component
public class RateLimitedImageGeneratorProxy {
    private final GigaChatImageGeneratorAdapter realGenerator;
    private final RateLimitService limits;

    public RateLimitedImageGeneratorProxy(GigaChatImageGeneratorAdapter realGenerator, RateLimitService limits) {
        this.realGenerator = realGenerator;
        this.limits = limits;
    }

    public ImageGenerationResult generate(User user, PromptProduct prompt) {
        limits.checkAndIncrement(user);
        return realGenerator.generate(prompt);
    }
}