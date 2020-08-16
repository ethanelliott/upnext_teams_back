package team.upnext.upnextteams.repository

import team.upnext.upnextteams.domain.Playlist
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

 /**
 * Spring Data MongoDB reactive repository for the Playlist entity.
 */
@SuppressWarnings("unused")
@Repository
interface PlaylistRepository: ReactiveMongoRepository<Playlist, String> {


 }