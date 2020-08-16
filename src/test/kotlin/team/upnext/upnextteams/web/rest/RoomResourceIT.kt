package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.UpnextteamsApp
import team.upnext.upnextteams.domain.Room
import team.upnext.upnextteams.domain.User
import team.upnext.upnextteams.domain.Player
import team.upnext.upnextteams.domain.Playlist
import team.upnext.upnextteams.repository.RoomRepository
import team.upnext.upnextteams.service.RoomService
import team.upnext.upnextteams.web.rest.errors.ExceptionTranslator

import kotlin.test.assertNotNull

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.junit.jupiter.api.extension.Extensions
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.security.test.context.support.WithMockUser
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import org.springframework.validation.Validator

import java.time.Duration

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.`is`
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify


/**
 * Integration tests for the [RoomResource] REST controller.
 *
 * @see RoomResource
 */
@SpringBootTest(classes = [UpnextteamsApp::class])
@AutoConfigureWebTestClient
@WithMockUser
@Extensions(
    ExtendWith(MockitoExtension::class)
)
class RoomResourceIT  {

    @Autowired
    private lateinit var roomRepository: RoomRepository

    @Mock
    private lateinit var roomRepositoryMock: RoomRepository

    @Mock
    private lateinit var roomServiceMock: RoomService

    @Autowired
    private lateinit var roomService: RoomService




    @Autowired
    private lateinit var webTestClient: WebTestClient 

    private lateinit var room: Room

    


