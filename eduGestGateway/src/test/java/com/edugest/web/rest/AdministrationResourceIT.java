package com.edugest.web.rest;

import static com.edugest.domain.AdministrationAsserts.*;
import static com.edugest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.edugest.IntegrationTest;
import com.edugest.domain.Administration;
import com.edugest.domain.enumeration.Genders;
import com.edugest.repository.AdministrationRepository;
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
 * Integration tests for the {@link AdministrationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AdministrationResourceIT {

    private static final String DEFAULT_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFIER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Genders DEFAULT_GENDER = Genders.MALE;
    private static final Genders UPDATED_GENDER = Genders.FEMALE;

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/administrations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AdministrationRepository administrationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Administration administration;

    private Administration insertedAdministration;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Administration createEntity() {
        return new Administration()
            .identifier(DEFAULT_IDENTIFIER)
            .birthDate(DEFAULT_BIRTH_DATE)
            .gender(DEFAULT_GENDER)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .address(DEFAULT_ADDRESS)
            .type(DEFAULT_TYPE)
            .note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Administration createUpdatedEntity() {
        return new Administration()
            .identifier(UPDATED_IDENTIFIER)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .type(UPDATED_TYPE)
            .note(UPDATED_NOTE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Administration.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        administration = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAdministration != null) {
            administrationRepository.delete(insertedAdministration).block();
            insertedAdministration = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAdministration() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Administration
        var returnedAdministration = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(administration))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Administration.class)
            .returnResult()
            .getResponseBody();

        // Validate the Administration in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAdministrationUpdatableFieldsEquals(returnedAdministration, getPersistedAdministration(returnedAdministration));

        insertedAdministration = returnedAdministration;
    }

    @Test
    void createAdministrationWithExistingId() throws Exception {
        // Create the Administration with an existing ID
        administration.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(administration))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Administration in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkIdentifierIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        administration.setIdentifier(null);

        // Create the Administration, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(administration))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAdministrationsAsStream() {
        // Initialize the database
        administrationRepository.save(administration).block();

        List<Administration> administrationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Administration.class)
            .getResponseBody()
            .filter(administration::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(administrationList).isNotNull();
        assertThat(administrationList).hasSize(1);
        Administration testAdministration = administrationList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertAdministrationAllPropertiesEquals(administration, testAdministration);
        assertAdministrationUpdatableFieldsEquals(administration, testAdministration);
    }

    @Test
    void getAllAdministrations() {
        // Initialize the database
        insertedAdministration = administrationRepository.save(administration).block();

        // Get all the administrationList
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
            .value(hasItem(administration.getId().intValue()))
            .jsonPath("$.[*].identifier")
            .value(hasItem(DEFAULT_IDENTIFIER))
            .jsonPath("$.[*].birthDate")
            .value(hasItem(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE));
    }

    @Test
    void getAdministration() {
        // Initialize the database
        insertedAdministration = administrationRepository.save(administration).block();

        // Get the administration
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, administration.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(administration.getId().intValue()))
            .jsonPath("$.identifier")
            .value(is(DEFAULT_IDENTIFIER))
            .jsonPath("$.birthDate")
            .value(is(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.gender")
            .value(is(DEFAULT_GENDER.toString()))
            .jsonPath("$.phoneNumber")
            .value(is(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE))
            .jsonPath("$.note")
            .value(is(DEFAULT_NOTE));
    }

    @Test
    void getNonExistingAdministration() {
        // Get the administration
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAdministration() throws Exception {
        // Initialize the database
        insertedAdministration = administrationRepository.save(administration).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the administration
        Administration updatedAdministration = administrationRepository.findById(administration.getId()).block();
        updatedAdministration
            .identifier(UPDATED_IDENTIFIER)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .type(UPDATED_TYPE)
            .note(UPDATED_NOTE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAdministration.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedAdministration))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Administration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAdministrationToMatchAllProperties(updatedAdministration);
    }

    @Test
    void putNonExistingAdministration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        administration.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, administration.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(administration))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Administration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAdministration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        administration.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(administration))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Administration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAdministration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        administration.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(administration))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Administration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAdministrationWithPatch() throws Exception {
        // Initialize the database
        insertedAdministration = administrationRepository.save(administration).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the administration using partial update
        Administration partialUpdatedAdministration = new Administration();
        partialUpdatedAdministration.setId(administration.getId());

        partialUpdatedAdministration.identifier(UPDATED_IDENTIFIER).phoneNumber(UPDATED_PHONE_NUMBER).note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAdministration.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAdministration))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Administration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAdministrationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAdministration, administration),
            getPersistedAdministration(administration)
        );
    }

    @Test
    void fullUpdateAdministrationWithPatch() throws Exception {
        // Initialize the database
        insertedAdministration = administrationRepository.save(administration).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the administration using partial update
        Administration partialUpdatedAdministration = new Administration();
        partialUpdatedAdministration.setId(administration.getId());

        partialUpdatedAdministration
            .identifier(UPDATED_IDENTIFIER)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .type(UPDATED_TYPE)
            .note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAdministration.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAdministration))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Administration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAdministrationUpdatableFieldsEquals(partialUpdatedAdministration, getPersistedAdministration(partialUpdatedAdministration));
    }

    @Test
    void patchNonExistingAdministration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        administration.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, administration.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(administration))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Administration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAdministration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        administration.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(administration))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Administration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAdministration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        administration.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(administration))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Administration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAdministration() {
        // Initialize the database
        insertedAdministration = administrationRepository.save(administration).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the administration
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, administration.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return administrationRepository.count().block();
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

    protected Administration getPersistedAdministration(Administration administration) {
        return administrationRepository.findById(administration.getId()).block();
    }

    protected void assertPersistedAdministrationToMatchAllProperties(Administration expectedAdministration) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAdministrationAllPropertiesEquals(expectedAdministration, getPersistedAdministration(expectedAdministration));
        assertAdministrationUpdatableFieldsEquals(expectedAdministration, getPersistedAdministration(expectedAdministration));
    }

    protected void assertPersistedAdministrationToMatchUpdatableProperties(Administration expectedAdministration) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAdministrationAllUpdatablePropertiesEquals(expectedAdministration, getPersistedAdministration(expectedAdministration));
        assertAdministrationUpdatableFieldsEquals(expectedAdministration, getPersistedAdministration(expectedAdministration));
    }
}
