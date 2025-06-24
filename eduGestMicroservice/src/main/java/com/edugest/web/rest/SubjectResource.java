package com.edugest.web.rest;

import com.edugest.domain.Subject;
import com.edugest.repository.SubjectRepository;
import com.edugest.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.edugest.domain.Subject}.
 */
@RestController
@RequestMapping("/api/subjects")
@Transactional
public class SubjectResource {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectResource.class);

    private static final String ENTITY_NAME = "eduGestMicroserviceSubject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubjectRepository subjectRepository;

    public SubjectResource(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    /**
     * {@code POST  /subjects} : Create a new subject.
     *
     * @param subject the subject to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subject, or with status {@code 400 (Bad Request)} if the subject has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Subject>> createSubject(@Valid @RequestBody Subject subject) throws URISyntaxException {
        LOG.debug("REST request to save Subject : {}", subject);
        if (subject.getId() != null) {
            throw new BadRequestAlertException("A new subject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return subjectRepository
            .save(subject)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/subjects/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /subjects/:id} : Updates an existing subject.
     *
     * @param id the id of the subject to save.
     * @param subject the subject to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subject,
     * or with status {@code 400 (Bad Request)} if the subject is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Subject>> updateSubject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Subject subject
    ) throws URISyntaxException {
        LOG.debug("REST request to update Subject : {}, {}", id, subject);
        if (subject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return subjectRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return subjectRepository
                    .save(subject)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /subjects/:id} : Partial updates given fields of an existing subject, field will ignore if it is null
     *
     * @param id the id of the subject to save.
     * @param subject the subject to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subject,
     * or with status {@code 400 (Bad Request)} if the subject is not valid,
     * or with status {@code 404 (Not Found)} if the subject is not found,
     * or with status {@code 500 (Internal Server Error)} if the subject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Subject>> partialUpdateSubject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Subject subject
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Subject partially : {}, {}", id, subject);
        if (subject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return subjectRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Subject> result = subjectRepository
                    .findById(subject.getId())
                    .map(existingSubject -> {
                        if (subject.getName() != null) {
                            existingSubject.setName(subject.getName());
                        }
                        if (subject.getCode() != null) {
                            existingSubject.setCode(subject.getCode());
                        }
                        if (subject.getDescription() != null) {
                            existingSubject.setDescription(subject.getDescription());
                        }

                        return existingSubject;
                    })
                    .flatMap(subjectRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /subjects} : get all the subjects.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subjects in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Subject>> getAllSubjects(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Subjects");
        if (eagerload) {
            return subjectRepository.findAllWithEagerRelationships().collectList();
        } else {
            return subjectRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /subjects} : get all the subjects as a stream.
     * @return the {@link Flux} of subjects.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Subject> getAllSubjectsAsStream() {
        LOG.debug("REST request to get all Subjects as a stream");
        return subjectRepository.findAll();
    }

    /**
     * {@code GET  /subjects/:id} : get the "id" subject.
     *
     * @param id the id of the subject to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subject, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Subject>> getSubject(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Subject : {}", id);
        Mono<Subject> subject = subjectRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(subject);
    }

    /**
     * {@code DELETE  /subjects/:id} : delete the "id" subject.
     *
     * @param id the id of the subject to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSubject(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Subject : {}", id);
        return subjectRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
