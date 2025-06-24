package com.edugest.web.rest;

import com.edugest.domain.Administration;
import com.edugest.repository.AdministrationRepository;
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
 * REST controller for managing {@link com.edugest.domain.Administration}.
 */
@RestController
@RequestMapping("/api/administrations")
@Transactional
public class AdministrationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdministrationResource.class);

    private static final String ENTITY_NAME = "eduGestMicroserviceAdministration";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdministrationRepository administrationRepository;

    public AdministrationResource(AdministrationRepository administrationRepository) {
        this.administrationRepository = administrationRepository;
    }

    /**
     * {@code POST  /administrations} : Create a new administration.
     *
     * @param administration the administration to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new administration, or with status {@code 400 (Bad Request)} if the administration has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Administration>> createAdministration(@Valid @RequestBody Administration administration)
        throws URISyntaxException {
        LOG.debug("REST request to save Administration : {}", administration);
        if (administration.getId() != null) {
            throw new BadRequestAlertException("A new administration cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return administrationRepository
            .save(administration)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/administrations/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /administrations/:id} : Updates an existing administration.
     *
     * @param id the id of the administration to save.
     * @param administration the administration to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated administration,
     * or with status {@code 400 (Bad Request)} if the administration is not valid,
     * or with status {@code 500 (Internal Server Error)} if the administration couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Administration>> updateAdministration(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Administration administration
    ) throws URISyntaxException {
        LOG.debug("REST request to update Administration : {}, {}", id, administration);
        if (administration.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, administration.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return administrationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return administrationRepository
                    .save(administration)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /administrations/:id} : Partial updates given fields of an existing administration, field will ignore if it is null
     *
     * @param id the id of the administration to save.
     * @param administration the administration to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated administration,
     * or with status {@code 400 (Bad Request)} if the administration is not valid,
     * or with status {@code 404 (Not Found)} if the administration is not found,
     * or with status {@code 500 (Internal Server Error)} if the administration couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Administration>> partialUpdateAdministration(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Administration administration
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Administration partially : {}, {}", id, administration);
        if (administration.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, administration.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return administrationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Administration> result = administrationRepository
                    .findById(administration.getId())
                    .map(existingAdministration -> {
                        if (administration.getIdentifier() != null) {
                            existingAdministration.setIdentifier(administration.getIdentifier());
                        }
                        if (administration.getBirthDate() != null) {
                            existingAdministration.setBirthDate(administration.getBirthDate());
                        }
                        if (administration.getGender() != null) {
                            existingAdministration.setGender(administration.getGender());
                        }
                        if (administration.getPhoneNumber() != null) {
                            existingAdministration.setPhoneNumber(administration.getPhoneNumber());
                        }
                        if (administration.getAddress() != null) {
                            existingAdministration.setAddress(administration.getAddress());
                        }
                        if (administration.getType() != null) {
                            existingAdministration.setType(administration.getType());
                        }
                        if (administration.getNote() != null) {
                            existingAdministration.setNote(administration.getNote());
                        }

                        return existingAdministration;
                    })
                    .flatMap(administrationRepository::save);

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
     * {@code GET  /administrations} : get all the administrations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of administrations in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Administration>> getAllAdministrations() {
        LOG.debug("REST request to get all Administrations");
        return administrationRepository.findAll().collectList();
    }

    /**
     * {@code GET  /administrations} : get all the administrations as a stream.
     * @return the {@link Flux} of administrations.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Administration> getAllAdministrationsAsStream() {
        LOG.debug("REST request to get all Administrations as a stream");
        return administrationRepository.findAll();
    }

    /**
     * {@code GET  /administrations/:id} : get the "id" administration.
     *
     * @param id the id of the administration to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the administration, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Administration>> getAdministration(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Administration : {}", id);
        Mono<Administration> administration = administrationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(administration);
    }

    /**
     * {@code DELETE  /administrations/:id} : delete the "id" administration.
     *
     * @param id the id of the administration to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAdministration(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Administration : {}", id);
        return administrationRepository
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
