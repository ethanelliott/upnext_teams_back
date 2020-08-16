package team.upnext.upnextteams.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import team.upnext.upnextteams.web.rest.equalsVerifier

class PlayerTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Player::class)
        val player1 = Player()
        player1.id = "id1"
        val player2 = Player()
        player2.id = player1.id
        assertThat(player1).isEqualTo(player2)
        player2.id = "id2"
        assertThat(player1).isNotEqualTo(player2)
        player1.id = null
        assertThat(player1).isNotEqualTo(player2)
    }
}
