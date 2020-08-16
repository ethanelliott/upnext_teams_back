package team.upnext.upnextteams.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.DBRef
import javax.validation.constraints.*

import java.io.Serializable

import team.upnext.upnextteams.domain.enumeration.PlayerState

/**
 * A Player.
 */
@Document(collection = "player")
data class Player(
    @Id
    var id: String? = null,
    @get: NotNull
    @Field("state")
    var state: PlayerState? = null,

    @DBRef
    @Field("current")
    @JsonIgnoreProperties(value = ["players"], allowSetters = true)
    var current: Track? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Player) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Player{" +
        "id=$id" +
        ", state='$state'" +
        "}"


    companion object {
        private const val serialVersionUID = 1L
    }
}
