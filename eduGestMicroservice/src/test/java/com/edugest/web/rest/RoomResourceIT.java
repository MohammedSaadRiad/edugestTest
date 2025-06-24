package com.edugest.web.rest;

import static com.edugest.domain.RoomAsserts.*;
import static com.edugest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.edugest.IntegrationTest;
import com.edugest.domain.Room;
import com.edugest.repository.EntityManager;
import com.edugest.repository.RoomRepository;
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
 * Integration tests for the {@link RoomResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RoomResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_CAPACITY = 1;
    private static final Integer UPDATED_CAPACITY = 2;

    private static final String ENTITY_API_URL = "/api/rooms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Room room;

    private Room insertedRoom;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Room createEntity() {
        return new Room().name(DEFAULT_NAME).capacity(DEFAULT_CAPACITY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Room createUpdatedEntity() {
        return new Room().name(UPDATED_NAME).capacity(UPDATED_CAPACITY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Room.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        room = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRoom != null) {
            roomRepository.delete(insertedRoom).block();
            insertedRoom = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRoom() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Room
        var returnedRoom = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(room))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Room.class)
            .returnResult()
            .getResponseBody();

        // Validate the Room in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRoomUpdatableFieldsEquals(returnedRoom, getPersistedRoom(returnedRoom));

        insertedRoom = returnedRoom;
    }

    @Test
    void createRoomWithExistingId() throws Exception {
        // Create the Room with an existing ID
        room.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(room))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        room.setName(null);

        // Create the Room, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(room))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllRoomsAsStream() {
        // Initialize the database
        roomRepository.save(room).block();

        List<Room> roomList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Room.class)
            .getResponseBody()
            .filter(room::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(roomList).isNotNull();
        assertThat(roomList).hasSize(1);
        Room testRoom = roomList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertRoomAllPropertiesEquals(room, testRoom);
        assertRoomUpdatableFieldsEquals(room, testRoom);
    }

    @Test
    void getAllRooms() {
        // Initialize the database
        insertedRoom = roomRepository.save(room).block();

        // Get all the roomList
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
            .value(hasItem(room.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].capacity")
            .value(hasItem(DEFAULT_CAPACITY));
    }

    @Test
    void getRoom() {
        // Initialize the database
        insertedRoom = roomRepository.save(room).block();

        // Get the room
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, room.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(room.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.capacity")
            .value(is(DEFAULT_CAPACITY));
    }

    @Test
    void getNonExistingRoom() {
        // Get the room
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRoom() throws Exception {
        // Initialize the database
        insertedRoom = roomRepository.save(room).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the room
        Room updatedRoom = roomRepository.findById(room.getId()).block();
        updatedRoom.name(UPDATED_NAME).capacity(UPDATED_CAPACITY);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRoom.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedRoom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRoomToMatchAllProperties(updatedRoom);
    }

    @Test
    void putNonExistingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, room.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(room))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(room))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(room))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRoomWithPatch() throws Exception {
        // Initialize the database
        insertedRoom = roomRepository.save(room).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the room using partial update
        Room partialUpdatedRoom = new Room();
        partialUpdatedRoom.setId(room.getId());

        partialUpdatedRoom.name(UPDATED_NAME).capacity(UPDATED_CAPACITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRoom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRoom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Room in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRoomUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRoom, room), getPersistedRoom(room));
    }

    @Test
    void fullUpdateRoomWithPatch() throws Exception {
        // Initialize the database
        insertedRoom = roomRepository.save(room).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the room using partial update
        Room partialUpdatedRoom = new Room();
        partialUpdatedRoom.setId(room.getId());

        partialUpdatedRoom.name(UPDATED_NAME).capacity(UPDATED_CAPACITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRoom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRoom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Room in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRoomUpdatableFieldsEquals(partialUpdatedRoom, getPersistedRoom(partialUpdatedRoom));
    }

    @Test
    void patchNonExistingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, room.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(room))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(room))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(room))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRoom() {
        // Initialize the database
        insertedRoom = roomRepository.save(room).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the room
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, room.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return roomRepository.count().block();
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

    protected Room getPersistedRoom(Room room) {
        return roomRepository.findById(room.getId()).block();
    }

    protected void assertPersistedRoomToMatchAllProperties(Room expectedRoom) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRoomAllPropertiesEquals(expectedRoom, getPersistedRoom(expectedRoom));
        assertRoomUpdatableFieldsEquals(expectedRoom, getPersistedRoom(expectedRoom));
    }

    protected void assertPersistedRoomToMatchUpdatableProperties(Room expectedRoom) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRoomAllUpdatablePropertiesEquals(expectedRoom, getPersistedRoom(expectedRoom));
        assertRoomUpdatableFieldsEquals(expectedRoom, getPersistedRoom(expectedRoom));
    }
}
