package team.upnext.upnextteams.web.rest

import team.upnext.upnextteams.UpNextTeamsApp
import team.upnext.upnextteams.web.rest.vm.LoginVM
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * Integration tests for the [UserJWTController] REST controller.
 */
@AutoConfigureWebTestClient
@SpringBootTest(classes = [UpNextTeamsApp::class])
class UserJWTControllerIT {


    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    @Throws(Exception::class)
    fun testAuthorize() {
        val login = LoginVM(username ="test", password = "test")
        webTestClient.post().uri("/api/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(login))
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueMatches("Authorization", "Bearer .+")
            .expectBody()
            .jsonPath("\$.id_token").isNotEmpty
    }

    @Test
    @Throws(Exception::class)
    fun testAuthorizeWithRememberMe() {
        val login = LoginVM(
            username ="test",
            password = "test",
            isRememberMe = true
        )
        webTestClient.post().uri("/api/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(login))
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueMatches("Authorization", "Bearer .+")
            .expectBody()
            .jsonPath("\$.id_token").isNotEmpty
    }

    @Test
    @Throws(Exception::class)
    fun testAuthorizeFails() {
        val login = LoginVM(username = "wrong-user", password = "wrong password")
        webTestClient.post().uri("/api/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(login))
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().doesNotExist("Authorization")
            .expectBody()
            .jsonPath("\$.id_token").doesNotExist()
    }
}
