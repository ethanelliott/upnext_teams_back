package team.upnext.upnextteams.domain

import java.io.Serializable

data class YoutubeAudio(
    var mimeType: String? = null,
    var bitrate: Int? = null,
    var duration: Int? = null,
    var channels: Int? = null,
    var container: String? = null,
    var url: String? = null
) : Serializable
