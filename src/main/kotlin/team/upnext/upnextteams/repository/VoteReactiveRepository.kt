package team.upnext.upnextteams.repository

import team.upnext.upnextteams.domain.Vote
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

 /**
 * Spring Data MongoDB reactive repository for the Vote entity.
 */
@SuppressWarnings("unused")
@Repository
interface VoteRepository: ReactiveMongoRepository<Vote, String> {


 }