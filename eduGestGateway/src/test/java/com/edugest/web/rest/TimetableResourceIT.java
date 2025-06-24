package com.edugest.web.rest;

import static com.edugest.domain.TimetableAsserts.*;
import static com.edugest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.edugest.IntegrationTest;
import com.edugest.domain.Timetable;
import com.edugest.repository.EntityManager;
import com.edugest.repository.TimetableRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link TimetableResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TimetableResourceIT {

    private static final Integer DEFAULT_SEMESTRE = 1;
    private static final Integer UPDATED_SEMESTRE = 2;

    private static final String ENTITY_API_URL = "/api/timetables";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Timetable timetable;

    private Timetable insertedTimetable;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Timetable createEntity() {
        return new Timetable().semestre(DEFAULT_SEMESTRE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Timetable createUpdatedEntity() {
        return new Timetable().semestre(UPDATED_SEMESTRE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Timetable.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        timetable = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTimetable != null) {
            timetableRepository.delete(insertedTimetable).block();
            insertedTimetable = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTimetable() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Timetable
        var returnedTimetable = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(timetable))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Timetable.class)
            .returnResult()
            .getResponseBody();

        // Validate the Timetable in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTimetableUpdatableFieldsEquals(returnedTimetable, getPersistedTimetable(returnedTimetable));

        insertedTimetable = returnedTimetable;
    }

    @Test
    void createTimetableWithExistingId() throws Exception {
        // Create the Timetable with an existing ID
        timetable.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(timetable))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Timetable in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTimetablesAsStream() {
        // Initialize the database
        timetableRepository.save(timetable).block();

        List<Timetable> timetableList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Timetable.class)
            .getResponseBody()
            .filter(timetable::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(timetableList).isNotNull();
        assertThat(timetableList).hasSize(1);
        Timetable testTimetable = timetableList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertTimetableAllPropertiesEquals(timetable, testTimetable);
        assertTimetableUpdatableFieldsEquals(timetable, testTimetable);
    }

    @Test
    void getAllTimetables() {
        // Initialize the database
        insertedTimetable = timetableRepository.save(timetable).block();

        // Get all the timetableList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(timetable.getId().intValue()))
            .jsonPath("$.[*].semestre")
            .value(hasItem(DEFAULT_SEMESTRE));
    }

    @Test
    void getTimetable() {
        // Initialize the database
        insertedTimetable = timetableRepository.save(timetable).block();

        // Get the timetable
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, timetable.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(timetable.getId().intValue()))
            .jsonPath("$.semestre")
            .value(is(DEFAULT_SEMESTRE));
    }

    @Test
    void getNonExistingTimetable() {
        // Get the timetable
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTimetable() throws Exception {
        // Initialize the database
        insertedTimetable = timetableRepository.save(timetable).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timetable
        Timetable updatedTimetable = timetableRepository.findById(timetable.getId()).block();
        updatedTimetable.semestre(UPDATED_SEMESTRE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTimetable.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedTimetable))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Timetable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTimetableToMatchAllProperties(updatedTimetable);
    }

    @Test
    void putNonExistingTimetable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetable.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, timetable.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(timetable))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Timetable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTimetable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetable.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(timetable))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Timetable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTimetable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetable.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(timetable))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Timetable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTimetableWithPatch() throws Exception {
        // Initialize the database
        insertedTimetable = timetableRepository.save(timetable).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timetable using partial update
        Timetable partialUpdatedTimetable = new Timetable();
        partialUpdatedTimetable.setId(timetable.getId());

        partialUpdatedTimetable.semestre(UPDATED_SEMESTRE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTimetable.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTimetable))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Timetable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTimetableUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTimetable, timetable),
            getPersistedTimetable(timetable)
        );
    }

    @Test
    void fullUpdateTimetableWithPatch() throws Exception {
        // Initialize the database
        insertedTimetable = timetableRepository.save(timetable).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the timetable using partial update
        Timetable partialUpdatedTimetable = new Timetable();
        partialUpdatedTimetable.setId(timetable.getId());

        partialUpdatedTimetable.semestre(UPDATED_SEMESTRE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTimetable.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTimetable))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Timetable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTimetableUpdatableFieldsEquals(partialUpdatedTimetable, getPersistedTimetable(partialUpdatedTimetable));
    }

    @Test
    void patchNonExistingTimetable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetable.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, timetable.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(timetable))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Timetable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTimetable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetable.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(timetable))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Timetable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTimetable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        timetable.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(timetable))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Timetable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTimetable() {
        // Initialize the database
        insertedTimetable = timetableRepository.save(timetable).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the timetable
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, timetable.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return timetableRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Timetable getPersistedTimetable(Timetable timetable) {
        return timetableRepository.findById(timetable.getId()).block();
    }

    protected void assertPersistedTimetableToMatchAllProperties(Timetable expectedTimetable) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTimetableAllPropertiesEquals(expectedTimetable, getPersistedTimetable(expectedTimetable));
        assertTimetableUpdatableFieldsEquals(expectedTimetable, getPersistedTimetable(expectedTimetable));
    }

    protected void assertPersistedTimetableToMatchUpdatableProperties(Timetable expectedTimetable) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTimetableAllUpdatablePropertiesEquals(expectedTimetable, getPersistedTimetable(expectedTimetable));
        assertTimetableUpdatableFieldsEquals(expectedTimetable, getPersistedTimetable(expectedTimetable));
    }
}
