package com.edugest.web.rest;

import static com.edugest.domain.SubjectAsserts.*;
import static com.edugest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.edugest.IntegrationTest;
import com.edugest.domain.Subject;
import com.edugest.repository.EntityManager;
import com.edugest.repository.SubjectRepository;
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
 * Integration tests for the {@link SubjectResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SubjectResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/subjects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubjectRepository subjectRepository;

    @Mock
    private SubjectRepository subjectRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Subject subject;

    private Subject insertedSubject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subject createEntity() {
        return new Subject().name(DEFAULT_NAME).code(DEFAULT_CODE).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subject createUpdatedEntity() {
        return new Subject().name(UPDATED_NAME).code(UPDATED_CODE).description(UPDATED_DESCRIPTION);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_subject__school_classes").block();
            em.deleteAll(Subject.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        subject = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSubject != null) {
            subjectRepository.delete(insertedSubject).block();
            insertedSubject = null;
        }
        deleteEntities(em);
    }

    @Test
    void createSubject() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Subject
        var returnedSubject = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subject))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Subject.class)
            .returnResult()
            .getResponseBody();

        // Validate the Subject in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSubjectUpdatableFieldsEquals(returnedSubject, getPersistedSubject(returnedSubject));

        insertedSubject = returnedSubject;
    }

    @Test
    void createSubjectWithExistingId() throws Exception {
        // Create the Subject with an existing ID
        subject.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subject))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subject.setName(null);

        // Create the Subject, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subject))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subject.setCode(null);

        // Create the Subject, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subject))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSubjectsAsStream() {
        // Initialize the database
        subjectRepository.save(subject).block();

        List<Subject> subjectList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Subject.class)
            .getResponseBody()
            .filter(subject::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(subjectList).isNotNull();
        assertThat(subjectList).hasSize(1);
        Subject testSubject = subjectList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertSubjectAllPropertiesEquals(subject, testSubject);
        assertSubjectUpdatableFieldsEquals(subject, testSubject);
    }

    @Test
    void getAllSubjects() {
        // Initialize the database
        insertedSubject = subjectRepository.save(subject).block();

        // Get all the subjectList
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
            .value(hasItem(subject.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubjectsWithEagerRelationshipsIsEnabled() {
        when(subjectRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(subjectRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubjectsWithEagerRelationshipsIsNotEnabled() {
        when(subjectRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(subjectRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSubject() {
        // Initialize the database
        insertedSubject = subjectRepository.save(subject).block();

        // Get the subject
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, subject.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(subject.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingSubject() {
        // Get the subject
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSubject() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.save(subject).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subject
        Subject updatedSubject = subjectRepository.findById(subject.getId()).block();
        updatedSubject.name(UPDATED_NAME).code(UPDATED_CODE).description(UPDATED_DESCRIPTION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSubject.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedSubject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubjectToMatchAllProperties(updatedSubject);
    }

    @Test
    void putNonExistingSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, subject.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subject))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subject))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subject))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSubjectWithPatch() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.save(subject).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subject using partial update
        Subject partialUpdatedSubject = new Subject();
        partialUpdatedSubject.setId(subject.getId());

        partialUpdatedSubject.name(UPDATED_NAME).code(UPDATED_CODE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSubject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSubject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Subject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubjectUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSubject, subject), getPersistedSubject(subject));
    }

    @Test
    void fullUpdateSubjectWithPatch() throws Exception {
        // Initialize the database
        insertedSubject = subjectRepository.save(subject).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subject using partial update
        Subject partialUpdatedSubject = new Subject();
        partialUpdatedSubject.setId(subject.getId());

        partialUpdatedSubject.name(UPDATED_NAME).code(UPDATED_CODE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSubject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSubject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Subject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubjectUpdatableFieldsEquals(partialUpdatedSubject, getPersistedSubject(partialUpdatedSubject));
    }

    @Test
    void patchNonExistingSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, subject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subject))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subject))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSubject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subject.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subject))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Subject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSubject() {
        // Initialize the database
        insertedSubject = subjectRepository.save(subject).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the subject
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, subject.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return subjectRepository.count().block();
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

    protected Subject getPersistedSubject(Subject subject) {
        return subjectRepository.findById(subject.getId()).block();
    }

    protected void assertPersistedSubjectToMatchAllProperties(Subject expectedSubject) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSubjectAllPropertiesEquals(expectedSubject, getPersistedSubject(expectedSubject));
        assertSubjectUpdatableFieldsEquals(expectedSubject, getPersistedSubject(expectedSubject));
    }

    protected void assertPersistedSubjectToMatchUpdatableProperties(Subject expectedSubject) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSubjectAllUpdatablePropertiesEquals(expectedSubject, getPersistedSubject(expectedSubject));
        assertSubjectUpdatableFieldsEquals(expectedSubject, getPersistedSubject(expectedSubject));
    }
}
