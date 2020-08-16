package team.upnext.upnextteams.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.DBRef
import javax.validation.constraints.*

import java.io.Serializable

/**
 * A Track.
 */
@Document(collection = "track")
data class Track(
    @Id
    var id: String? = null,
    @DBRef
    @Field("audio")
    @JsonIgnoreProperties(value = ["tracks"], allowSetters = true)
    var audio: YoutubeAudio? = null,

    @DBRef
    @Field("metadata")
    @JsonIgnoreProperties(value = ["tracks"], allowSetters = true)
    var metadata: YoutubeMetadata? = null,

    @DBRef
    @Field("votes")
    var votes: MutableSet<Vote> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addVotes(vote: Vote): Track {
        this.votes.add(vote)
        return this
    }

    fun removeVotes(vote: Vote): Track {
        this.votes.remove(vote)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Track) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Track{" +
        "id=$id" +
        "}"


    companion object {
        private const val serialVersionUID = 1L
    }
}
