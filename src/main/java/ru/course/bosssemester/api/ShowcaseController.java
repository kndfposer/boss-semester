package ru.course.bosssemester.api;

import org.springframework.web.bind.annotation.*;
import ru.course.bosssemester.dto.BossDtos.*;
import ru.course.bosssemester.patterns.facade.BossSemesterFacade;
import ru.course.bosssemester.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/api/showcase")
public class ShowcaseController {
    private final AuthService auth;
    private final BossSemesterFacade facade;

    public ShowcaseController(AuthService auth, BossSemesterFacade facade) {
        this.auth = auth;
        this.facade = facade;
    }

    @PostMapping("/request/{requestId}")
    public ShowcaseResponse submit(@RequestHeader(value="Authorization", required=false) String h,
                                   @PathVariable Long requestId,
                                   @RequestBody(required = false) ShowcaseSubmitRequest r){
        String title = r == null ? null : r.title();
        return facade.submitShowcase(auth.requireUser(h), requestId, title);
    }

    @GetMapping
    public List<ShowcaseResponse> publicList(){
        return facade.publicShowcase();
    }

    @GetMapping("/moderation")
    public List<ShowcaseResponse> modList(@RequestHeader(value="Authorization", required=false) String h){
        return facade.moderation(auth.requireUser(h));
    }

    @PostMapping("/moderation/{id}")
    public ShowcaseResponse moderate(@RequestHeader(value="Authorization", required=false) String h,
                                     @PathVariable Long id,
                                     @RequestBody ShowcaseModerationRequest r){
        return facade.moderate(auth.requireUser(h), id, r.approve(), r.comment());
    }
}