package ru.course.bosssemester;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.course.bosssemester.dto.BossDtos.*;
import ru.course.bosssemester.patterns.facade.BossSemesterFacade;
import ru.course.bosssemester.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/api/boss")
public class BossController {
    private final AuthService auth;
    private final BossSemesterFacade facade;

    public BossController(AuthService auth, BossSemesterFacade facade) {
        this.auth = auth;
        this.facade = facade;
    }

    @PostMapping
    public BossResponse create(@RequestHeader(value="Authorization", required=false) String h,
                               @Valid @RequestBody CreateBossRequest r){
        return facade.createBoss(auth.requireUser(h), r);
    }

    @GetMapping
    public List<BossResponse> history(@RequestHeader(value="Authorization", required=false) String h){
        return facade.history(auth.requireUser(h));
    }

    @GetMapping("/favorites")
    public List<BossResponse> favorites(@RequestHeader(value="Authorization", required=false) String h){
        return facade.favorites(auth.requireUser(h));
    }

    @GetMapping("/saved")
    public List<BossResponse> saved(@RequestHeader(value="Authorization", required=false) String h){
        return facade.saved(auth.requireUser(h));
    }

    @GetMapping("/{id}")
    public BossResponse one(@RequestHeader(value="Authorization", required=false) String h,
                            @PathVariable Long id){
        return facade.one(auth.requireUser(h), id);
    }

    @PostMapping("/{id}/favorite")
    public BossResponse fav(@RequestHeader(value="Authorization", required=false) String h,
                            @PathVariable Long id,
                            @RequestParam boolean value){
        return facade.favorite(auth.requireUser(h), id, value);
    }

    @PostMapping("/{id}/saved")
    public BossResponse saved(@RequestHeader(value="Authorization", required=false) String h,
                              @PathVariable Long id,
                              @RequestParam boolean value){
        return facade.saved(auth.requireUser(h), id, value);
    }

    @PostMapping("/{id}/clone")
    public BossResponse clone(@RequestHeader(value="Authorization", required=false) String h,
                              @PathVariable Long id,
                              @RequestBody CloneRequest r){
        return facade.cloneBoss(auth.requireUser(h), id, r);
    }
}