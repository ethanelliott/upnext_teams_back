package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.UpnextteamsApp
import team.upnext.upnextteams.domain.Track
import team.upnext.upnextteams.domain.YoutubeAudio
import team.upnext.upnextteams.domain.YoutubeMetadata
import team.upnext.upnextteams.repository.TrackRepository
import team.upnext.upnextteams.service.TrackService
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
 * Integration tests for the [TrackResource] REST controller.
 *
 * @see TrackResource
 */
@SpringBootTest(classes = [UpnextteamsApp::class])
@AutoConfigureWebTestClient
@WithMockUser
class TrackResourceIT  {

    @Autowired
    private lateinit var trackRepository: TrackRepository

    @Autowired
    private lateinit var trackService: TrackService




    @Autowired
    private lateinit var webTestClient: WebTestClient 

    private lateinit var track: Track

    


    @BeforeEach
    fun initTest() {
        trackRepository.deleteAll().block()
        track = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createTrack() {
        val databaseSizeBeforeCreate = trackRepository.findAll().collectList().block().size

        // Create the Track
        webTestClient.post().uri("/api/tracks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus().isCreated

        // Validate the Track in the database
        val trackList = trackRepository.findAll().collectList().block()
        assertThat(trackList).hasSize(databaseSizeBeforeCreate + 1)
    }

    @Test
    fun createTrackWithExistingId() {
        val databaseSizeBeforeCreate = trackRepository.findAll().collectList().block().size

        // Create the Track with an existing ID
        track.id = "existing_id"

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient.post().uri("/api/tracks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Track in the database
        val trackList = trackRepository.findAll().collectList().block()
        assertThat(trackList).hasSize(databaseSizeBeforeCreate)
    }


    @Test
    fun getAllTracksAsStream() {
        // Initialize the database
        trackRepository.save(track).block()

        val trackList = webTestClient.get().uri("/api/tracks")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_STREAM_JSON)
            .returnResult(Track::class.java)
            .responseBody
            .filter(track::equals)
            .collectList()
            .block(Duration.ofSeconds(5))

        assertThat(trackList).isNotNull
        assertThat(trackList).hasSize(1)
        val testTrack = trackList[0]
    }
    @Test
    
    fun getAllTracks() {
        // Initialize the database
        trackRepository.save(track).block()
        
        webTestClient.get().uri("/api/tracks?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id").value(hasItem(track.id))    }
    
    @Test
    
    fun getTrack() {
        // Initialize the database
        trackRepository.save(track).block()

        val id = track.id
        assertNotNull(id)

        // Get the track
        webTestClient.get().uri("/api/tracks/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").value(`is`(track.id))    }

    @Test
    
    fun getNonExistingTrack() {
        // Get the track
        webTestClient.get().uri("/api/tracks/{id}", Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }
    @Test
    fun updateTrack() {
        // Initialize the database
        trackService.save(track).block()

        val databaseSizeBeforeUpdate = trackRepository.findAll().collectList().block().size

        // Update the track
        val id = track.id
        assertNotNull(id)
        val updatedTrack = trackRepository.findById(id).block()

        webTestClient.put().uri("/api/tracks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(updatedTrack))
            .exchange()
            .expectStatus().isOk

        // Validate the Track in the database
        val trackList = trackRepository.findAll().collectList().block()
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate)
        val testTrack = trackList[trackList.size - 1]
    }

    @Test
    fun updateNonExistingTrack() {
        val databaseSizeBeforeUpdate = trackRepository.findAll().collectList().block().size


        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.put().uri("/api/tracks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Track in the database
        val trackList = trackRepository.findAll().collectList().block()
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    
    fun deleteTrack() {
        // Initialize the database
        trackService.save(track).block()

        val databaseSizeBeforeDelete = trackRepository.findAll().collectList().block().size

        webTestClient.delete().uri("/api/tracks/{id}", track.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // Validate the database contains one less item
        val trackList = trackRepository.findAll().collectList().block()
        assertThat(trackList).hasSize(databaseSizeBeforeDelete - 1)
    }


    companion object {

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Track {
            val track = Track(
            )

            // Add required entity
            val youtubeAudio: YoutubeAudio
            youtubeAudio = YoutubeAudioResourceIT.createEntity()
            youtubeAudio.id = "fixed-id-for-tests"
            track.audio = youtubeAudio
            // Add required entity
            val youtubeMetadata: YoutubeMetadata
            youtubeMetadata = YoutubeMetadataResourceIT.createEntity()
            youtubeMetadata.id = "fixed-id-for-tests"
            track.metadata = youtubeMetadata
            return track
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Track {
            val track = Track(
            )

            // Add required entity
            val youtubeAudio: YoutubeAudio
            youtubeAudio = YoutubeAudioResourceIT.createUpdatedEntity()
            youtubeAudio.id = "fixed-id-for-tests"
            track.audio = youtubeAudio
            // Add required entity
            val youtubeMetadata: YoutubeMetadata
            youtubeMetadata = YoutubeMetadataResourceIT.createUpdatedEntity()
            youtubeMetadata.id = "fixed-id-for-tests"
            track.metadata = youtubeMetadata
            return track
        }
    }
}
