package team.upnext.upnextteams.repository

import team.upnext.upnextteams.domain.Player
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

 /**
 * Spring Data MongoDB reactive repository for the Player entity.
 */
@SuppressWarnings("unused")
@Repository
interface PlayerRepository: ReactiveMongoRepository<Player, String> {


 }