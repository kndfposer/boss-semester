package ru.course.bosssemester.patterns.command;
import ru.course.bosssemester.service.BossGenerationService;
public class CreateBossGenerationCommand implements GenerationCommand {
    private final BossGenerationService service; private final Long requestId;
    public CreateBossGenerationCommand(BossGenerationService service, Long requestId){ this.service=service; this.requestId=requestId; }
    public void execute(){ service.generateExistingRequest(requestId); }
}
