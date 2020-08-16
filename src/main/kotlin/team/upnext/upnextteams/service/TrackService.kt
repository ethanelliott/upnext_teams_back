package team.upnext.upnextteams.service
import team.upnext.upnextteams.domain.Track
import team.upnext.upnextteams.repository.TrackRepository
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Implementation for managing [Track].
 */
@Service
class TrackService(
        private val trackRepository: TrackRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a track.
     *
     * @param track the entity to save.
     * @return the persisted entity.
     */
    fun save(track: Track): Mono<Track> {
        log.debug("Request to save Track : $track")
        return trackRepository.save(track)    }

    /**
     * Get all the tracks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Flux<Track> {
        log.debug("Request to get all Tracks")
        return trackRepository.findAllBy(pageable)
    }


    /**
    * Returns the number of tracks available.
    *
    */
    fun countAll() = trackRepository.count()


    /**
     * Get one track by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: String): Mono<Track> {
        log.debug("Request to get Track : $id")
        return trackRepository.findById(id)
    }

    /**
     * Delete the track by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: String): Mono<Void> {
        log.debug("Request to delete Track : $id")
        return trackRepository.deleteById(id)    }
}
