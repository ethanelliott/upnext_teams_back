package team.upnext.upnextteams.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import team.upnext.upnextteams.web.rest.equalsVerifier

class RoomTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Room::class)
        val room1 = Room()
        room1.id = "id1"
        val room2 = Room()
        room2.id = room1.id
        assertThat(room1).isEqualTo(room2)
        room2.id = "id2"
        assertThat(room1).isNotEqualTo(room2)
        room1.id = null
        assertThat(room1).isNotEqualTo(room2)
    }
}
