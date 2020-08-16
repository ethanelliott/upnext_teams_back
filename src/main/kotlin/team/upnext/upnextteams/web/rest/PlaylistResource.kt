package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.domain.Playlist
import team.upnext.upnextteams.service.PlaylistService
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

import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "playlist"
/**
 * REST controller for managing [team.upnext.upnextteams.domain.Playlist].
 */
@RestController
@RequestMapping("/api")
class PlaylistResource(
    private val playlistService: PlaylistService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /playlists` : Create a new playlist.
     *
     * @param playlist the playlist to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new playlist, or with status `400 (Bad Request)` if the playlist has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/playlists")
    fun createPlaylist(@RequestBody playlist: Playlist): Mono<ResponseEntity<Playlist>> {
        log.debug("REST request to save Playlist : $playlist")
        if (playlist.id != null) {
            throw BadRequestAlertException(
                "A new playlist cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        return playlistService.save(playlist)
            .map { result -> 
                try {
                    ResponseEntity.created(URI("/api/playlists/${result.id}"))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id))
                        .body(result)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    /**
     * `PUT  /playlists` : Updates an existing playlist.
     *
     * @param playlist the playlist to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated playlist,
     * or with status `400 (Bad Request)` if the playlist is not valid,
     * or with status `500 (Internal Server Error)` if the playlist couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/playlists")
    fun updatePlaylist(@RequestBody playlist: Playlist): Mono<ResponseEntity<Playlist>> {
        log.debug("REST request to update Playlist : $playlist")
        if (playlist.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        return playlistService.save(playlist)
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map { result -> ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.id))
                            .body(result)
                        }
    }
    /**
     * `GET  /playlists` : get all the playlists.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of playlists in body.
     */
    @GetMapping("/playlists")    
    fun getAllPlaylists(): Mono<MutableList<Playlist>> {
        log.debug("REST request to get all Playlists")
        
        return playlistService.findAll().collectList()
            }

    /**
     * `GET  /playlists` : get all the playlists as a stream.
     * @return the [Flux] of playlists.
     */
    @GetMapping(value = ["/playlists"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun getAllPlaylistsAsStream(): Flux<Playlist> {
        log.debug("REST request to get all Playlists as a stream")
        return playlistService.findAll()
    }

    /**
     * `GET  /playlists/:id` : get the "id" playlist.
     *
     * @param id the id of the playlist to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the playlist, or with status `404 (Not Found)`.
     */
    @GetMapping("/playlists/{id}")
    fun getPlaylist(@PathVariable id: String): Mono<ResponseEntity<Playlist>> {
        log.debug("REST request to get Playlist : $id")
        val playlist = playlistService.findOne(id)
        return ResponseUtil.wrapOrNotFound(playlist)
    }
    /**
     *  `DELETE  /playlists/:id` : delete the "id" playlist.
     *
     * @param id the id of the playlist to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/playlists/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun deletePlaylist(@PathVariable id: String): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete Playlist : $id")
        return playlistService.delete(id)                .map {
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build<Void>()
                }
    }
}
