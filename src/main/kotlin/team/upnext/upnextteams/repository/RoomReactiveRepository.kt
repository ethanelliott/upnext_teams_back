package team.upnext.upnextteams.repository

import team.upnext.upnextteams.domain.Room
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

 /**
 * Spring Data MongoDB reactive repository for the Room entity.
 */
@SuppressWarnings("unused")
@Repository
interface RoomRepository: ReactiveMongoRepository<Room, String> {


    fun findAllBy(pageable: Pageable): Flux<Room> 

    @Query("{}")
    fun findAllWithEagerRelationships(pageable: Pageable): Flux<Room> 

    @Query("{}")
    fun findAllWithEagerRelationships(): Flux<Room> 

    @Query("{'id': ?0}")
    fun findOneWithEagerRelationships(id: String): Mono<Room> 

 }