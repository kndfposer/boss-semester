package ru.course.bosssemester.patterns.chain;
public interface GenerationValidator { void setNext(GenerationValidator next); void validate(GenerationValidationContext context); }
