package team.upnext.upnextteams.repository

import team.upnext.upnextteams.domain.User

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import java.time.Instant

/**
 * Spring Data MongoDB repository for the [User] entity.
 */
@Repository
interface UserRepository : ReactiveMongoRepository<User, String> {

    fun findOneByActivationKey(activationKey: String): Mono<User>

    fun findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(dateTime: Instant): Flux<User>

    fun findOneByResetKey(resetKey: String): Mono<User>

    fun findOneByEmailIgnoreCase(email: String?): Mono<User>

    fun findOneByLogin(login: String): Mono<User>


    fun findAllByLoginNot(pageable: Pageable, login: String): Flux<User>

    fun countAllByLoginNot(anonymousUser: String): Mono<Long>
}
