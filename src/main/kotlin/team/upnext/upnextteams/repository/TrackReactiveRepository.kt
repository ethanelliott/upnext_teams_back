package team.upnext.upnextteams.repository

import team.upnext.upnextteams.domain.Track
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

 /**
 * Spring Data MongoDB reactive repository for the Track entity.
 */
@SuppressWarnings("unused")
@Repository
interface TrackRepository: ReactiveMongoRepository<Track, String> {


 }