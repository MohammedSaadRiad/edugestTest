package com.edugest.web.rest;

import com.edugest.domain.Grades;
import com.edugest.repository.GradesRepository;
import com.edugest.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.edugest.domain.Grades}.
 */
@RestController
@RequestMapping("/api/grades")
@Transactional
public class GradesResource {

    private static final Logger LOG = LoggerFactory.getLogger(GradesResource.class);

    private static final String ENTITY_NAME = "eduGestMicroserviceGrades";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GradesRepository gradesRepository;

    public GradesResource(GradesRepository gradesRepository) {
        this.gradesRepository = gradesRepository;
    }

    /**
     * {@code POST  /grades} : Create a new grades.
     *
     * @param grades the grades to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new grades, or with status {@code 400 (Bad Request)} if the grades has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Grades>> createGrades(@RequestBody Grades grades) throws URISyntaxException {
        LOG.debug("REST request to save Grades : {}", grades);
        if (grades.getId() != null) {
            throw new BadRequestAlertException("A new grades cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return gradesRepository
            .save(grades)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/grades/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /grades/:id} : Updates an existing grades.
     *
     * @param id the id of the grades to save.
     * @param grades the grades to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated grades,
     * or with status {@code 400 (Bad Request)} if the grades is not valid,
     * or with status {@code 500 (Internal Server Error)} if the grades couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Grades>> updateGrades(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Grades grades
    ) throws URISyntaxException {
        LOG.debug("REST request to update Grades : {}, {}", id, grades);
        if (grades.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, grades.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return gradesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return gradesRepository
                    .save(grades)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /grades/:id} : Partial updates given fields of an existing grades, field will ignore if it is null
     *
     * @param id the id of the grades to save.
     * @param grades the grades to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated grades,
     * or with status {@code 400 (Bad Request)} if the grades is not valid,
     * or with status {@code 404 (Not Found)} if the grades is not found,
     * or with status {@code 500 (Internal Server Error)} if the grades couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Grades>> partialUpdateGrades(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Grades grades
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Grades partially : {}, {}", id, grades);
        if (grades.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, grades.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return gradesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Grades> result = gradesRepository
                    .findById(grades.getId())
                    .map(existingGrades -> {
                        if (grades.getGrade() != null) {
                            existingGrades.setGrade(grades.getGrade());
                        }

                        return existingGrades;
                    })
                    .flatMap(gradesRepository::save);

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
     * {@code GET  /grades} : get all the grades.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of grades in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Grades>> getAllGrades() {
        LOG.debug("REST request to get all Grades");
        return gradesRepository.findAll().collectList();
    }

    /**
     * {@code GET  /grades} : get all the grades as a stream.
     * @return the {@link Flux} of grades.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Grades> getAllGradesAsStream() {
        LOG.debug("REST request to get all Grades as a stream");
        return gradesRepository.findAll();
    }

    /**
     * {@code GET  /grades/:id} : get the "id" grades.
     *
     * @param id the id of the grades to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the grades, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Grades>> getGrades(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Grades : {}", id);
        Mono<Grades> grades = gradesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(grades);
    }

    /**
     * {@code DELETE  /grades/:id} : delete the "id" grades.
     *
     * @param id the id of the grades to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteGrades(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Grades : {}", id);
        return gradesRepository
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
