package com.edugest.web.rest;

import com.edugest.domain.Absence;
import com.edugest.repository.AbsenceRepository;
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
 * REST controller for managing {@link com.edugest.domain.Absence}.
 */
@RestController
@RequestMapping("/api/absences")
@Transactional
public class AbsenceResource {

    private static final Logger LOG = LoggerFactory.getLogger(AbsenceResource.class);

    private static final String ENTITY_NAME = "absence";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AbsenceRepository absenceRepository;

    public AbsenceResource(AbsenceRepository absenceRepository) {
        this.absenceRepository = absenceRepository;
    }

    /**
     * {@code POST  /absences} : Create a new absence.
     *
     * @param absence the absence to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new absence, or with status {@code 400 (Bad Request)} if the absence has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Absence>> createAbsence(@RequestBody Absence absence) throws URISyntaxException {
        LOG.debug("REST request to save Absence : {}", absence);
        if (absence.getId() != null) {
            throw new BadRequestAlertException("A new absence cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return absenceRepository
            .save(absence)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/absences/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /absences/:id} : Updates an existing absence.
     *
     * @param id the id of the absence to save.
     * @param absence the absence to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated absence,
     * or with status {@code 400 (Bad Request)} if the absence is not valid,
     * or with status {@code 500 (Internal Server Error)} if the absence couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Absence>> updateAbsence(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Absence absence
    ) throws URISyntaxException {
        LOG.debug("REST request to update Absence : {}, {}", id, absence);
        if (absence.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, absence.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return absenceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return absenceRepository
                    .save(absence)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /absences/:id} : Partial updates given fields of an existing absence, field will ignore if it is null
     *
     * @param id the id of the absence to save.
     * @param absence the absence to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated absence,
     * or with status {@code 400 (Bad Request)} if the absence is not valid,
     * or with status {@code 404 (Not Found)} if the absence is not found,
     * or with status {@code 500 (Internal Server Error)} if the absence couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Absence>> partialUpdateAbsence(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Absence absence
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Absence partially : {}, {}", id, absence);
        if (absence.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, absence.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return absenceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Absence> result = absenceRepository
                    .findById(absence.getId())
                    .map(existingAbsence -> {
                        if (absence.getDate() != null) {
                            existingAbsence.setDate(absence.getDate());
                        }
                        if (absence.getJustification() != null) {
                            existingAbsence.setJustification(absence.getJustification());
                        }
                        if (absence.getNote() != null) {
                            existingAbsence.setNote(absence.getNote());
                        }

                        return existingAbsence;
                    })
                    .flatMap(absenceRepository::save);

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
     * {@code GET  /absences} : get all the absences.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of absences in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Absence>> getAllAbsences() {
        LOG.debug("REST request to get all Absences");
        return absenceRepository.findAll().collectList();
    }

    /**
     * {@code GET  /absences} : get all the absences as a stream.
     * @return the {@link Flux} of absences.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Absence> getAllAbsencesAsStream() {
        LOG.debug("REST request to get all Absences as a stream");
        return absenceRepository.findAll();
    }

    /**
     * {@code GET  /absences/:id} : get the "id" absence.
     *
     * @param id the id of the absence to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the absence, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Absence>> getAbsence(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Absence : {}", id);
        Mono<Absence> absence = absenceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(absence);
    }

    /**
     * {@code DELETE  /absences/:id} : delete the "id" absence.
     *
     * @param id the id of the absence to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAbsence(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Absence : {}", id);
        return absenceRepository
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