    @BeforeEach
    fun initTest() {
        roomRepository.deleteAll().block()
        room = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createRoom() {
        val databaseSizeBeforeCreate = roomRepository.findAll().collectList().block().size

        // Create the Room
        webTestClient.post().uri("/api/rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(room))
            .exchange()
            .expectStatus().isCreated

        // Validate the Room in the database
        val roomList = roomRepository.findAll().collectList().block()
        assertThat(roomList).hasSize(databaseSizeBeforeCreate + 1)
        val testRoom = roomList[roomList.size - 1]
        assertThat(testRoom.name).isEqualTo(DEFAULT_NAME)
        assertThat(testRoom.code).isEqualTo(DEFAULT_CODE)
        assertThat(testRoom.password).isEqualTo(DEFAULT_PASSWORD)
    }

    @Test
    fun createRoomWithExistingId() {
        val databaseSizeBeforeCreate = roomRepository.findAll().collectList().block().size

        // Create the Room with an existing ID
        room.id = "existing_id"

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient.post().uri("/api/rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(room))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Room in the database
        val roomList = roomRepository.findAll().collectList().block()
        assertThat(roomList).hasSize(databaseSizeBeforeCreate)
    }


    @Test
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = roomRepository.findAll().collectList().block().size
        // set the field null
        room.name = null

        // Create the Room, which fails.

        webTestClient.post().uri("/api/rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(room))
            .exchange()
            .expectStatus().isBadRequest

        val roomList = roomRepository.findAll().collectList().block()
        assertThat(roomList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    fun checkCodeIsRequired() {
        val databaseSizeBeforeTest = roomRepository.findAll().collectList().block().size
        // set the field null
        room.code = null

        // Create the Room, which fails.

        webTestClient.post().uri("/api/rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(room))
            .exchange()
            .expectStatus().isBadRequest

        val roomList = roomRepository.findAll().collectList().block()
        assertThat(roomList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    
    fun getAllRooms() {
        // Initialize the database
        roomRepository.save(room).block()
        
        webTestClient.get().uri("/api/rooms?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id").value(hasItem(room.id))
            .jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD))    }
    
    @Suppress("unchecked")
    
    fun getAllRoomsWithEagerRelationshipsIsEnabled() {
        val roomResource = RoomResource(roomServiceMock)
        `when`(roomServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty())

        webTestClient.get().uri("/api/rooms?eagerload=true")
            .exchange()
            .expectStatus().isOk

        verify(roomServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Suppress("unchecked")
    
    fun getAllRoomsWithEagerRelationshipsIsNotEnabled() {
        val roomResource = RoomResource(roomServiceMock)
        `when`(roomServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty())

        webTestClient.get().uri("/api/rooms?eagerload=true")
            .exchange()
            .expectStatus().isOk

        verify(roomServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    
    fun getRoom() {
        // Initialize the database
        roomRepository.save(room).block()

        val id = room.id
        assertNotNull(id)

        // Get the room
        webTestClient.get().uri("/api/rooms/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").value(`is`(room.id))
            .jsonPath("$.name").value(`is`(DEFAULT_NAME))
            .jsonPath("$.code").value(`is`(DEFAULT_CODE))
            .jsonPath("$.password").value(`is`(DEFAULT_PASSWORD))    }

    @Test
    
    fun getNonExistingRoom() {
        // Get the room
        webTestClient.get().uri("/api/rooms/{id}", Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }
    @Test
    fun updateRoom() {
        // Initialize the database
        roomService.save(room).block()

        val databaseSizeBeforeUpdate = roomRepository.findAll().collectList().block().size

        // Update the room
        val id = room.id
        assertNotNull(id)
        val updatedRoom = roomRepository.findById(id).block()
        updatedRoom.name = UPDATED_NAME
        updatedRoom.code = UPDATED_CODE
        updatedRoom.password = UPDATED_PASSWORD

        webTestClient.put().uri("/api/rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(updatedRoom))
            .exchange()
            .expectStatus().isOk

        // Validate the Room in the database
        val roomList = roomRepository.findAll().collectList().block()
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate)
        val testRoom = roomList[roomList.size - 1]
        assertThat(testRoom.name).isEqualTo(UPDATED_NAME)
        assertThat(testRoom.code).isEqualTo(UPDATED_CODE)
        assertThat(testRoom.password).isEqualTo(UPDATED_PASSWORD)
    }

    @Test
    fun updateNonExistingRoom() {
        val databaseSizeBeforeUpdate = roomRepository.findAll().collectList().block().size


        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.put().uri("/api/rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(room))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Room in the database
        val roomList = roomRepository.findAll().collectList().block()
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    
    fun deleteRoom() {
        // Initialize the database
        roomService.save(room).block()

        val databaseSizeBeforeDelete = roomRepository.findAll().collectList().block().size

        webTestClient.delete().uri("/api/rooms/{id}", room.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // Validate the database contains one less item
        val roomList = roomRepository.findAll().collectList().block()
        assertThat(roomList).hasSize(databaseSizeBeforeDelete - 1)
    }


    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_CODE = "AAAA"
        private const val UPDATED_CODE = "BBBB"

        private const val DEFAULT_PASSWORD = "AAAAAAAAAA"
        private const val UPDATED_PASSWORD = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Room {
            val room = Room(
                name = DEFAULT_NAME,
                code = DEFAULT_CODE,
                password = DEFAULT_PASSWORD
            )

            // Add required entity
            val user = UserResourceIT.createEntity()
            user.id = "fixed-id-for-tests"
            room.relationshipNamePlural.add(user);
            // Add required entity
            val player: Player
            player = PlayerResourceIT.createEntity()
            player.id = "fixed-id-for-tests"
            room.player = player
            // Add required entity
            val playlist: Playlist
            playlist = PlaylistResourceIT.createEntity()
            playlist.id = "fixed-id-for-tests"
            room.playlist = playlist
            return room
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Room {
            val room = Room(
                name = UPDATED_NAME,
                code = UPDATED_CODE,
                password = UPDATED_PASSWORD
            )

            // Add required entity
            val user = UserResourceIT.createEntity()
            user.id = "fixed-id-for-tests"
            room.relationshipNamePlural.add(user);
            // Add required entity
            val player: Player
            player = PlayerResourceIT.createUpdatedEntity()
            player.id = "fixed-id-for-tests"
            room.player = player
            // Add required entity
            val playlist: Playlist
            playlist = PlaylistResourceIT.createUpdatedEntity()
            playlist.id = "fixed-id-for-tests"
            room.playlist = playlist
            return room
        }
    }
}
