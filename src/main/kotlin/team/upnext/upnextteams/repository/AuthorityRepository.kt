package team.upnext.upnextteams.repository

import team.upnext.upnextteams.domain.Authority
import org.springframework.data.mongodb.repository.ReactiveMongoRepository


/**
 * Spring Data MongoDB repository for the [Authority] entity.
 */

interface AuthorityRepository : ReactiveMongoRepository<Authority, String> {
}
