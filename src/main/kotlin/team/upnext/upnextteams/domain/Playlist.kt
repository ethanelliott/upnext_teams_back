package team.upnext.upnextteams.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.DBRef

import java.io.Serializable

/**
 * A Playlist.
 */
@Document(collection = "playlist")
data class Playlist(
    @Id
    var id: String? = null,
    @DBRef
    @Field("tracks")
    var tracks: MutableSet<Track> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addTracks(track: Track): Playlist {
        this.tracks.add(track)
        return this
    }

    fun removeTracks(track: Track): Playlist {
        this.tracks.remove(track)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Playlist) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Playlist{" +
        "id=$id" +
        "}"


    companion object {
        private const val serialVersionUID = 1L
    }
}
