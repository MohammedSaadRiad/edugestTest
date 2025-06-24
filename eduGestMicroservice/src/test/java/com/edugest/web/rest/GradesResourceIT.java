package com.edugest.web.rest;

import static com.edugest.domain.GradesAsserts.*;
import static com.edugest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.edugest.IntegrationTest;
import com.edugest.domain.Grades;
import com.edugest.repository.EntityManager;
import com.edugest.repository.GradesRepository;
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
 * Integration tests for the {@link GradesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class GradesResourceIT {

    private static final Double DEFAULT_GRADE = 1D;
    private static final Double UPDATED_GRADE = 2D;

    private static final String ENTITY_API_URL = "/api/grades";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GradesRepository gradesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Grades grades;

    private Grades insertedGrades;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Grades createEntity() {
        return new Grades().grade(DEFAULT_GRADE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Grades createUpdatedEntity() {
        return new Grades().grade(UPDATED_GRADE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Grades.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        grades = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedGrades != null) {
            gradesRepository.delete(insertedGrades).block();
            insertedGrades = null;
        }
        deleteEntities(em);
    }

    @Test
    void createGrades() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Grades
        var returnedGrades = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(grades))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Grades.class)
            .returnResult()
            .getResponseBody();

        // Validate the Grades in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertGradesUpdatableFieldsEquals(returnedGrades, getPersistedGrades(returnedGrades));

        insertedGrades = returnedGrades;
    }

    @Test
    void createGradesWithExistingId() throws Exception {
        // Create the Grades with an existing ID
        grades.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(grades))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Grades in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllGradesAsStream() {
        // Initialize the database
        gradesRepository.save(grades).block();

        List<Grades> gradesList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Grades.class)
            .getResponseBody()
            .filter(grades::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(gradesList).isNotNull();
        assertThat(gradesList).hasSize(1);
        Grades testGrades = gradesList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertGradesAllPropertiesEquals(grades, testGrades);
        assertGradesUpdatableFieldsEquals(grades, testGrades);
    }

    @Test
    void getAllGrades() {
        // Initialize the database
        insertedGrades = gradesRepository.save(grades).block();

        // Get all the gradesList
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
            .value(hasItem(grades.getId().intValue()))
            .jsonPath("$.[*].grade")
            .value(hasItem(DEFAULT_GRADE));
    }

    @Test
    void getGrades() {
        // Initialize the database
        insertedGrades = gradesRepository.save(grades).block();

        // Get the grades
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, grades.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(grades.getId().intValue()))
            .jsonPath("$.grade")
            .value(is(DEFAULT_GRADE));
    }

    @Test
    void getNonExistingGrades() {
        // Get the grades
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingGrades() throws Exception {
        // Initialize the database
        insertedGrades = gradesRepository.save(grades).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the grades
        Grades updatedGrades = gradesRepository.findById(grades.getId()).block();
        updatedGrades.grade(UPDATED_GRADE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedGrades.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedGrades))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Grades in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGradesToMatchAllProperties(updatedGrades);
    }

    @Test
    void putNonExistingGrades() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grades.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, grades.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(grades))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Grades in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchGrades() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grades.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(grades))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Grades in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamGrades() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grades.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(grades))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Grades in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateGradesWithPatch() throws Exception {
        // Initialize the database
        insertedGrades = gradesRepository.save(grades).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the grades using partial update
        Grades partialUpdatedGrades = new Grades();
        partialUpdatedGrades.setId(grades.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGrades.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedGrades))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Grades in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGradesUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedGrades, grades), getPersistedGrades(grades));
    }

    @Test
    void fullUpdateGradesWithPatch() throws Exception {
        // Initialize the database
        insertedGrades = gradesRepository.save(grades).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the grades using partial update
        Grades partialUpdatedGrades = new Grades();
        partialUpdatedGrades.setId(grades.getId());

        partialUpdatedGrades.grade(UPDATED_GRADE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGrades.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedGrades))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Grades in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGradesUpdatableFieldsEquals(partialUpdatedGrades, getPersistedGrades(partialUpdatedGrades));
    }

    @Test
    void patchNonExistingGrades() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grades.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, grades.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(grades))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Grades in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchGrades() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grades.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(grades))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Grades in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamGrades() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        grades.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(grades))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Grades in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteGrades() {
        // Initialize the database
        insertedGrades = gradesRepository.save(grades).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the grades
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, grades.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return gradesRepository.count().block();
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

    protected Grades getPersistedGrades(Grades grades) {
        return gradesRepository.findById(grades.getId()).block();
    }

    protected void assertPersistedGradesToMatchAllProperties(Grades expectedGrades) {
        // Test fails because reactive api returns an empty object instead of null
        // assertGradesAllPropertiesEquals(expectedGrades, getPersistedGrades(expectedGrades));
        assertGradesUpdatableFieldsEquals(expectedGrades, getPersistedGrades(expectedGrades));
    }

    protected void assertPersistedGradesToMatchUpdatableProperties(Grades expectedGrades) {
        // Test fails because reactive api returns an empty object instead of null
        // assertGradesAllUpdatablePropertiesEquals(expectedGrades, getPersistedGrades(expectedGrades));
        assertGradesUpdatableFieldsEquals(expectedGrades, getPersistedGrades(expectedGrades));
    }
}
