package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.domain.Player
import team.upnext.upnextteams.service.PlayerService
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

private const val ENTITY_NAME = "player"
/**
 * REST controller for managing [team.upnext.upnextteams.domain.Player].
 */
@RestController
@RequestMapping("/api")
class PlayerResource(
    private val playerService: PlayerService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /players` : Create a new player.
     *
     * @param player the player to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new player, or with status `400 (Bad Request)` if the player has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/players")
    fun createPlayer(@Valid @RequestBody player: Player): Mono<ResponseEntity<Player>> {
        log.debug("REST request to save Player : $player")
        if (player.id != null) {
            throw BadRequestAlertException(
                "A new player cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        return playerService.save(player)
            .map { result -> 
                try {
                    ResponseEntity.created(URI("/api/players/${result.id}"))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id))
                        .body(result)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    /**
     * `PUT  /players` : Updates an existing player.
     *
     * @param player the player to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated player,
     * or with status `400 (Bad Request)` if the player is not valid,
     * or with status `500 (Internal Server Error)` if the player couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/players")
    fun updatePlayer(@Valid @RequestBody player: Player): Mono<ResponseEntity<Player>> {
        log.debug("REST request to update Player : $player")
        if (player.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        return playerService.save(player)
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map { result -> ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.id))
                            .body(result)
                        }
    }
    /**
     * `GET  /players` : get all the players.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of players in body.
     */
    @GetMapping("/players")    
    fun getAllPlayers(): Mono<MutableList<Player>> {
        log.debug("REST request to get all Players")
        
        return playerService.findAll().collectList()
            }

    /**
     * `GET  /players` : get all the players as a stream.
     * @return the [Flux] of players.
     */
    @GetMapping(value = ["/players"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun getAllPlayersAsStream(): Flux<Player> {
        log.debug("REST request to get all Players as a stream")
        return playerService.findAll()
    }

    /**
     * `GET  /players/:id` : get the "id" player.
     *
     * @param id the id of the player to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the player, or with status `404 (Not Found)`.
     */
    @GetMapping("/players/{id}")
    fun getPlayer(@PathVariable id: String): Mono<ResponseEntity<Player>> {
        log.debug("REST request to get Player : $id")
        val player = playerService.findOne(id)
        return ResponseUtil.wrapOrNotFound(player)
    }
    /**
     *  `DELETE  /players/:id` : delete the "id" player.
     *
     * @param id the id of the player to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/players/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun deletePlayer(@PathVariable id: String): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete Player : $id")
        return playerService.delete(id)                .map {
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build<Void>()
                }
    }
}
