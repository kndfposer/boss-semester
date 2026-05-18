package ru.course.bosssemester.patterns.adapter;
import ru.course.bosssemester.patterns.builder.PromptProduct;
public interface ImageGenerator { ImageGenerationResult generate(PromptProduct prompt); }
