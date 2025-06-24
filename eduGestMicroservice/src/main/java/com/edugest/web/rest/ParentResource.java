package com.edugest.web.rest;

import com.edugest.domain.Parent;
import com.edugest.repository.ParentRepository;
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
 * REST controller for managing {@link com.edugest.domain.Parent}.
 */
@RestController
@RequestMapping("/api/parents")
@Transactional
public class ParentResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParentResource.class);

    private static final String ENTITY_NAME = "eduGestMicroserviceParent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParentRepository parentRepository;

    public ParentResource(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    /**
     * {@code POST  /parents} : Create a new parent.
     *
     * @param parent the parent to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new parent, or with status {@code 400 (Bad Request)} if the parent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Parent>> createParent(@Valid @RequestBody Parent parent) throws URISyntaxException {
        LOG.debug("REST request to save Parent : {}", parent);
        if (parent.getId() != null) {
            throw new BadRequestAlertException("A new parent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return parentRepository
            .save(parent)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/parents/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /parents/:id} : Updates an existing parent.
     *
     * @param id the id of the parent to save.
     * @param parent the parent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parent,
     * or with status {@code 400 (Bad Request)} if the parent is not valid,
     * or with status {@code 500 (Internal Server Error)} if the parent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Parent>> updateParent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Parent parent
    ) throws URISyntaxException {
        LOG.debug("REST request to update Parent : {}, {}", id, parent);
        if (parent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return parentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return parentRepository
                    .save(parent)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /parents/:id} : Partial updates given fields of an existing parent, field will ignore if it is null
     *
     * @param id the id of the parent to save.
     * @param parent the parent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parent,
     * or with status {@code 400 (Bad Request)} if the parent is not valid,
     * or with status {@code 404 (Not Found)} if the parent is not found,
     * or with status {@code 500 (Internal Server Error)} if the parent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Parent>> partialUpdateParent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Parent parent
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Parent partially : {}, {}", id, parent);
        if (parent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return parentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Parent> result = parentRepository
                    .findById(parent.getId())
                    .map(existingParent -> {
                        if (parent.getIdentifier() != null) {
                            existingParent.setIdentifier(parent.getIdentifier());
                        }
                        if (parent.getBirthDate() != null) {
                            existingParent.setBirthDate(parent.getBirthDate());
                        }
                        if (parent.getGender() != null) {
                            existingParent.setGender(parent.getGender());
                        }
                        if (parent.getPhoneNumber() != null) {
                            existingParent.setPhoneNumber(parent.getPhoneNumber());
                        }
                        if (parent.getAddress() != null) {
                            existingParent.setAddress(parent.getAddress());
                        }
                        if (parent.getNote() != null) {
                            existingParent.setNote(parent.getNote());
                        }

                        return existingParent;
                    })
                    .flatMap(parentRepository::save);

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
     * {@code GET  /parents} : get all the parents.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of parents in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Parent>> getAllParents() {
        LOG.debug("REST request to get all Parents");
        return parentRepository.findAll().collectList();
    }

    /**
     * {@code GET  /parents} : get all the parents as a stream.
     * @return the {@link Flux} of parents.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Parent> getAllParentsAsStream() {
        LOG.debug("REST request to get all Parents as a stream");
        return parentRepository.findAll();
    }

    /**
     * {@code GET  /parents/:id} : get the "id" parent.
     *
     * @param id the id of the parent to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the parent, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Parent>> getParent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Parent : {}", id);
        Mono<Parent> parent = parentRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(parent);
    }

    /**
     * {@code DELETE  /parents/:id} : delete the "id" parent.
     *
     * @param id the id of the parent to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteParent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Parent : {}", id);
        return parentRepository
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
