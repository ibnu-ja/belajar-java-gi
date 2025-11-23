package io.ibnuja.hypersonic

import kotlinx.coroutines.runBlocking
import ru.stersh.subsonic.api.SubsonicApi as RawSubsonicApi
import ru.stersh.subsonic.api.model.*

/**
 * JVM overload of [ru.stersh.subsonic.api.SubsonicApi]
 * @see ru.stersh.subsonic.api.SubsonicApi
 */
@Suppress("unused")
class SubsonicApi(private val api: RawSubsonicApi) {

    fun ping(): EmptyResponse = runBlocking {
        api.ping().data
    }

    fun getSong(id: String): SongResponse = runBlocking {
        api.getSong(id).data
    }

    @JvmOverloads
    fun getRandomSongs(
        size: Int? = null,
        genre: String? = null,
        fromYear: Int? = null,
        toYear: Int? = null,
        musicFolderId: String? = null
    ): RandomSongsResponse = runBlocking {
        api.getRandomSongs(size, genre, fromYear, toYear, musicFolderId).data
    }

    fun getArtist(id: String): ArtistResponse = runBlocking {
        api.getArtist(id).data
    }

    fun getArtists(): ArtistsResponse = runBlocking {
        api.getArtists().data
    }

    @JvmOverloads
    fun getAlbumList(
        type: ListType,
        size: Int? = null,
        offset: Int? = null,
        fromYear: Int? = null,
        toYear: Int? = null,
        genre: String? = null,
        musicFolderId: String? = null
    ): AlbumListResponse = runBlocking {
        api.getAlbumList(type, size, offset, fromYear, toYear, genre, musicFolderId).data
    }

    @JvmOverloads
    fun getAlbumList2(
        type: ListType,
        size: Int? = null,
        offset: Int? = null,
        fromYear: Int? = null,
        toYear: Int? = null,
        genre: String? = null,
        musicFolderId: String? = null
    ): AlbumList2Response = runBlocking {
        api.getAlbumList2(type, size, offset, fromYear, toYear, genre, musicFolderId).data
    }

    fun getAlbum(id: String): AlbumResponse = runBlocking {
        api.getAlbum(id).data
    }

    fun getPlaylist(id: String): PlaylistResponse = runBlocking {
        api.getPlaylist(id).data
    }

    fun getPlaylists(): PlaylistsResponse = runBlocking {
        api.getPlaylists().data
    }

    fun getPlayQueue(): PlayQueueResponse = runBlocking {
        api.getPlayQueue().data
    }

    @JvmOverloads
    fun savePlayQueue(
        id: List<String>,
        current: String? = null,
        position: Long? = null
    ): EmptyResponse = runBlocking {
        api.savePlayQueue(id, current, position).data
    }

    @JvmOverloads
    fun star(
        id: List<String>? = null,
        albumId: List<String>? = null,
        artistId: List<String>? = null
    ): EmptyResponse = runBlocking {
        api.star(id, albumId, artistId).data
    }

    @JvmOverloads
    fun unstar(
        id: List<String>? = null,
        albumId: List<String>? = null,
        artistId: List<String>? = null
    ): EmptyResponse = runBlocking {
        api.unstar(id, albumId, artistId).data
    }

    @JvmOverloads
    fun getStarred(
        musicFolderId: String? = null
    ): StarredResponse = runBlocking {
        api.getStarred(musicFolderId).data
    }

    @JvmOverloads
    fun getStarred2(
        musicFolderId: String? = null
    ): Starred2Response = runBlocking {
        api.getStarred2(musicFolderId).data
    }

    @JvmOverloads
    fun scrobble(
        id: String,
        time: Long? = null,
        submission: Boolean? = null,
    ): EmptyResponse = runBlocking {
        api.scrobble(id, time, submission).data
    }

    @JvmOverloads
    fun search3(
        query: String,
        songCount: Int? = null,
        songOffset: Int? = null,
        albumCount: Int? = null,
        albumOffset: Int? = null,
        artistCount: Int? = null,
        artistOffset: Int? = null
    ): SearchResult3Response = runBlocking {
        api.search3(query, songCount, songOffset, albumCount, albumOffset, artistCount, artistOffset).data
    }

    @JvmOverloads
    fun getCoverArtUrl(
        id: String?,
        size: Int? = null,
        auth: Boolean = false
    ): String? {
        return api.getCoverArtUrl(id, size, auth)
    }

    fun downloadUrl(id: String): String {
        return api.downloadUrl(id)
    }

    fun streamUrl(id: String): String {
        return api.streamUrl(id)
    }

    @JvmOverloads
    fun avatarUrl(
        username: String,
        auth: Boolean = false
    ): String {
        return api.avatarUrl(username, auth)
    }
}
