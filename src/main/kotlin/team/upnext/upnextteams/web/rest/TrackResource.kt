package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.domain.Track
import team.upnext.upnextteams.service.TrackService
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

private const val ENTITY_NAME = "track"
/**
 * REST controller for managing [team.upnext.upnextteams.domain.Track].
 */
@RestController
@RequestMapping("/api")
class TrackResource(
    private val trackService: TrackService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /tracks` : Create a new track.
     *
     * @param track the track to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new track, or with status `400 (Bad Request)` if the track has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tracks")
    fun createTrack(@Valid @RequestBody track: Track): Mono<ResponseEntity<Track>> {
        log.debug("REST request to save Track : $track")
        if (track.id != null) {
            throw BadRequestAlertException(
                "A new track cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        return trackService.save(track)
            .map { result -> 
                try {
                    ResponseEntity.created(URI("/api/tracks/${result.id}"))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id))
                        .body(result)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    /**
     * `PUT  /tracks` : Updates an existing track.
     *
     * @param track the track to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated track,
     * or with status `400 (Bad Request)` if the track is not valid,
     * or with status `500 (Internal Server Error)` if the track couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tracks")
    fun updateTrack(@Valid @RequestBody track: Track): Mono<ResponseEntity<Track>> {
        log.debug("REST request to update Track : $track")
        if (track.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        return trackService.save(track)
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map { result -> ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.id))
                            .body(result)
                        }
    }
    /**
     * `GET  /tracks` : get all the tracks.
     *
     * @param pageable the pagination information.
     * @param request a [ServerHttpRequest] request.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of tracks in body.
     */
    @GetMapping("/tracks")    
    fun getAllTracks(pageable: Pageable, request: ServerHttpRequest): Mono<ResponseEntity<Flux<Track>>> {
        log.debug("REST request to get a page of Tracks")
        
        return trackService.countAll()
            .map { PageImpl<Track>(listOf(), pageable, it) }
            .map { PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), it) }
            .map { ResponseEntity.ok().headers(it).body(trackService.findAll(pageable)) }
    }

    /**
     * `GET  /tracks/:id` : get the "id" track.
     *
     * @param id the id of the track to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the track, or with status `404 (Not Found)`.
     */
    @GetMapping("/tracks/{id}")
    fun getTrack(@PathVariable id: String): Mono<ResponseEntity<Track>> {
        log.debug("REST request to get Track : $id")
        val track = trackService.findOne(id)
        return ResponseUtil.wrapOrNotFound(track)
    }
    /**
     *  `DELETE  /tracks/:id` : delete the "id" track.
     *
     * @param id the id of the track to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/tracks/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun deleteTrack(@PathVariable id: String): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete Track : $id")
        return trackService.delete(id)                .map {
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build<Void>()
                }
    }
}
