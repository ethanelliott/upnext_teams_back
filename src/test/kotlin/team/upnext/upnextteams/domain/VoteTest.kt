package team.upnext.upnextteams.domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import team.upnext.upnextteams.web.rest.equalsVerifier

class VoteTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Vote::class)
        val vote1 = Vote()
        vote1.id = "id1"
        val vote2 = Vote()
        vote2.id = vote1.id
        assertThat(vote1).isEqualTo(vote2)
        vote2.id = "id2"
        assertThat(vote1).isNotEqualTo(vote2)
        vote1.id = null
        assertThat(vote1).isNotEqualTo(vote2)
    }
}
