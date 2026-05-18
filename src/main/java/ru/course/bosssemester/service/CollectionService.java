package ru.course.bosssemester.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.course.bosssemester.dto.BossDtos.*;
import ru.course.bosssemester.entity.*;
import ru.course.bosssemester.repository.*;

import java.time.Instant;
import java.util.List;

@Service
public class CollectionService {
    private final SemesterCollectionRepository collections;
    private final BossRequestRepository requests;
    private final BossGenerationService bossService;

    public CollectionService(SemesterCollectionRepository collections,
                             BossRequestRepository requests,
                             BossGenerationService bossService) {
        this.collections = collections;
        this.requests = requests;
        this.bossService = bossService;
    }

    @Transactional
    public CollectionResponse create(User u, CollectionRequest dto){
        SemesterCollection c = SemesterCollection.builder()
                .user(u)
                .title(dto.title())
                .description(dto.description())
                .createdAt(Instant.now())
                .build();
        return toDto(collections.save(c));
    }

    @Transactional(readOnly = true)
    public List<CollectionResponse> list(User u){
        return collections.findByUserOrderByCreatedAtDesc(u).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CollectionResponse one(User u, Long id){
        SemesterCollection c = collections.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Коллекция не найдена"));
        if(!c.getUser().getId().equals(u.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к коллекции");
        }
        return toDto(c);
    }

    @Transactional
    public CollectionResponse add(User u, Long id, Long reqId){
        SemesterCollection c = collections.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Коллекция не найдена"));
        if(!c.getUser().getId().equals(u.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к коллекции");
        }

        BossRequest r = requests.findById(reqId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Босс не найден"));
        if(!r.getUser().getId().equals(u.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к боссу");
        }

        boolean alreadyAdded = c.getRequests().stream().anyMatch(existing -> existing.getId().equals(r.getId()));
        if (!alreadyAdded) {
            c.getRequests().add(r);
        }

        return toDto(collections.save(c));
    }

    @Transactional
    public CollectionResponse remove(User u, Long collectionId, Long requestId) {
        SemesterCollection c = collections.findById(collectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Коллекция не найдена"));
        if(!c.getUser().getId().equals(u.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к коллекции");
        }
        c.getRequests().removeIf(r -> r.getId().equals(requestId));
        return toDto(collections.save(c));
    }

    private CollectionResponse toDto(SemesterCollection c){
        List<BossResponse> items = c.getRequests().stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(bossService::toDto)
                .toList();
        return new CollectionResponse(c.getId(), c.getTitle(), c.getDescription(), items.size(), c.getCreatedAt(), items);
    }
}