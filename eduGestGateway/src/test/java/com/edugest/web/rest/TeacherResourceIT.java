package com.edugest.web.rest;

import static com.edugest.domain.TeacherAsserts.*;
import static com.edugest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.edugest.IntegrationTest;
import com.edugest.domain.Teacher;
import com.edugest.domain.enumeration.Genders;
import com.edugest.repository.EntityManager;
import com.edugest.repository.TeacherRepository;
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
 * Integration tests for the {@link TeacherResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TeacherResourceIT {

    private static final String DEFAULT_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFIER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_QUALIFICATION = "AAAAAAAAAA";
    private static final String UPDATED_QUALIFICATION = "BBBBBBBBBB";

    private static final Genders DEFAULT_GENDER = Genders.MALE;
    private static final Genders UPDATED_GENDER = Genders.FEMALE;

    private static final Integer DEFAULT_EXPERIENCE = 1;
    private static final Integer UPDATED_EXPERIENCE = 2;

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/teachers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TeacherRepository teacherRepository;

    @Mock
    private TeacherRepository teacherRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Teacher teacher;

    private Teacher insertedTeacher;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Teacher createEntity() {
        return new Teacher()
            .identifier(DEFAULT_IDENTIFIER)
            .birthDate(DEFAULT_BIRTH_DATE)
            .qualification(DEFAULT_QUALIFICATION)
            .gender(DEFAULT_GENDER)
            .experience(DEFAULT_EXPERIENCE)
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
    public static Teacher createUpdatedEntity() {
        return new Teacher()
            .identifier(UPDATED_IDENTIFIER)
            .birthDate(UPDATED_BIRTH_DATE)
            .qualification(UPDATED_QUALIFICATION)
            .gender(UPDATED_GENDER)
            .experience(UPDATED_EXPERIENCE)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .type(UPDATED_TYPE)
            .note(UPDATED_NOTE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_teacher__subjects").block();
            em.deleteAll(Teacher.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        teacher = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTeacher != null) {
            teacherRepository.delete(insertedTeacher).block();
            insertedTeacher = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTeacher() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Teacher
        var returnedTeacher = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teacher))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Teacher.class)
            .returnResult()
            .getResponseBody();

        // Validate the Teacher in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTeacherUpdatableFieldsEquals(returnedTeacher, getPersistedTeacher(returnedTeacher));

        insertedTeacher = returnedTeacher;
    }

    @Test
    void createTeacherWithExistingId() throws Exception {
        // Create the Teacher with an existing ID
        teacher.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teacher))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Teacher in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkIdentifierIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        teacher.setIdentifier(null);

        // Create the Teacher, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teacher))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTeachersAsStream() {
        // Initialize the database
        teacherRepository.save(teacher).block();

        List<Teacher> teacherList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Teacher.class)
            .getResponseBody()
            .filter(teacher::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(teacherList).isNotNull();
        assertThat(teacherList).hasSize(1);
        Teacher testTeacher = teacherList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertTeacherAllPropertiesEquals(teacher, testTeacher);
        assertTeacherUpdatableFieldsEquals(teacher, testTeacher);
    }

    @Test
    void getAllTeachers() {
        // Initialize the database
        insertedTeacher = teacherRepository.save(teacher).block();

        // Get all the teacherList
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
            .value(hasItem(teacher.getId().intValue()))
            .jsonPath("$.[*].identifier")
            .value(hasItem(DEFAULT_IDENTIFIER))
            .jsonPath("$.[*].birthDate")
            .value(hasItem(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.[*].qualification")
            .value(hasItem(DEFAULT_QUALIFICATION))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].experience")
            .value(hasItem(DEFAULT_EXPERIENCE))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeachersWithEagerRelationshipsIsEnabled() {
        when(teacherRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(teacherRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeachersWithEagerRelationshipsIsNotEnabled() {
        when(teacherRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(teacherRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTeacher() {
        // Initialize the database
        insertedTeacher = teacherRepository.save(teacher).block();

        // Get the teacher
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, teacher.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(teacher.getId().intValue()))
            .jsonPath("$.identifier")
            .value(is(DEFAULT_IDENTIFIER))
            .jsonPath("$.birthDate")
            .value(is(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.qualification")
            .value(is(DEFAULT_QUALIFICATION))
            .jsonPath("$.gender")
            .value(is(DEFAULT_GENDER.toString()))
            .jsonPath("$.experience")
            .value(is(DEFAULT_EXPERIENCE))
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
    void getNonExistingTeacher() {
        // Get the teacher
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTeacher() throws Exception {
        // Initialize the database
        insertedTeacher = teacherRepository.save(teacher).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teacher
        Teacher updatedTeacher = teacherRepository.findById(teacher.getId()).block();
        updatedTeacher
            .identifier(UPDATED_IDENTIFIER)
            .birthDate(UPDATED_BIRTH_DATE)
            .qualification(UPDATED_QUALIFICATION)
            .gender(UPDATED_GENDER)
            .experience(UPDATED_EXPERIENCE)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .type(UPDATED_TYPE)
            .note(UPDATED_NOTE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTeacher.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedTeacher))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Teacher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTeacherToMatchAllProperties(updatedTeacher);
    }

    @Test
    void putNonExistingTeacher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacher.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, teacher.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teacher))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Teacher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTeacher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacher.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teacher))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Teacher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTeacher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacher.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teacher))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Teacher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTeacherWithPatch() throws Exception {
        // Initialize the database
        insertedTeacher = teacherRepository.save(teacher).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teacher using partial update
        Teacher partialUpdatedTeacher = new Teacher();
        partialUpdatedTeacher.setId(teacher.getId());

        partialUpdatedTeacher
            .identifier(UPDATED_IDENTIFIER)
            .birthDate(UPDATED_BIRTH_DATE)
            .qualification(UPDATED_QUALIFICATION)
            .experience(UPDATED_EXPERIENCE)
            .address(UPDATED_ADDRESS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeacher.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTeacher))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Teacher in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeacherUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTeacher, teacher), getPersistedTeacher(teacher));
    }

    @Test
    void fullUpdateTeacherWithPatch() throws Exception {
        // Initialize the database
        insertedTeacher = teacherRepository.save(teacher).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teacher using partial update
        Teacher partialUpdatedTeacher = new Teacher();
        partialUpdatedTeacher.setId(teacher.getId());

        partialUpdatedTeacher
            .identifier(UPDATED_IDENTIFIER)
            .birthDate(UPDATED_BIRTH_DATE)
            .qualification(UPDATED_QUALIFICATION)
            .gender(UPDATED_GENDER)
            .experience(UPDATED_EXPERIENCE)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .type(UPDATED_TYPE)
            .note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeacher.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTeacher))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Teacher in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeacherUpdatableFieldsEquals(partialUpdatedTeacher, getPersistedTeacher(partialUpdatedTeacher));
    }

    @Test
    void patchNonExistingTeacher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacher.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, teacher.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teacher))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Teacher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTeacher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacher.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teacher))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Teacher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTeacher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teacher.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teacher))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Teacher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTeacher() {
        // Initialize the database
        insertedTeacher = teacherRepository.save(teacher).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the teacher
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, teacher.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return teacherRepository.count().block();
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

    protected Teacher getPersistedTeacher(Teacher teacher) {
        return teacherRepository.findById(teacher.getId()).block();
    }

    protected void assertPersistedTeacherToMatchAllProperties(Teacher expectedTeacher) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTeacherAllPropertiesEquals(expectedTeacher, getPersistedTeacher(expectedTeacher));
        assertTeacherUpdatableFieldsEquals(expectedTeacher, getPersistedTeacher(expectedTeacher));
    }

    protected void assertPersistedTeacherToMatchUpdatableProperties(Teacher expectedTeacher) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTeacherAllUpdatablePropertiesEquals(expectedTeacher, getPersistedTeacher(expectedTeacher));
        assertTeacherUpdatableFieldsEquals(expectedTeacher, getPersistedTeacher(expectedTeacher));
    }
}
