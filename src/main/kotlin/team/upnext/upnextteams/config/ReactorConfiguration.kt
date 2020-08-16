package team.upnext.upnextteams.config

import io.github.jhipster.config.JHipsterConstants
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import reactor.core.publisher.Hooks

@Configuration
@Profile("!" + JHipsterConstants.SPRING_PROFILE_PRODUCTION)
class ReactorConfiguration {
    fun ReactorConfiguration() = Hooks.onOperatorDebug()
}
