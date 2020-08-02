package team.upnext.upnextteams.web.rest

import com.fasterxml.jackson.annotation.JsonCreator
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class AccountResource {

    internal class AccountResourceException : RuntimeException()

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws AccountResourceException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    fun getAccount(): Mono<UserVM> {
        return ReactiveSecurityContextHolder.getContext()
            .map { context -> toUser(context) }
            .switchIfEmpty(Mono.error(AccountResourceException()))
    }

    fun toUser(context: SecurityContext): UserVM {
        val authentication = context.authentication
        val login = when (authentication.principal) {
            is UserDetails -> {
                (authentication.principal as UserDetails).username
            }
            is String -> {
                authentication.principal as String
            }
            else -> {
                throw AccountResourceException()
            }
        }
        val authorities = authentication.authorities
            .map { grantedAuthority -> grantedAuthority.authority }
            .toSet()
        return UserVM(login, authorities)
    }

    data class UserVM @JsonCreator constructor(val login: String, val authorities: Set<String>) {

        fun isActivated() = true
    }
}
