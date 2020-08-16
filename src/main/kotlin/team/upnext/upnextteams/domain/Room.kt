package team.upnext.upnextteams.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.DBRef
import javax.validation.constraints.*

import java.io.Serializable

/**
 * A Room.
 */
@Document(collection = "room")
data class Room(
    @Id
    var id: String? = null,
    @get: NotNull
    @Field("name")
    var name: String? = null,

    @get: NotNull
    @get: Size(max = 4)
    @Field("code")
    var code: String? = null,

    @Field("password")
    var password: String? = null,

    @DBRef
    @Field("users")
    var users: MutableSet<User> = mutableSetOf(),

    @DBRef
    @Field("player")
    var player: Player? = null,

    @DBRef
    @Field("playlist")
    var playlist: Playlist? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addUsers(user: User): Room {
        this.users.add(user)
        return this
    }

    fun removeUsers(user: User): Room {
        this.users.remove(user)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Room) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Room{" +
        "id=$id" +
        ", name='$name'" +
        ", code='$code'" +
        ", password='$password'" +
        "}"


    companion object {
        private const val serialVersionUID = 1L
    }
}
