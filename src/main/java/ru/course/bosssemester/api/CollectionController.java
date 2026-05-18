package ru.course.bosssemester.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.course.bosssemester.dto.BossDtos.*;
import ru.course.bosssemester.patterns.facade.BossSemesterFacade;
import ru.course.bosssemester.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {
    private final AuthService auth;
    private final BossSemesterFacade facade;

    public CollectionController(AuthService auth, BossSemesterFacade facade) {
        this.auth = auth;
        this.facade = facade;
    }

    @PostMapping
    public CollectionResponse create(@RequestHeader(value="Authorization", required=false) String h,
                                     @Valid @RequestBody CollectionRequest r){
        return facade.createCollection(auth.requireUser(h), r);
    }

    @GetMapping
    public List<CollectionResponse> list(@RequestHeader(value="Authorization", required=false) String h){
        return facade.collections(auth.requireUser(h));
    }

    @GetMapping("/{id}")
    public CollectionResponse one(@RequestHeader(value="Authorization", required=false) String h,
                                  @PathVariable Long id){
        return facade.collection(auth.requireUser(h), id);
    }

    @PostMapping("/{id}/items")
    public CollectionResponse add(@RequestHeader(value="Authorization", required=false) String h,
                                  @PathVariable Long id,
                                  @RequestBody CollectionAddRequest r){
        return facade.addToCollection(auth.requireUser(h), id, r.requestId());
    }

    @DeleteMapping("/{id}/items/{requestId}")
    public CollectionResponse remove(@RequestHeader(value="Authorization", required=false) String h,
                                     @PathVariable Long id,
                                     @PathVariable Long requestId){
        return facade.removeFromCollection(auth.requireUser(h), id, requestId);
    }
}