package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.UpnextteamsApp
import team.upnext.upnextteams.domain.Vote
import team.upnext.upnextteams.domain.User
import team.upnext.upnextteams.repository.VoteRepository
import team.upnext.upnextteams.service.VoteService
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
 * Integration tests for the [VoteResource] REST controller.
 *
 * @see VoteResource
 */
@SpringBootTest(classes = [UpnextteamsApp::class])
@AutoConfigureWebTestClient
@WithMockUser
class VoteResourceIT  {

    @Autowired
    private lateinit var voteRepository: VoteRepository

    @Autowired
    private lateinit var voteService: VoteService




    @Autowired
    private lateinit var webTestClient: WebTestClient 

    private lateinit var vote: Vote

    


    @BeforeEach
    fun initTest() {
        voteRepository.deleteAll().block()
        vote = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createVote() {
        val databaseSizeBeforeCreate = voteRepository.findAll().collectList().block().size

        // Create the Vote
        webTestClient.post().uri("/api/votes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(vote))
            .exchange()
            .expectStatus().isCreated

        // Validate the Vote in the database
        val voteList = voteRepository.findAll().collectList().block()
        assertThat(voteList).hasSize(databaseSizeBeforeCreate + 1)
        val testVote = voteList[voteList.size - 1]
        assertThat(testVote.value).isEqualTo(DEFAULT_VALUE)
    }

    @Test
    fun createVoteWithExistingId() {
        val databaseSizeBeforeCreate = voteRepository.findAll().collectList().block().size

        // Create the Vote with an existing ID
        vote.id = "existing_id"

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient.post().uri("/api/votes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(vote))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Vote in the database
        val voteList = voteRepository.findAll().collectList().block()
        assertThat(voteList).hasSize(databaseSizeBeforeCreate)
    }


    @Test
    fun checkValueIsRequired() {
        val databaseSizeBeforeTest = voteRepository.findAll().collectList().block().size
        // set the field null
        vote.value = null

        // Create the Vote, which fails.

        webTestClient.post().uri("/api/votes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(vote))
            .exchange()
            .expectStatus().isBadRequest

        val voteList = voteRepository.findAll().collectList().block()
        assertThat(voteList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    fun getAllVotesAsStream() {
        // Initialize the database
        voteRepository.save(vote).block()

        val voteList = webTestClient.get().uri("/api/votes")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_STREAM_JSON)
            .returnResult(Vote::class.java)
            .responseBody
            .filter(vote::equals)
            .collectList()
            .block(Duration.ofSeconds(5))

        assertThat(voteList).isNotNull
        assertThat(voteList).hasSize(1)
        val testVote = voteList[0]
        assertThat(testVote.value).isEqualTo(DEFAULT_VALUE)
    }
    @Test
    
    fun getAllVotes() {
        // Initialize the database
        voteRepository.save(vote).block()
        
        webTestClient.get().uri("/api/votes?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id").value(hasItem(vote.id))
            .jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE))    }
    
    @Test
    
    fun getVote() {
        // Initialize the database
        voteRepository.save(vote).block()

        val id = vote.id
        assertNotNull(id)

        // Get the vote
        webTestClient.get().uri("/api/votes/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").value(`is`(vote.id))
            .jsonPath("$.value").value(`is`(DEFAULT_VALUE))    }

    @Test
    
    fun getNonExistingVote() {
        // Get the vote
        webTestClient.get().uri("/api/votes/{id}", Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }
    @Test
    fun updateVote() {
        // Initialize the database
        voteService.save(vote).block()

        val databaseSizeBeforeUpdate = voteRepository.findAll().collectList().block().size

        // Update the vote
        val id = vote.id
        assertNotNull(id)
        val updatedVote = voteRepository.findById(id).block()
        updatedVote.value = UPDATED_VALUE

        webTestClient.put().uri("/api/votes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(updatedVote))
            .exchange()
            .expectStatus().isOk

        // Validate the Vote in the database
        val voteList = voteRepository.findAll().collectList().block()
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate)
        val testVote = voteList[voteList.size - 1]
        assertThat(testVote.value).isEqualTo(UPDATED_VALUE)
    }

    @Test
    fun updateNonExistingVote() {
        val databaseSizeBeforeUpdate = voteRepository.findAll().collectList().block().size


        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.put().uri("/api/votes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(vote))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Vote in the database
        val voteList = voteRepository.findAll().collectList().block()
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    
    fun deleteVote() {
        // Initialize the database
        voteService.save(vote).block()

        val databaseSizeBeforeDelete = voteRepository.findAll().collectList().block().size

        webTestClient.delete().uri("/api/votes/{id}", vote.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // Validate the database contains one less item
        val voteList = voteRepository.findAll().collectList().block()
        assertThat(voteList).hasSize(databaseSizeBeforeDelete - 1)
    }


    companion object {

        private const val DEFAULT_VALUE: Int = 1
        private const val UPDATED_VALUE: Int = 2

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Vote {
            val vote = Vote(
                value = DEFAULT_VALUE
            )

            // Add required entity
            val user = UserResourceIT.createEntity()
            user.id = "fixed-id-for-tests"
            vote.user = user
            return vote
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Vote {
            val vote = Vote(
                value = UPDATED_VALUE
            )

            // Add required entity
            val user = UserResourceIT.createEntity()
            user.id = "fixed-id-for-tests"
            vote.user = user
            return vote
        }
    }
}
