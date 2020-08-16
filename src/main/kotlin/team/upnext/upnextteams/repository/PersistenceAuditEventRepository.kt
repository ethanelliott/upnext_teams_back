package team.upnext.upnextteams.repository

import team.upnext.upnextteams.domain.PersistentAuditEvent
import org.springframework.data.domain.Pageable

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

/**
 * Spring Data MongoDB for the [PersistentAuditEvent] entity.
 */
interface PersistenceAuditEventRepository : ReactiveMongoRepository<PersistentAuditEvent, String>  {

    fun findByPrincipal(principal: String): Flux<PersistentAuditEvent>


    fun findAllByAuditEventDateBetween(
        fromDate: Instant,
        toDate: Instant,
        pageable: Pageable
    ): Flux<PersistentAuditEvent>

    fun findByAuditEventDateBefore(before: Instant): Flux<PersistentAuditEvent>

    fun findAllBy(pageable: Pageable): Flux<PersistentAuditEvent>

    fun countByAuditEventDateBetween(fromDate: Instant, toDate: Instant): Mono<Long>
}
