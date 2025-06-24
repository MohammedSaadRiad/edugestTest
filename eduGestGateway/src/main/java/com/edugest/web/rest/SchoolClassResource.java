package com.edugest.web.rest;

import com.edugest.domain.SchoolClass;
import com.edugest.repository.SchoolClassRepository;
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
 * REST controller for managing {@link com.edugest.domain.SchoolClass}.
 */
@RestController
@RequestMapping("/api/school-classes")
@Transactional
public class SchoolClassResource {

    private static final Logger LOG = LoggerFactory.getLogger(SchoolClassResource.class);

    private static final String ENTITY_NAME = "schoolClass";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SchoolClassRepository schoolClassRepository;

    public SchoolClassResource(SchoolClassRepository schoolClassRepository) {
        this.schoolClassRepository = schoolClassRepository;
    }

    /**
     * {@code POST  /school-classes} : Create a new schoolClass.
     *
     * @param schoolClass the schoolClass to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new schoolClass, or with status {@code 400 (Bad Request)} if the schoolClass has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SchoolClass>> createSchoolClass(@Valid @RequestBody SchoolClass schoolClass) throws URISyntaxException {
        LOG.debug("REST request to save SchoolClass : {}", schoolClass);
        if (schoolClass.getId() != null) {
            throw new BadRequestAlertException("A new schoolClass cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return schoolClassRepository
            .save(schoolClass)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/school-classes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /school-classes/:id} : Updates an existing schoolClass.
     *
     * @param id the id of the schoolClass to save.
     * @param schoolClass the schoolClass to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated schoolClass,
     * or with status {@code 400 (Bad Request)} if the schoolClass is not valid,
     * or with status {@code 500 (Internal Server Error)} if the schoolClass couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SchoolClass>> updateSchoolClass(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SchoolClass schoolClass
    ) throws URISyntaxException {
        LOG.debug("REST request to update SchoolClass : {}, {}", id, schoolClass);
        if (schoolClass.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, schoolClass.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return schoolClassRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return schoolClassRepository
                    .save(schoolClass)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /school-classes/:id} : Partial updates given fields of an existing schoolClass, field will ignore if it is null
     *
     * @param id the id of the schoolClass to save.
     * @param schoolClass the schoolClass to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated schoolClass,
     * or with status {@code 400 (Bad Request)} if the schoolClass is not valid,
     * or with status {@code 404 (Not Found)} if the schoolClass is not found,
     * or with status {@code 500 (Internal Server Error)} if the schoolClass couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SchoolClass>> partialUpdateSchoolClass(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SchoolClass schoolClass
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SchoolClass partially : {}, {}", id, schoolClass);
        if (schoolClass.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, schoolClass.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return schoolClassRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SchoolClass> result = schoolClassRepository
                    .findById(schoolClass.getId())
                    .map(existingSchoolClass -> {
                        if (schoolClass.getName() != null) {
                            existingSchoolClass.setName(schoolClass.getName());
                        }
                        if (schoolClass.getYear() != null) {
                            existingSchoolClass.setYear(schoolClass.getYear());
                        }

                        return existingSchoolClass;
                    })
                    .flatMap(schoolClassRepository::save);

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
     * {@code GET  /school-classes} : get all the schoolClasses.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of schoolClasses in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<SchoolClass>> getAllSchoolClasses(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SchoolClasses");
        if (eagerload) {
            return schoolClassRepository.findAllWithEagerRelationships().collectList();
        } else {
            return schoolClassRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /school-classes} : get all the schoolClasses as a stream.
     * @return the {@link Flux} of schoolClasses.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SchoolClass> getAllSchoolClassesAsStream() {
        LOG.debug("REST request to get all SchoolClasses as a stream");
        return schoolClassRepository.findAll();
    }

    /**
     * {@code GET  /school-classes/:id} : get the "id" schoolClass.
     *
     * @param id the id of the schoolClass to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the schoolClass, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SchoolClass>> getSchoolClass(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SchoolClass : {}", id);
        Mono<SchoolClass> schoolClass = schoolClassRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(schoolClass);
    }

    /**
     * {@code DELETE  /school-classes/:id} : delete the "id" schoolClass.
     *
     * @param id the id of the schoolClass to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSchoolClass(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SchoolClass : {}", id);
        return schoolClassRepository
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
