package team.upnext.upnextteams.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import team.upnext.upnextteams.web.rest.equalsVerifier

class TrackTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Track::class)
        val track1 = Track()
        track1.id = "id1"
        val track2 = Track()
        track2.id = track1.id
        assertThat(track1).isEqualTo(track2)
        track2.id = "id2"
        assertThat(track1).isNotEqualTo(track2)
        track1.id = null
        assertThat(track1).isNotEqualTo(track2)
    }
}
