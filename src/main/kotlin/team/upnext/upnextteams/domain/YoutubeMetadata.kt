package team.upnext.upnextteams.domain

import java.io.Serializable

data class YoutubeMetadata(
    var likes: Int? = null,
    var dislikes: Int? = null,
    var duration: Int? = null,
    var description: String? = null,
    var title: String? = null,
    var author: Author? = null,
    var id: String? = null,
    var url: String? = null,
    var formats: String? = null
) : Serializable {
    class Author(
        var id: String? = null,
        var name: String? = null,
        var avatar: String? = null,
        var verified: Boolean? = null,
        var user: String? = null,
        var channelUrl: String? = null,
        var externalChannelUrl: String? = null,
        var userUrl: String? = null,
        var subscriberCount: Int? = null
    )
}
