package ru.course.bosssemester.patterns.facade;

import org.springframework.stereotype.Component;
import ru.course.bosssemester.dto.BossDtos.*;
import ru.course.bosssemester.entity.User;
import ru.course.bosssemester.service.BossGenerationService;
import ru.course.bosssemester.service.CollectionService;
import ru.course.bosssemester.service.ShowcaseService;

import java.util.List;

@Component
public class BossSemesterFacade {
    private final BossGenerationService boss;
    private final CollectionService collections;
    private final ShowcaseService showcase;

    public BossSemesterFacade(BossGenerationService boss, CollectionService collections, ShowcaseService showcase) {
        this.boss = boss;
        this.collections = collections;
        this.showcase = showcase;
    }

    public BossResponse createBoss(User u, CreateBossRequest r){ return boss.create(u,r); }
    public List<BossResponse> history(User u){ return boss.history(u); }
    public List<BossResponse> favorites(User u){ return boss.favorites(u); }
    public List<BossResponse> saved(User u){ return boss.saved(u); }
    public BossResponse one(User u, Long id){ return boss.get(u,id); }
    public BossResponse favorite(User u, Long id, boolean value){ return boss.favorite(u,id,value); }
    public BossResponse saved(User u, Long id, boolean value){ return boss.saved(u,id,value); }
    public BossResponse cloneBoss(User u, Long id, CloneRequest r){ return boss.cloneFrom(u,id,r); }

    public CollectionResponse createCollection(User u, CollectionRequest r){ return collections.create(u,r); }
    public List<CollectionResponse> collections(User u){ return collections.list(u); }
    public CollectionResponse collection(User u, Long id){ return collections.one(u,id); }
    public CollectionResponse addToCollection(User u, Long cid, Long rid){ return collections.add(u,cid,rid); }
    public CollectionResponse removeFromCollection(User u, Long cid, Long rid){ return collections.remove(u,cid,rid); }

    public ShowcaseResponse submitShowcase(User u, Long requestId, String title){ return showcase.submit(u, requestId, title); }
    public List<ShowcaseResponse> publicShowcase(){ return showcase.publicList(); }
    public List<ShowcaseResponse> moderation(User u){ return showcase.moderation(u); }
    public ShowcaseResponse moderate(User u, Long id, boolean approve, String comment){ return showcase.moderate(u,id,approve,comment); }
}