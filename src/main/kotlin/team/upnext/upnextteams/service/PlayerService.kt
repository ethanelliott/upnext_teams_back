package team.upnext.upnextteams.service
import team.upnext.upnextteams.domain.Player
import team.upnext.upnextteams.repository.PlayerRepository
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Implementation for managing [Player].
 */
@Service
class PlayerService(
        private val playerRepository: PlayerRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a player.
     *
     * @param player the entity to save.
     * @return the persisted entity.
     */
    fun save(player: Player): Mono<Player> {
        log.debug("Request to save Player : $player")
        return playerRepository.save(player)    }

    /**
     * Get all the players.
     *
     * @return the list of entities.
     */
    fun findAll(): Flux<Player> {
        log.debug("Request to get all Players")
        return playerRepository.findAll()            
    }


    /**
    * Returns the number of players available.
    *
    */
    fun countAll() = playerRepository.count()


    /**
     * Get one player by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: String): Mono<Player> {
        log.debug("Request to get Player : $id")
        return playerRepository.findById(id)
    }

    /**
     * Delete the player by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: String): Mono<Void> {
        log.debug("Request to delete Player : $id")
        return playerRepository.deleteById(id)    }
}
