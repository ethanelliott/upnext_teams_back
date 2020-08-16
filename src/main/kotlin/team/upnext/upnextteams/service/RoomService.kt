package team.upnext.upnextteams.service
import team.upnext.upnextteams.domain.Room
import team.upnext.upnextteams.repository.RoomRepository
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Implementation for managing [Room].
 */
@Service
class RoomService(
        private val roomRepository: RoomRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a room.
     *
     * @param room the entity to save.
     * @return the persisted entity.
     */
    fun save(room: Room): Mono<Room> {
        log.debug("Request to save Room : $room")
        return roomRepository.save(room)    }

    /**
     * Get all the rooms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Flux<Room> {
        log.debug("Request to get all Rooms")
        return roomRepository.findAllBy(pageable)
    }


    /**
     * Get all the rooms with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    fun findAllWithEagerRelationships(pageable: Pageable) =
        roomRepository.findAllWithEagerRelationships(pageable)


    /**
    * Returns the number of rooms available.
    *
    */
    fun countAll() = roomRepository.count()


    /**
     * Get one room by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: String): Mono<Room> {
        log.debug("Request to get Room : $id")
        return roomRepository.findOneWithEagerRelationships(id)
    }

    /**
     * Delete the room by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: String): Mono<Void> {
        log.debug("Request to delete Room : $id")
        return roomRepository.deleteById(id)    }
}
