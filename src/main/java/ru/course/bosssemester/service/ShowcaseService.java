package ru.course.bosssemester.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.course.bosssemester.dto.BossDtos.*;
import ru.course.bosssemester.entity.*;
import ru.course.bosssemester.repository.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class ShowcaseService {
    private final PublicShowcaseRepository showcase;
    private final BossRequestRepository requests;

    public ShowcaseService(PublicShowcaseRepository showcase, BossRequestRepository requests) {
        this.showcase = showcase;
        this.requests = requests;
    }

    @Transactional
    public ShowcaseResponse submit(User u, Long requestId, String title){
        BossRequest r = requests.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Босс не найден"));
        if(!r.getUser().getId().equals(u.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к боссу");
        }
        if(r.getStatus()!=RequestStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Публиковать можно только завершённые запросы");
        }
        PublicShowcaseItem item = showcase.findByRequest(r)
                .orElse(PublicShowcaseItem.builder().request(r).createdAt(Instant.now()).build());
        item.setTitle(title == null || title.isBlank() ? defaultTitle(r) : title.trim());
        item.setStatus(ShowcaseStatus.PENDING_MODERATION);
        return toDto(showcase.save(item));
    }

    @Transactional(readOnly = true)
    public List<ShowcaseResponse> publicList(){
        return showcase.findByStatusOrderByCreatedAtDesc(ShowcaseStatus.APPROVED).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ShowcaseResponse> moderation(User u){
        if(!"ADMIN".equals(u.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Нужна роль ADMIN");
        }
        return showcase.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    @Transactional
    public ShowcaseResponse moderate(User u, Long id, boolean approve, String comment){
        if(!"ADMIN".equals(u.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нужна роль ADMIN");
        }
        PublicShowcaseItem item = showcase.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Элемент витрины не найден"));
        item.setStatus(approve ? ShowcaseStatus.APPROVED : ShowcaseStatus.REJECTED);
        item.setModerationComment(comment);
        item.setModeratedAt(Instant.now());
        return toDto(showcase.save(item));
    }

    private ShowcaseResponse toDto(PublicShowcaseItem i){
        BossRequest r = i.getRequest();
        String img = r.getArtifact()==null ? null : "/api/images/" + r.getArtifact().getFileName();
        return new ShowcaseResponse(
                i.getId(),
                r.getId(),
                i.getTitle(),
                i.getStatus(),
                img,
                r.getUser().getUsername(),
                i.getCreatedAt(),
                splitSubjects(r.getSubjectsCsv()),
                splitDifficulties(r.getSubjectDifficultiesCsv()),
                r.getDifficulty(),
                r.getEmotionalBackground(),
                r.getStyle()
        );
    }

    private String defaultTitle(BossRequest r) {
        return "Босс #" + r.getId() + " от " + r.getUser().getUsername();
    }

    private List<String> splitSubjects(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    private List<Integer> splitDifficulties(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Integer::parseInt)
                .toList();
    }
}