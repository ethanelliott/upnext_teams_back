package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.UpnextteamsApp
import team.upnext.upnextteams.domain.Playlist
import team.upnext.upnextteams.repository.PlaylistRepository
import team.upnext.upnextteams.service.PlaylistService
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


/**
 * Integration tests for the [PlaylistResource] REST controller.
 *
 * @see PlaylistResource
 */
@SpringBootTest(classes = [UpnextteamsApp::class])
@AutoConfigureWebTestClient
@WithMockUser
class PlaylistResourceIT  {

    @Autowired
    private lateinit var playlistRepository: PlaylistRepository

    @Autowired
    private lateinit var playlistService: PlaylistService




    @Autowired
    private lateinit var webTestClient: WebTestClient 

    private lateinit var playlist: Playlist

    


    @BeforeEach
    fun initTest() {
        playlistRepository.deleteAll().block()
        playlist = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createPlaylist() {
        val databaseSizeBeforeCreate = playlistRepository.findAll().collectList().block().size

        // Create the Playlist
        webTestClient.post().uri("/api/playlists")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(playlist))
            .exchange()
            .expectStatus().isCreated

        // Validate the Playlist in the database
        val playlistList = playlistRepository.findAll().collectList().block()
        assertThat(playlistList).hasSize(databaseSizeBeforeCreate + 1)
    }

    @Test
    fun createPlaylistWithExistingId() {
        val databaseSizeBeforeCreate = playlistRepository.findAll().collectList().block().size

        // Create the Playlist with an existing ID
        playlist.id = "existing_id"

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient.post().uri("/api/playlists")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(playlist))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Playlist in the database
        val playlistList = playlistRepository.findAll().collectList().block()
        assertThat(playlistList).hasSize(databaseSizeBeforeCreate)
    }


    @Test
    fun getAllPlaylistsAsStream() {
        // Initialize the database
        playlistRepository.save(playlist).block()

        val playlistList = webTestClient.get().uri("/api/playlists")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_STREAM_JSON)
            .returnResult(Playlist::class.java)
            .responseBody
            .filter(playlist::equals)
            .collectList()
            .block(Duration.ofSeconds(5))

        assertThat(playlistList).isNotNull
        assertThat(playlistList).hasSize(1)
        val testPlaylist = playlistList[0]
    }
    @Test
    
    fun getAllPlaylists() {
        // Initialize the database
        playlistRepository.save(playlist).block()
        
        webTestClient.get().uri("/api/playlists?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id").value(hasItem(playlist.id))    }
    
    @Test
    
    fun getPlaylist() {
        // Initialize the database
        playlistRepository.save(playlist).block()

        val id = playlist.id
        assertNotNull(id)

        // Get the playlist
        webTestClient.get().uri("/api/playlists/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").value(`is`(playlist.id))    }

    @Test
    
    fun getNonExistingPlaylist() {
        // Get the playlist
        webTestClient.get().uri("/api/playlists/{id}", Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }
    @Test
    fun updatePlaylist() {
        // Initialize the database
        playlistService.save(playlist).block()

        val databaseSizeBeforeUpdate = playlistRepository.findAll().collectList().block().size

        // Update the playlist
        val id = playlist.id
        assertNotNull(id)
        val updatedPlaylist = playlistRepository.findById(id).block()

        webTestClient.put().uri("/api/playlists")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(updatedPlaylist))
            .exchange()
            .expectStatus().isOk

        // Validate the Playlist in the database
        val playlistList = playlistRepository.findAll().collectList().block()
        assertThat(playlistList).hasSize(databaseSizeBeforeUpdate)
        val testPlaylist = playlistList[playlistList.size - 1]
    }

    @Test
    fun updateNonExistingPlaylist() {
        val databaseSizeBeforeUpdate = playlistRepository.findAll().collectList().block().size


        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.put().uri("/api/playlists")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(playlist))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Playlist in the database
        val playlistList = playlistRepository.findAll().collectList().block()
        assertThat(playlistList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    
    fun deletePlaylist() {
        // Initialize the database
        playlistService.save(playlist).block()

        val databaseSizeBeforeDelete = playlistRepository.findAll().collectList().block().size

        webTestClient.delete().uri("/api/playlists/{id}", playlist.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // Validate the database contains one less item
        val playlistList = playlistRepository.findAll().collectList().block()
        assertThat(playlistList).hasSize(databaseSizeBeforeDelete - 1)
    }


    companion object {

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Playlist {
            val playlist = Playlist(
            )

            return playlist
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Playlist {
            val playlist = Playlist(
            )

            return playlist
        }
    }
}
