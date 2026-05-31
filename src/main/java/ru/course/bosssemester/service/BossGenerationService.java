package ru.course.bosssemester.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.course.bosssemester.dto.BossDtos.*;
import ru.course.bosssemester.entity.*;
import ru.course.bosssemester.patterns.ImageGenerationResult;
import ru.course.bosssemester.patterns.builder.*;
import ru.course.bosssemester.patterns.chain.*;
import ru.course.bosssemester.patterns.memento.*;
import ru.course.bosssemester.patterns.observer.RequestEventPublisher;
import ru.course.bosssemester.patterns.proxy.RateLimitedImageGeneratorProxy;
import ru.course.bosssemester.patterns.state.*;
import ru.course.bosssemester.repository.*;

import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BossGenerationService {
    private final BossRequestRepository requests;
    private final BossImageArtifactRepository artifacts;
    private final PromptDirector promptDirector;
    private final GenerationValidationChain validation;
    private final RateLimitedImageGeneratorProxy generator;
    private final RequestEventPublisher events;

    @Value("${app.images-dir:generated-images}")
    private String imagesDir;

    public BossGenerationService(BossRequestRepository requests,
                                 BossImageArtifactRepository artifacts,
                                 PromptDirector promptDirector,
                                 GenerationValidationChain validation,
                                 RateLimitedImageGeneratorProxy generator,
                                 RequestEventPublisher events) {
        this.requests = requests;
        this.artifacts = artifacts;
        this.promptDirector = promptDirector;
        this.validation = validation;
        this.generator = generator;
        this.events = events;
    }

    @Transactional
    public BossResponse create(User user, CreateBossRequest incoming) {
        CreateBossRequest dto = normalizeDifficulty(incoming);
        validation.validate(new GenerationValidationContext(user, dto));

        PromptProduct prompt = promptDirector.construct(dto);
        BossRequest r = BossRequest.builder()
                .user(user)
                .subjectsCsv(String.join(", ", dto.subjects()))
                .subjectDifficultiesCsv(toCsv(dto.subjectDifficulties()))
                .difficulty(dto.difficulty())
                .emotionalBackground(dto.emotionalBackground())
                .style(dto.style())
                .status(RequestStatus.PENDING)
                .finalPrompt(prompt.userPrompt())
                .favorite(false)
                .saved(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        new PendingState().apply(r);
        requests.save(r);
        generateExistingRequest(r.getId());
        return toDto(requests.findById(r.getId()).orElseThrow());
    }

    @Transactional
    public void generateExistingRequest(Long id) {
        BossRequest r = requests.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрос не найден"));

        new RunningState().apply(r);
        r.setUpdatedAt(Instant.now());
        requests.saveAndFlush(r);

        try {
            CreateBossRequest dto = new CreateBossRequest(
                    splitSubjects(r.getSubjectsCsv()),
                    splitDifficulties(r.getSubjectDifficultiesCsv()),
                    r.getDifficulty(),
                    r.getEmotionalBackground(),
                    r.getStyle()
            );

            PromptProduct prompt = promptDirector.construct(dto);
            r.setFinalPrompt(prompt.userPrompt());

            events.notify(r, "GIGACHAT_GENERATION_START", true, "Запрос к генерации изображения отправлен");
            ImageGenerationResult image = generator.generate(r.getUser(), prompt);

            Path dir = Paths.get(imagesDir).toAbsolutePath();
            Files.createDirectories(dir);
            String fileName = "boss-" + r.getId() + "-" + System.currentTimeMillis() + "." + image.extension();
            Path path = dir.resolve(fileName);
            Files.write(path, image.content());

            BossImageArtifact artifact = BossImageArtifact.builder()
                    .request(r)
                    .gigaChatFileId(image.gigaFileId())
                    .fileName(fileName)
                    .contentType(image.contentType())
                    .localPath(path.toString())
                    .sizeBytes((long) image.content().length)
                    .createdAt(Instant.now())
                    .build();

            if (r.getArtifact() != null) {
                artifacts.delete(r.getArtifact());
            }

            r.setArtifact(artifact);
            new CompletedState().apply(r);
            r.setUpdatedAt(Instant.now());
            requests.save(r);
            events.notify(r, "GIGACHAT_GENERATION_FINISH", true, "Изображение сохранено: " + fileName);
        } catch (Exception e) {
            String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            new ErrorState(msg).apply(r);
            r.setUpdatedAt(Instant.now());
            requests.save(r);
            events.notify(r, "GIGACHAT_GENERATION_ERROR", false, msg);
        }
    }

    @Transactional(readOnly = true)
    public List<BossResponse> history(User user) {
        return requests.findByUserOrderByCreatedAtDesc(user).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<BossResponse> favorites(User user) {
        return requests.findByUserAndFavoriteTrueOrderByCreatedAtDesc(user).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<BossResponse> saved(User user) {
        return requests.findByUserAndSavedTrueOrderByCreatedAtDesc(user).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public BossResponse get(User user, Long id) {
        BossRequest r = requests.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Не найдено"));
        if(!r.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Нет доступа");
        }
        return toDto(r);
    }

    @Transactional
    public BossResponse favorite(User user, Long id, boolean value) {
        BossRequest r = owned(user,id);
        r.setFavorite(value);
        return toDto(requests.save(r));
    }

    @Transactional
    public BossResponse saved(User user, Long id, boolean value) {
        BossRequest r = owned(user,id);
        r.setSaved(value);
        return toDto(requests.save(r));
    }

    @Transactional
    public BossResponse cloneFrom(User user, Long sourceId, CloneRequest changes) {
        BossRequest source = owned(user, sourceId);
        RequestMemento m = new RequestOriginator(source).save();

        List<String> subjects = changes.subjects() == null ? m.subjects() : changes.subjects();
        List<Integer> subjectDifficulties = changes.subjectDifficulties();
        Integer difficulty = changes.difficulty();

        if ((subjectDifficulties == null || subjectDifficulties.isEmpty()) && difficulty == null) {
            subjectDifficulties = splitDifficulties(source.getSubjectDifficultiesCsv());
            difficulty = m.difficulty();
        }

        CreateBossRequest dto = normalizeDifficulty(new CreateBossRequest(
                subjects,
                subjectDifficulties,
                difficulty,
                changes.emotionalBackground() == null ? m.emotion() : changes.emotionalBackground(),
                changes.style() == null ? m.style() : changes.style()
        ));
        return create(user, dto);
    }

    private BossRequest owned(User u, Long id){
        BossRequest r = requests.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Не найдено"));
        if(!r.getUser().getId().equals(u.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Нет доступа");
        }
        return r;
    }

    public BossResponse toDto(BossRequest r) {
        String imageUrl = r.getArtifact() == null ? null : "/api/images/" + r.getArtifact().getFileName();
        return new BossResponse(
                r.getId(),
                splitSubjects(r.getSubjectsCsv()),
                splitDifficulties(r.getSubjectDifficultiesCsv()),
                r.getDifficulty(),
                r.getEmotionalBackground(),
                r.getStyle(),
                r.getStatus(),
                r.isFavorite(),
                r.isSaved(),
                imageUrl,
                r.getErrorMessage(),
                r.getCreatedAt(),
                r.getFinalPrompt()
        );
    }

    private CreateBossRequest normalizeDifficulty(CreateBossRequest dto) {
        List<Integer> perSubject = dto.subjectDifficulties();
        Integer average = dto.difficulty();

        if (perSubject != null && !perSubject.isEmpty()) {
            average = (int) Math.round(perSubject.stream().mapToInt(Integer::intValue).average().orElse(5.0));
        }
        if (average == null) {
            average = 5;
        }

        return new CreateBossRequest(dto.subjects(), perSubject, average, dto.emotionalBackground(), dto.style());
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

    private String toCsv(List<Integer> values) {
        if (values == null || values.isEmpty()) return null;
        return values.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
}