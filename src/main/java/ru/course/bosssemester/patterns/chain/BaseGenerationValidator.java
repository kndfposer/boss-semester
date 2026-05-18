package ru.course.bosssemester.patterns.chain;
public abstract class BaseGenerationValidator implements GenerationValidator {
    private GenerationValidator next;
    public void setNext(GenerationValidator next){ this.next = next; }
    protected void validateNext(GenerationValidationContext context){ if(next != null) next.validate(context); }
}
