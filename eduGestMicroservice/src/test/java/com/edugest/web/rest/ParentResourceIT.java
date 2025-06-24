package com.edugest.web.rest;

import static com.edugest.domain.ParentAsserts.*;
import static com.edugest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.edugest.IntegrationTest;
import com.edugest.domain.Parent;
import com.edugest.domain.enumeration.Genders;
import com.edugest.repository.EntityManager;
import com.edugest.repository.ParentRepository;
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
 * Integration tests for the {@link ParentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ParentResourceIT {

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

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/parents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Parent parent;

    private Parent insertedParent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parent createEntity() {
        return new Parent()
            .identifier(DEFAULT_IDENTIFIER)
            .birthDate(DEFAULT_BIRTH_DATE)
            .gender(DEFAULT_GENDER)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .address(DEFAULT_ADDRESS)
            .note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parent createUpdatedEntity() {
        return new Parent()
            .identifier(UPDATED_IDENTIFIER)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .note(UPDATED_NOTE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Parent.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        parent = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedParent != null) {
            parentRepository.delete(insertedParent).block();
            insertedParent = null;
        }
        deleteEntities(em);
    }

    @Test
    void createParent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Parent
        var returnedParent = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(parent))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Parent.class)
            .returnResult()
            .getResponseBody();

        // Validate the Parent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertParentUpdatableFieldsEquals(returnedParent, getPersistedParent(returnedParent));

        insertedParent = returnedParent;
    }

    @Test
    void createParentWithExistingId() throws Exception {
        // Create the Parent with an existing ID
        parent.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(parent))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkIdentifierIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        parent.setIdentifier(null);

        // Create the Parent, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(parent))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllParentsAsStream() {
        // Initialize the database
        parentRepository.save(parent).block();

        List<Parent> parentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Parent.class)
            .getResponseBody()
            .filter(parent::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(parentList).isNotNull();
        assertThat(parentList).hasSize(1);
        Parent testParent = parentList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertParentAllPropertiesEquals(parent, testParent);
        assertParentUpdatableFieldsEquals(parent, testParent);
    }

    @Test
    void getAllParents() {
        // Initialize the database
        insertedParent = parentRepository.save(parent).block();

        // Get all the parentList
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
            .value(hasItem(parent.getId().intValue()))
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
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE));
    }

    @Test
    void getParent() {
        // Initialize the database
        insertedParent = parentRepository.save(parent).block();

        // Get the parent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, parent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(parent.getId().intValue()))
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
            .jsonPath("$.note")
            .value(is(DEFAULT_NOTE));
    }

    @Test
    void getNonExistingParent() {
        // Get the parent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingParent() throws Exception {
        // Initialize the database
        insertedParent = parentRepository.save(parent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parent
        Parent updatedParent = parentRepository.findById(parent.getId()).block();
        updatedParent
            .identifier(UPDATED_IDENTIFIER)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .note(UPDATED_NOTE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedParent.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedParent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Parent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParentToMatchAllProperties(updatedParent);
    }

    @Test
    void putNonExistingParent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, parent.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(parent))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchParent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(parent))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamParent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(parent))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Parent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateParentWithPatch() throws Exception {
        // Initialize the database
        insertedParent = parentRepository.save(parent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parent using partial update
        Parent partialUpdatedParent = new Parent();
        partialUpdatedParent.setId(parent.getId());

        partialUpdatedParent
            .identifier(UPDATED_IDENTIFIER)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedParent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Parent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedParent, parent), getPersistedParent(parent));
    }

    @Test
    void fullUpdateParentWithPatch() throws Exception {
        // Initialize the database
        insertedParent = parentRepository.save(parent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parent using partial update
        Parent partialUpdatedParent = new Parent();
        partialUpdatedParent.setId(parent.getId());

        partialUpdatedParent
            .identifier(UPDATED_IDENTIFIER)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedParent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Parent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParentUpdatableFieldsEquals(partialUpdatedParent, getPersistedParent(partialUpdatedParent));
    }

    @Test
    void patchNonExistingParent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, parent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(parent))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchParent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(parent))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamParent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(parent))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Parent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteParent() {
        // Initialize the database
        insertedParent = parentRepository.save(parent).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the parent
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, parent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return parentRepository.count().block();
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

    protected Parent getPersistedParent(Parent parent) {
        return parentRepository.findById(parent.getId()).block();
    }

    protected void assertPersistedParentToMatchAllProperties(Parent expectedParent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertParentAllPropertiesEquals(expectedParent, getPersistedParent(expectedParent));
        assertParentUpdatableFieldsEquals(expectedParent, getPersistedParent(expectedParent));
    }

    protected void assertPersistedParentToMatchUpdatableProperties(Parent expectedParent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertParentAllUpdatablePropertiesEquals(expectedParent, getPersistedParent(expectedParent));
        assertParentUpdatableFieldsEquals(expectedParent, getPersistedParent(expectedParent));
    }
}
