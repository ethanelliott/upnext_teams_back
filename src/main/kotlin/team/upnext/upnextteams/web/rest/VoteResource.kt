package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.domain.Vote
import team.upnext.upnextteams.service.VoteService
import team.upnext.upnextteams.web.rest.errors.BadRequestAlertException

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.reactive.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus  
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import javax.validation.Valid
import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "vote"
/**
 * REST controller for managing [team.upnext.upnextteams.domain.Vote].
 */
@RestController
@RequestMapping("/api")
class VoteResource(
    private val voteService: VoteService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /votes` : Create a new vote.
     *
     * @param vote the vote to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new vote, or with status `400 (Bad Request)` if the vote has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/votes")
    fun createVote(@Valid @RequestBody vote: Vote): Mono<ResponseEntity<Vote>> {
        log.debug("REST request to save Vote : $vote")
        if (vote.id != null) {
            throw BadRequestAlertException(
                "A new vote cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        return voteService.save(vote)
            .map { result -> 
                try {
                    ResponseEntity.created(URI("/api/votes/${result.id}"))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id))
                        .body(result)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    /**
     * `PUT  /votes` : Updates an existing vote.
     *
     * @param vote the vote to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated vote,
     * or with status `400 (Bad Request)` if the vote is not valid,
     * or with status `500 (Internal Server Error)` if the vote couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/votes")
    fun updateVote(@Valid @RequestBody vote: Vote): Mono<ResponseEntity<Vote>> {
        log.debug("REST request to update Vote : $vote")
        if (vote.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        return voteService.save(vote)
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map { result -> ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.id))
                            .body(result)
                        }
    }
    /**
     * `GET  /votes` : get all the votes.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of votes in body.
     */
    @GetMapping("/votes")    
    fun getAllVotes(): Mono<MutableList<Vote>> {
        log.debug("REST request to get all Votes")
        
        return voteService.findAll().collectList()
            }

    /**
     * `GET  /votes` : get all the votes as a stream.
     * @return the [Flux] of votes.
     */
    @GetMapping(value = ["/votes"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun getAllVotesAsStream(): Flux<Vote> {
        log.debug("REST request to get all Votes as a stream")
        return voteService.findAll()
    }

    /**
     * `GET  /votes/:id` : get the "id" vote.
     *
     * @param id the id of the vote to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the vote, or with status `404 (Not Found)`.
     */
    @GetMapping("/votes/{id}")
    fun getVote(@PathVariable id: String): Mono<ResponseEntity<Vote>> {
        log.debug("REST request to get Vote : $id")
        val vote = voteService.findOne(id)
        return ResponseUtil.wrapOrNotFound(vote)
    }
    /**
     *  `DELETE  /votes/:id` : delete the "id" vote.
     *
     * @param id the id of the vote to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/votes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun deleteVote(@PathVariable id: String): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete Vote : $id")
        return voteService.delete(id)                .map {
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build<Void>()
                }
    }
}
