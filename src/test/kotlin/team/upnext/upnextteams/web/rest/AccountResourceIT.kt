package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.UpNextTeamsApp
import team.upnext.upnextteams.security.ADMIN
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * Integration tests for the {@link AccountResource} REST controller.
 */
@AutoConfigureWebTestClient
@SpringBootTest(classes = [UpNextTeamsApp::class])
class AccountResourceIT {

    @Autowired
    private lateinit var accountWebTestClient: WebTestClient

    @Test
    @WithMockUser(username = TEST_USER_LOGIN, authorities = [ADMIN])
    fun testGetExistingAccount() {
            accountWebTestClient.get().uri("/api/account")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.login").isEqualTo(TEST_USER_LOGIN)
                .jsonPath("$.authorities").isEqualTo(ADMIN)
    }

    @Test
    fun testGetUnknownAccount() {
        accountWebTestClient.get().uri("/api/account")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is4xxClientError
    }
}
