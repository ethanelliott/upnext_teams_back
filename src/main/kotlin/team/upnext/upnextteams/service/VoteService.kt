package team.upnext.upnextteams.service
import team.upnext.upnextteams.domain.Vote
import team.upnext.upnextteams.repository.VoteRepository
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Implementation for managing [Vote].
 */
@Service
class VoteService(
        private val voteRepository: VoteRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a vote.
     *
     * @param vote the entity to save.
     * @return the persisted entity.
     */
    fun save(vote: Vote): Mono<Vote> {
        log.debug("Request to save Vote : $vote")
        return voteRepository.save(vote)    }

    /**
     * Get all the votes.
     *
     * @return the list of entities.
     */
    fun findAll(): Flux<Vote> {
        log.debug("Request to get all Votes")
        return voteRepository.findAll()            
    }


    /**
    * Returns the number of votes available.
    *
    */
    fun countAll() = voteRepository.count()


    /**
     * Get one vote by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: String): Mono<Vote> {
        log.debug("Request to get Vote : $id")
        return voteRepository.findById(id)
    }

    /**
     * Delete the vote by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: String): Mono<Void> {
        log.debug("Request to delete Vote : $id")
        return voteRepository.deleteById(id)    }
}
