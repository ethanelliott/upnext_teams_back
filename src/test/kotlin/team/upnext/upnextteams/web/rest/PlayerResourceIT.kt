package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.UpnextteamsApp
import team.upnext.upnextteams.domain.Player
import team.upnext.upnextteams.repository.PlayerRepository
import team.upnext.upnextteams.service.PlayerService
import team.upnext.upnextteams.web.rest.errors.ExceptionTranslator

import kotlin.test.assertNotNull

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.validation.Validator

import java.time.Duration

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.`is`

import team.upnext.upnextteams.domain.enumeration.PlayerState

/**
 * Integration tests for the [PlayerResource] REST controller.
 *
 * @see PlayerResource
 */
@SpringBootTest(classes = [UpnextteamsApp::class])
@AutoConfigureWebTestClient
@WithMockUser
class PlayerResourceIT  {

    @Autowired
    private lateinit var playerRepository: PlayerRepository

    @Autowired
    private lateinit var playerService: PlayerService




    @Autowired
    private lateinit var webTestClient: WebTestClient 

    private lateinit var player: Player

    


    @BeforeEach
    fun initTest() {
        playerRepository.deleteAll().block()
        player = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createPlayer() {
        val databaseSizeBeforeCreate = playerRepository.findAll().collectList().block().size

        // Create the Player
        webTestClient.post().uri("/api/players")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(player))
            .exchange()
            .expectStatus().isCreated

        // Validate the Player in the database
        val playerList = playerRepository.findAll().collectList().block()
        assertThat(playerList).hasSize(databaseSizeBeforeCreate + 1)
        val testPlayer = playerList[playerList.size - 1]
        assertThat(testPlayer.state).isEqualTo(DEFAULT_STATE)
    }

    @Test
    fun createPlayerWithExistingId() {
        val databaseSizeBeforeCreate = playerRepository.findAll().collectList().block().size

        // Create the Player with an existing ID
        player.id = "existing_id"

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient.post().uri("/api/players")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(player))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Player in the database
        val playerList = playerRepository.findAll().collectList().block()
        assertThat(playerList).hasSize(databaseSizeBeforeCreate)
    }


    @Test
    fun checkStateIsRequired() {
        val databaseSizeBeforeTest = playerRepository.findAll().collectList().block().size
        // set the field null
        player.state = null

        // Create the Player, which fails.

        webTestClient.post().uri("/api/players")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(player))
            .exchange()
            .expectStatus().isBadRequest

        val playerList = playerRepository.findAll().collectList().block()
        assertThat(playerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    fun getAllPlayersAsStream() {
        // Initialize the database
        playerRepository.save(player).block()

        val playerList = webTestClient.get().uri("/api/players")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_STREAM_JSON)
            .returnResult(Player::class.java)
            .responseBody
            .filter(player::equals)
            .collectList()
            .block(Duration.ofSeconds(5))

        assertThat(playerList).isNotNull
        assertThat(playerList).hasSize(1)
        val testPlayer = playerList[0]
        assertThat(testPlayer.state).isEqualTo(DEFAULT_STATE)
    }
    @Test
    
    fun getAllPlayers() {
        // Initialize the database
        playerRepository.save(player).block()
        
        webTestClient.get().uri("/api/players?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id").value(hasItem(player.id))
            .jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString()))    }
    
    @Test
    
    fun getPlayer() {
        // Initialize the database
        playerRepository.save(player).block()

        val id = player.id
        assertNotNull(id)

        // Get the player
        webTestClient.get().uri("/api/players/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").value(`is`(player.id))
            .jsonPath("$.state").value(`is`(DEFAULT_STATE.toString()))    }

    @Test
    
    fun getNonExistingPlayer() {
        // Get the player
        webTestClient.get().uri("/api/players/{id}", Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }
    @Test
    fun updatePlayer() {
        // Initialize the database
        playerService.save(player).block()

        val databaseSizeBeforeUpdate = playerRepository.findAll().collectList().block().size

        // Update the player
        val id = player.id
        assertNotNull(id)
        val updatedPlayer = playerRepository.findById(id).block()
        updatedPlayer.state = UPDATED_STATE

        webTestClient.put().uri("/api/players")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(updatedPlayer))
            .exchange()
            .expectStatus().isOk

        // Validate the Player in the database
        val playerList = playerRepository.findAll().collectList().block()
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate)
        val testPlayer = playerList[playerList.size - 1]
        assertThat(testPlayer.state).isEqualTo(UPDATED_STATE)
    }

    @Test
    fun updateNonExistingPlayer() {
        val databaseSizeBeforeUpdate = playerRepository.findAll().collectList().block().size


        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.put().uri("/api/players")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(player))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Player in the database
        val playerList = playerRepository.findAll().collectList().block()
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    
    fun deletePlayer() {
        // Initialize the database
        playerService.save(player).block()

        val databaseSizeBeforeDelete = playerRepository.findAll().collectList().block().size

        webTestClient.delete().uri("/api/players/{id}", player.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // Validate the database contains one less item
        val playerList = playerRepository.findAll().collectList().block()
        assertThat(playerList).hasSize(databaseSizeBeforeDelete - 1)
    }


    companion object {

        private val DEFAULT_STATE: PlayerState = PlayerState.IDLE
        private val UPDATED_STATE: PlayerState = PlayerState.PLAYING

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Player {
            val player = Player(
                state = DEFAULT_STATE
            )

            return player
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Player {
            val player = Player(
                state = UPDATED_STATE
            )

            return player
        }
    }
}
