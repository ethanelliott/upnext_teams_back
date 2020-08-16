package team.upnext.upnextteams.service
import team.upnext.upnextteams.domain.Playlist
import team.upnext.upnextteams.repository.PlaylistRepository
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Implementation for managing [Playlist].
 */
@Service
class PlaylistService(
        private val playlistRepository: PlaylistRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a playlist.
     *
     * @param playlist the entity to save.
     * @return the persisted entity.
     */
    fun save(playlist: Playlist): Mono<Playlist> {
        log.debug("Request to save Playlist : $playlist")
        return playlistRepository.save(playlist)    }

    /**
     * Get all the playlists.
     *
     * @return the list of entities.
     */
    fun findAll(): Flux<Playlist> {
        log.debug("Request to get all Playlists")
        return playlistRepository.findAll()            
    }


    /**
    * Returns the number of playlists available.
    *
    */
    fun countAll() = playlistRepository.count()


    /**
     * Get one playlist by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: String): Mono<Playlist> {
        log.debug("Request to get Playlist : $id")
        return playlistRepository.findById(id)
    }

    /**
     * Delete the playlist by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: String): Mono<Void> {
        log.debug("Request to delete Playlist : $id")
        return playlistRepository.deleteById(id)    }
}
