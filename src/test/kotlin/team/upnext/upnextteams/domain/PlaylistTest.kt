package team.upnext.upnextteams.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import team.upnext.upnextteams.web.rest.equalsVerifier

class PlaylistTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Playlist::class)
        val playlist1 = Playlist()
        playlist1.id = "id1"
        val playlist2 = Playlist()
        playlist2.id = playlist1.id
        assertThat(playlist1).isEqualTo(playlist2)
        playlist2.id = "id2"
        assertThat(playlist1).isNotEqualTo(playlist2)
        playlist1.id = null
        assertThat(playlist1).isNotEqualTo(playlist2)
    }
}
