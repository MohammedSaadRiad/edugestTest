package com.edugest.web.rest;

import com.edugest.domain.Teacher;
import com.edugest.repository.TeacherRepository;
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
 * REST controller for managing {@link com.edugest.domain.Teacher}.
 */
@RestController
@RequestMapping("/api/teachers")
@Transactional
public class TeacherResource {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherResource.class);

    private static final String ENTITY_NAME = "teacher";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeacherRepository teacherRepository;

    public TeacherResource(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    /**
     * {@code POST  /teachers} : Create a new teacher.
     *
     * @param teacher the teacher to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teacher, or with status {@code 400 (Bad Request)} if the teacher has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Teacher>> createTeacher(@Valid @RequestBody Teacher teacher) throws URISyntaxException {
        LOG.debug("REST request to save Teacher : {}", teacher);
        if (teacher.getId() != null) {
            throw new BadRequestAlertException("A new teacher cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return teacherRepository
            .save(teacher)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/teachers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /teachers/:id} : Updates an existing teacher.
     *
     * @param id the id of the teacher to save.
     * @param teacher the teacher to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacher,
     * or with status {@code 400 (Bad Request)} if the teacher is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teacher couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Teacher>> updateTeacher(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Teacher teacher
    ) throws URISyntaxException {
        LOG.debug("REST request to update Teacher : {}, {}", id, teacher);
        if (teacher.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacher.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return teacherRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return teacherRepository
                    .save(teacher)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /teachers/:id} : Partial updates given fields of an existing teacher, field will ignore if it is null
     *
     * @param id the id of the teacher to save.
     * @param teacher the teacher to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacher,
     * or with status {@code 400 (Bad Request)} if the teacher is not valid,
     * or with status {@code 404 (Not Found)} if the teacher is not found,
     * or with status {@code 500 (Internal Server Error)} if the teacher couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Teacher>> partialUpdateTeacher(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Teacher teacher
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Teacher partially : {}, {}", id, teacher);
        if (teacher.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacher.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return teacherRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Teacher> result = teacherRepository
                    .findById(teacher.getId())
                    .map(existingTeacher -> {
                        if (teacher.getIdentifier() != null) {
                            existingTeacher.setIdentifier(teacher.getIdentifier());
                        }
                        if (teacher.getBirthDate() != null) {
                            existingTeacher.setBirthDate(teacher.getBirthDate());
                        }
                        if (teacher.getQualification() != null) {
                            existingTeacher.setQualification(teacher.getQualification());
                        }
                        if (teacher.getGender() != null) {
                            existingTeacher.setGender(teacher.getGender());
                        }
                        if (teacher.getExperience() != null) {
                            existingTeacher.setExperience(teacher.getExperience());
                        }
                        if (teacher.getPhoneNumber() != null) {
                            existingTeacher.setPhoneNumber(teacher.getPhoneNumber());
                        }
                        if (teacher.getAddress() != null) {
                            existingTeacher.setAddress(teacher.getAddress());
                        }
                        if (teacher.getType() != null) {
                            existingTeacher.setType(teacher.getType());
                        }
                        if (teacher.getNote() != null) {
                            existingTeacher.setNote(teacher.getNote());
                        }

                        return existingTeacher;
                    })
                    .flatMap(teacherRepository::save);

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
     * {@code GET  /teachers} : get all the teachers.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teachers in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Teacher>> getAllTeachers(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Teachers");
        if (eagerload) {
            return teacherRepository.findAllWithEagerRelationships().collectList();
        } else {
            return teacherRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /teachers} : get all the teachers as a stream.
     * @return the {@link Flux} of teachers.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Teacher> getAllTeachersAsStream() {
        LOG.debug("REST request to get all Teachers as a stream");
        return teacherRepository.findAll();
    }

    /**
     * {@code GET  /teachers/:id} : get the "id" teacher.
     *
     * @param id the id of the teacher to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teacher, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Teacher>> getTeacher(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Teacher : {}", id);
        Mono<Teacher> teacher = teacherRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(teacher);
    }

    /**
     * {@code DELETE  /teachers/:id} : delete the "id" teacher.
     *
     * @param id the id of the teacher to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTeacher(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Teacher : {}", id);
        return teacherRepository
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
