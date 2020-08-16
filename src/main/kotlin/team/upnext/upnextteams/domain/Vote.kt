package team.upnext.upnextteams.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.DBRef
import javax.validation.constraints.*

import java.io.Serializable

/**
 * A Vote.
 */
@Document(collection = "vote")
data class Vote(
    @Id
    var id: String? = null,
    @get: NotNull
    @Field("value")
    var value: Int? = null,

    @DBRef
    @Field("user")
    var user: User? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vote) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Vote{" +
        "id=$id" +
        ", value=$value" +
        "}"


    companion object {
        private const val serialVersionUID = 1L
    }
}
