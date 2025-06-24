package com.edugest.web.rest;

import static com.edugest.domain.SessionAsserts.*;
import static com.edugest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.edugest.IntegrationTest;
import com.edugest.domain.Session;
import com.edugest.repository.EntityManager;
import com.edugest.repository.SessionRepository;
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
 * Integration tests for the {@link SessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SessionResourceIT {

    private static final String DEFAULT_DAY = "AAAAAAAAAA";
    private static final String UPDATED_DAY = "BBBBBBBBBB";

    private static final String DEFAULT_START_TIME = "AAAAAAAAAA";
    private static final String UPDATED_START_TIME = "BBBBBBBBBB";

    private static final String DEFAULT_END_TIME = "AAAAAAAAAA";
    private static final String UPDATED_END_TIME = "BBBBBBBBBB";

    private static final Integer DEFAULT_SEMESTER = 1;
    private static final Integer UPDATED_SEMESTER = 2;

    private static final String ENTITY_API_URL = "/api/sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Session session;

    private Session insertedSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Session createEntity() {
        return new Session().day(DEFAULT_DAY).startTime(DEFAULT_START_TIME).endTime(DEFAULT_END_TIME).semester(DEFAULT_SEMESTER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Session createUpdatedEntity() {
        return new Session().day(UPDATED_DAY).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).semester(UPDATED_SEMESTER);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Session.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        session = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSession != null) {
            sessionRepository.delete(insertedSession).block();
            insertedSession = null;
        }
        deleteEntities(em);
    }

    @Test
    void createSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Session
        var returnedSession = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(session))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Session.class)
            .returnResult()
            .getResponseBody();

        // Validate the Session in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSessionUpdatableFieldsEquals(returnedSession, getPersistedSession(returnedSession));

        insertedSession = returnedSession;
    }

    @Test
    void createSessionWithExistingId() throws Exception {
        // Create the Session with an existing ID
        session.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(session))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllSessionsAsStream() {
        // Initialize the database
        sessionRepository.save(session).block();

        List<Session> sessionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Session.class)
            .getResponseBody()
            .filter(session::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(sessionList).isNotNull();
        assertThat(sessionList).hasSize(1);
        Session testSession = sessionList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertSessionAllPropertiesEquals(session, testSession);
        assertSessionUpdatableFieldsEquals(session, testSession);
    }

    @Test
    void getAllSessions() {
        // Initialize the database
        insertedSession = sessionRepository.save(session).block();

        // Get all the sessionList
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
            .value(hasItem(session.getId().intValue()))
            .jsonPath("$.[*].day")
            .value(hasItem(DEFAULT_DAY))
            .jsonPath("$.[*].startTime")
            .value(hasItem(DEFAULT_START_TIME))
            .jsonPath("$.[*].endTime")
            .value(hasItem(DEFAULT_END_TIME))
            .jsonPath("$.[*].semester")
            .value(hasItem(DEFAULT_SEMESTER));
    }

    @Test
    void getSession() {
        // Initialize the database
        insertedSession = sessionRepository.save(session).block();

        // Get the session
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, session.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(session.getId().intValue()))
            .jsonPath("$.day")
            .value(is(DEFAULT_DAY))
            .jsonPath("$.startTime")
            .value(is(DEFAULT_START_TIME))
            .jsonPath("$.endTime")
            .value(is(DEFAULT_END_TIME))
            .jsonPath("$.semester")
            .value(is(DEFAULT_SEMESTER));
    }

    @Test
    void getNonExistingSession() {
        // Get the session
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSession() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.save(session).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the session
        Session updatedSession = sessionRepository.findById(session.getId()).block();
        updatedSession.day(UPDATED_DAY).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).semester(UPDATED_SEMESTER);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSession.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedSession))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSessionToMatchAllProperties(updatedSession);
    }

    @Test
    void putNonExistingSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, session.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(session))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(session))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(session))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSessionWithPatch() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.save(session).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the session using partial update
        Session partialUpdatedSession = new Session();
        partialUpdatedSession.setId(session.getId());

        partialUpdatedSession.startTime(UPDATED_START_TIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSession.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSession))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Session in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSessionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSession, session), getPersistedSession(session));
    }

    @Test
    void fullUpdateSessionWithPatch() throws Exception {
        // Initialize the database
        insertedSession = sessionRepository.save(session).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the session using partial update
        Session partialUpdatedSession = new Session();
        partialUpdatedSession.setId(session.getId());

        partialUpdatedSession.day(UPDATED_DAY).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).semester(UPDATED_SEMESTER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSession.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSession))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Session in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSessionUpdatableFieldsEquals(partialUpdatedSession, getPersistedSession(partialUpdatedSession));
    }

    @Test
    void patchNonExistingSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, session.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(session))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(session))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(session))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Session in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSession() {
        // Initialize the database
        insertedSession = sessionRepository.save(session).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the session
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, session.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sessionRepository.count().block();
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

    protected Session getPersistedSession(Session session) {
        return sessionRepository.findById(session.getId()).block();
    }

    protected void assertPersistedSessionToMatchAllProperties(Session expectedSession) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSessionAllPropertiesEquals(expectedSession, getPersistedSession(expectedSession));
        assertSessionUpdatableFieldsEquals(expectedSession, getPersistedSession(expectedSession));
    }

    protected void assertPersistedSessionToMatchUpdatableProperties(Session expectedSession) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSessionAllUpdatablePropertiesEquals(expectedSession, getPersistedSession(expectedSession));
        assertSessionUpdatableFieldsEquals(expectedSession, getPersistedSession(expectedSession));
    }
}
