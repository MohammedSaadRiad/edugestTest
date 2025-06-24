package com.edugest.web.rest;

import static com.edugest.domain.SchoolClassAsserts.*;
import static com.edugest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.edugest.IntegrationTest;
import com.edugest.domain.SchoolClass;
import com.edugest.repository.EntityManager;
import com.edugest.repository.SchoolClassRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link SchoolClassResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SchoolClassResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    private static final String ENTITY_API_URL = "/api/school-classes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private SchoolClassRepository schoolClassRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private SchoolClass schoolClass;

    private SchoolClass insertedSchoolClass;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SchoolClass createEntity() {
        return new SchoolClass().name(DEFAULT_NAME).year(DEFAULT_YEAR);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SchoolClass createUpdatedEntity() {
        return new SchoolClass().name(UPDATED_NAME).year(UPDATED_YEAR);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_school_class__teachers").block();
            em.deleteAll(SchoolClass.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        schoolClass = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSchoolClass != null) {
            schoolClassRepository.delete(insertedSchoolClass).block();
            insertedSchoolClass = null;
        }
        deleteEntities(em);
    }

    @Test
    void createSchoolClass() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SchoolClass
        var returnedSchoolClass = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(schoolClass))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SchoolClass.class)
            .returnResult()
            .getResponseBody();

        // Validate the SchoolClass in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSchoolClassUpdatableFieldsEquals(returnedSchoolClass, getPersistedSchoolClass(returnedSchoolClass));

        insertedSchoolClass = returnedSchoolClass;
    }

    @Test
    void createSchoolClassWithExistingId() throws Exception {
        // Create the SchoolClass with an existing ID
        schoolClass.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(schoolClass))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SchoolClass in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        schoolClass.setName(null);

        // Create the SchoolClass, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(schoolClass))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSchoolClassesAsStream() {
        // Initialize the database
        schoolClassRepository.save(schoolClass).block();

        List<SchoolClass> schoolClassList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(SchoolClass.class)
            .getResponseBody()
            .filter(schoolClass::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(schoolClassList).isNotNull();
        assertThat(schoolClassList).hasSize(1);
        SchoolClass testSchoolClass = schoolClassList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertSchoolClassAllPropertiesEquals(schoolClass, testSchoolClass);
        assertSchoolClassUpdatableFieldsEquals(schoolClass, testSchoolClass);
    }

    @Test
    void getAllSchoolClasses() {
        // Initialize the database
        insertedSchoolClass = schoolClassRepository.save(schoolClass).block();

        // Get all the schoolClassList
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
            .value(hasItem(schoolClass.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].year")
            .value(hasItem(DEFAULT_YEAR));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSchoolClassesWithEagerRelationshipsIsEnabled() {
        when(schoolClassRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(schoolClassRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSchoolClassesWithEagerRelationshipsIsNotEnabled() {
        when(schoolClassRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(schoolClassRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSchoolClass() {
        // Initialize the database
        insertedSchoolClass = schoolClassRepository.save(schoolClass).block();

        // Get the schoolClass
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, schoolClass.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(schoolClass.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.year")
            .value(is(DEFAULT_YEAR));
    }

    @Test
    void getNonExistingSchoolClass() {
        // Get the schoolClass
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSchoolClass() throws Exception {
        // Initialize the database
        insertedSchoolClass = schoolClassRepository.save(schoolClass).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the schoolClass
        SchoolClass updatedSchoolClass = schoolClassRepository.findById(schoolClass.getId()).block();
        updatedSchoolClass.name(UPDATED_NAME).year(UPDATED_YEAR);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSchoolClass.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedSchoolClass))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SchoolClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSchoolClassToMatchAllProperties(updatedSchoolClass);
    }

    @Test
    void putNonExistingSchoolClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schoolClass.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, schoolClass.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(schoolClass))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SchoolClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSchoolClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schoolClass.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(schoolClass))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SchoolClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSchoolClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schoolClass.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(schoolClass))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SchoolClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSchoolClassWithPatch() throws Exception {
        // Initialize the database
        insertedSchoolClass = schoolClassRepository.save(schoolClass).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the schoolClass using partial update
        SchoolClass partialUpdatedSchoolClass = new SchoolClass();
        partialUpdatedSchoolClass.setId(schoolClass.getId());

        partialUpdatedSchoolClass.year(UPDATED_YEAR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSchoolClass.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSchoolClass))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SchoolClass in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSchoolClassUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSchoolClass, schoolClass),
            getPersistedSchoolClass(schoolClass)
        );
    }

    @Test
    void fullUpdateSchoolClassWithPatch() throws Exception {
        // Initialize the database
        insertedSchoolClass = schoolClassRepository.save(schoolClass).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the schoolClass using partial update
        SchoolClass partialUpdatedSchoolClass = new SchoolClass();
        partialUpdatedSchoolClass.setId(schoolClass.getId());

        partialUpdatedSchoolClass.name(UPDATED_NAME).year(UPDATED_YEAR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSchoolClass.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSchoolClass))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SchoolClass in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSchoolClassUpdatableFieldsEquals(partialUpdatedSchoolClass, getPersistedSchoolClass(partialUpdatedSchoolClass));
    }

    @Test
    void patchNonExistingSchoolClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schoolClass.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, schoolClass.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(schoolClass))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SchoolClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSchoolClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schoolClass.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(schoolClass))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SchoolClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSchoolClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schoolClass.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(schoolClass))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SchoolClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSchoolClass() {
        // Initialize the database
        insertedSchoolClass = schoolClassRepository.save(schoolClass).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the schoolClass
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, schoolClass.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return schoolClassRepository.count().block();
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

    protected SchoolClass getPersistedSchoolClass(SchoolClass schoolClass) {
        return schoolClassRepository.findById(schoolClass.getId()).block();
    }

    protected void assertPersistedSchoolClassToMatchAllProperties(SchoolClass expectedSchoolClass) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSchoolClassAllPropertiesEquals(expectedSchoolClass, getPersistedSchoolClass(expectedSchoolClass));
        assertSchoolClassUpdatableFieldsEquals(expectedSchoolClass, getPersistedSchoolClass(expectedSchoolClass));
    }

    protected void assertPersistedSchoolClassToMatchUpdatableProperties(SchoolClass expectedSchoolClass) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSchoolClassAllUpdatablePropertiesEquals(expectedSchoolClass, getPersistedSchoolClass(expectedSchoolClass));
        assertSchoolClassUpdatableFieldsEquals(expectedSchoolClass, getPersistedSchoolClass(expectedSchoolClass));
    }
}
