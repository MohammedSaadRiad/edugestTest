package com.edugest.web.rest;

import static com.edugest.domain.AbsenceAsserts.*;
import static com.edugest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.edugest.IntegrationTest;
import com.edugest.domain.Absence;
import com.edugest.repository.AbsenceRepository;
import com.edugest.repository.EntityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link AbsenceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AbsenceResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_JUSTIFICATION = "AAAAAAAAAA";
    private static final String UPDATED_JUSTIFICATION = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/absences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Absence absence;

    private Absence insertedAbsence;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Absence createEntity() {
        return new Absence().date(DEFAULT_DATE).justification(DEFAULT_JUSTIFICATION).note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Absence createUpdatedEntity() {
        return new Absence().date(UPDATED_DATE).justification(UPDATED_JUSTIFICATION).note(UPDATED_NOTE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Absence.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        absence = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAbsence != null) {
            absenceRepository.delete(insertedAbsence).block();
            insertedAbsence = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAbsence() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Absence
        var returnedAbsence = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(absence))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Absence.class)
            .returnResult()
            .getResponseBody();

        // Validate the Absence in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAbsenceUpdatableFieldsEquals(returnedAbsence, getPersistedAbsence(returnedAbsence));

        insertedAbsence = returnedAbsence;
    }

    @Test
    void createAbsenceWithExistingId() throws Exception {
        // Create the Absence with an existing ID
        absence.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(absence))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Absence in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAbsencesAsStream() {
        // Initialize the database
        absenceRepository.save(absence).block();

        List<Absence> absenceList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Absence.class)
            .getResponseBody()
            .filter(absence::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(absenceList).isNotNull();
        assertThat(absenceList).hasSize(1);
        Absence testAbsence = absenceList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertAbsenceAllPropertiesEquals(absence, testAbsence);
        assertAbsenceUpdatableFieldsEquals(absence, testAbsence);
    }

    @Test
    void getAllAbsences() {
        // Initialize the database
        insertedAbsence = absenceRepository.save(absence).block();

        // Get all the absenceList
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
            .value(hasItem(absence.getId().intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].justification")
            .value(hasItem(DEFAULT_JUSTIFICATION))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE));
    }

    @Test
    void getAbsence() {
        // Initialize the database
        insertedAbsence = absenceRepository.save(absence).block();

        // Get the absence
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, absence.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(absence.getId().intValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.justification")
            .value(is(DEFAULT_JUSTIFICATION))
            .jsonPath("$.note")
            .value(is(DEFAULT_NOTE));
    }

    @Test
    void getNonExistingAbsence() {
        // Get the absence
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAbsence() throws Exception {
        // Initialize the database
        insertedAbsence = absenceRepository.save(absence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the absence
        Absence updatedAbsence = absenceRepository.findById(absence.getId()).block();
        updatedAbsence.date(UPDATED_DATE).justification(UPDATED_JUSTIFICATION).note(UPDATED_NOTE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAbsence.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedAbsence))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Absence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAbsenceToMatchAllProperties(updatedAbsence);
    }

    @Test
    void putNonExistingAbsence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        absence.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, absence.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(absence))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Absence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAbsence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        absence.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(absence))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Absence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAbsence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        absence.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(absence))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Absence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAbsenceWithPatch() throws Exception {
        // Initialize the database
        insertedAbsence = absenceRepository.save(absence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the absence using partial update
        Absence partialUpdatedAbsence = new Absence();
        partialUpdatedAbsence.setId(absence.getId());

        partialUpdatedAbsence.note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAbsence.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAbsence))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Absence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAbsenceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAbsence, absence), getPersistedAbsence(absence));
    }

    @Test
    void fullUpdateAbsenceWithPatch() throws Exception {
        // Initialize the database
        insertedAbsence = absenceRepository.save(absence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the absence using partial update
        Absence partialUpdatedAbsence = new Absence();
        partialUpdatedAbsence.setId(absence.getId());

        partialUpdatedAbsence.date(UPDATED_DATE).justification(UPDATED_JUSTIFICATION).note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAbsence.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAbsence))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Absence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAbsenceUpdatableFieldsEquals(partialUpdatedAbsence, getPersistedAbsence(partialUpdatedAbsence));
    }

    @Test
    void patchNonExistingAbsence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        absence.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, absence.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(absence))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Absence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAbsence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        absence.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(absence))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Absence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAbsence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        absence.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(absence))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Absence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAbsence() {
        // Initialize the database
        insertedAbsence = absenceRepository.save(absence).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the absence
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, absence.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return absenceRepository.count().block();
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

    protected Absence getPersistedAbsence(Absence absence) {
        return absenceRepository.findById(absence.getId()).block();
    }

    protected void assertPersistedAbsenceToMatchAllProperties(Absence expectedAbsence) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAbsenceAllPropertiesEquals(expectedAbsence, getPersistedAbsence(expectedAbsence));
        assertAbsenceUpdatableFieldsEquals(expectedAbsence, getPersistedAbsence(expectedAbsence));
    }

    protected void assertPersistedAbsenceToMatchUpdatableProperties(Absence expectedAbsence) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAbsenceAllUpdatablePropertiesEquals(expectedAbsence, getPersistedAbsence(expectedAbsence));
        assertAbsenceUpdatableFieldsEquals(expectedAbsence, getPersistedAbsence(expectedAbsence));
    }
}
