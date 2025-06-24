package com.edugest.web.rest;

import com.edugest.domain.Timetable;
import com.edugest.repository.TimetableRepository;
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
 * REST controller for managing {@link com.edugest.domain.Timetable}.
 */
@RestController
@RequestMapping("/api/timetables")
@Transactional
public class TimetableResource {

    private static final Logger LOG = LoggerFactory.getLogger(TimetableResource.class);

    private static final String ENTITY_NAME = "timetable";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimetableRepository timetableRepository;

    public TimetableResource(TimetableRepository timetableRepository) {
        this.timetableRepository = timetableRepository;
    }

    /**
     * {@code POST  /timetables} : Create a new timetable.
     *
     * @param timetable the timetable to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timetable, or with status {@code 400 (Bad Request)} if the timetable has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Timetable>> createTimetable(@RequestBody Timetable timetable) throws URISyntaxException {
        LOG.debug("REST request to save Timetable : {}", timetable);
        if (timetable.getId() != null) {
            throw new BadRequestAlertException("A new timetable cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return timetableRepository
            .save(timetable)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/timetables/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /timetables/:id} : Updates an existing timetable.
     *
     * @param id the id of the timetable to save.
     * @param timetable the timetable to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timetable,
     * or with status {@code 400 (Bad Request)} if the timetable is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timetable couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Timetable>> updateTimetable(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Timetable timetable
    ) throws URISyntaxException {
        LOG.debug("REST request to update Timetable : {}, {}", id, timetable);
        if (timetable.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timetable.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return timetableRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return timetableRepository
                    .save(timetable)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /timetables/:id} : Partial updates given fields of an existing timetable, field will ignore if it is null
     *
     * @param id the id of the timetable to save.
     * @param timetable the timetable to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timetable,
     * or with status {@code 400 (Bad Request)} if the timetable is not valid,
     * or with status {@code 404 (Not Found)} if the timetable is not found,
     * or with status {@code 500 (Internal Server Error)} if the timetable couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Timetable>> partialUpdateTimetable(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Timetable timetable
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Timetable partially : {}, {}", id, timetable);
        if (timetable.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timetable.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return timetableRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Timetable> result = timetableRepository
                    .findById(timetable.getId())
                    .map(existingTimetable -> {
                        if (timetable.getSemestre() != null) {
                            existingTimetable.setSemestre(timetable.getSemestre());
                        }

                        return existingTimetable;
                    })
                    .flatMap(timetableRepository::save);

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
     * {@code GET  /timetables} : get all the timetables.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of timetables in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Timetable>> getAllTimetables(@RequestParam(name = "filter", required = false) String filter) {
        if ("schoolclass-is-null".equals(filter)) {
            LOG.debug("REST request to get all Timetables where schoolClass is null");
            return timetableRepository.findAllWhereSchoolClassIsNull().collectList();
        }
        LOG.debug("REST request to get all Timetables");
        return timetableRepository.findAll().collectList();
    }

    /**
     * {@code GET  /timetables} : get all the timetables as a stream.
     * @return the {@link Flux} of timetables.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Timetable> getAllTimetablesAsStream() {
        LOG.debug("REST request to get all Timetables as a stream");
        return timetableRepository.findAll();
    }

    /**
     * {@code GET  /timetables/:id} : get the "id" timetable.
     *
     * @param id the id of the timetable to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timetable, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Timetable>> getTimetable(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Timetable : {}", id);
        Mono<Timetable> timetable = timetableRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(timetable);
    }

    /**
     * {@code DELETE  /timetables/:id} : delete the "id" timetable.
     *
     * @param id the id of the timetable to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTimetable(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Timetable : {}", id);
        return timetableRepository
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
