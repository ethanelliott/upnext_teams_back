package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.domain.Room
import team.upnext.upnextteams.service.RoomService
import team.upnext.upnextteams.web.rest.errors.BadRequestAlertException

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.reactive.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import javax.validation.Valid
import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "room"
/**
 * REST controller for managing [team.upnext.upnextteams.domain.Room].
 */
@RestController
@RequestMapping("/api")
class RoomResource(
    private val roomService: RoomService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /rooms` : Create a new room.
     *
     * @param room the room to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new room, or with status `400 (Bad Request)` if the room has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rooms")
    fun createRoom(@Valid @RequestBody room: Room): Mono<ResponseEntity<Room>> {
        log.debug("REST request to save Room : $room")
        if (room.id != null) {
            throw BadRequestAlertException(
                "A new room cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        return roomService.save(room)
            .map { result -> 
                try {
                    ResponseEntity.created(URI("/api/rooms/${result.id}"))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id))
                        .body(result)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    /**
     * `PUT  /rooms` : Updates an existing room.
     *
     * @param room the room to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated room,
     * or with status `400 (Bad Request)` if the room is not valid,
     * or with status `500 (Internal Server Error)` if the room couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rooms")
    fun updateRoom(@Valid @RequestBody room: Room): Mono<ResponseEntity<Room>> {
        log.debug("REST request to update Room : $room")
        if (room.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        return roomService.save(room)
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map { result -> ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.id))
                            .body(result)
                        }
    }
    /**
     * `GET  /rooms` : get all the rooms.
     *
     * @param pageable the pagination information.
     * @param request a [ServerHttpRequest] request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the [ResponseEntity] with status `200 (OK)` and the list of rooms in body.
     */
    @GetMapping("/rooms")    
    fun getAllRooms(pageable: Pageable, request: ServerHttpRequest, @RequestParam(required = false, defaultValue = "false") eagerload: Boolean): Mono<ResponseEntity<Flux<Room>>> {
        log.debug("REST request to get a page of Rooms")
        
        return roomService.countAll()
            .map { PageImpl<Room>(listOf(), pageable, it) }
            .map { PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), it) }
            .map { ResponseEntity.ok().headers(it).body(roomService.findAll(pageable)) }
    }

    /**
     * `GET  /rooms/:id` : get the "id" room.
     *
     * @param id the id of the room to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the room, or with status `404 (Not Found)`.
     */
    @GetMapping("/rooms/{id}")
    fun getRoom(@PathVariable id: String): Mono<ResponseEntity<Room>> {
        log.debug("REST request to get Room : $id")
        val room = roomService.findOne(id)
        return ResponseUtil.wrapOrNotFound(room)
    }
    /**
     *  `DELETE  /rooms/:id` : delete the "id" room.
     *
     * @param id the id of the room to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/rooms/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun deleteRoom(@PathVariable id: String): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete Room : $id")
        return roomService.delete(id)                .map {
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build<Void>()
                }
    }
}
