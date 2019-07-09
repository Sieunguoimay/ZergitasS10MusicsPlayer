package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.FavouriteSongInfo
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Playlist
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.PlaylistSongInfo
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song

class DatabaseContract {
    interface Model{
        fun insertFavouriteSong(songId:Long, title:String)
        fun deleteFavouriteSong(songId:Long)
        fun getFavouriteSongs():ArrayList<FavouriteSongInfo>

        fun insertPlaylist(playlistTitle:String,songs:ArrayList<Song>):Long
        fun deltePlaylist(playlistId:Long)
        fun getAllPlaylists():Pair<LinkedHashMap<Long,Playlist>, ArrayList<PlaylistSongInfo>>
    }

    interface Presenter{
        fun likeOrUnlike(song:Song)
        //at the beginning of an activity this method will be call to load all the favourite songs from SQLite
        //and it is also called when user click on the love button.
        fun getFavouriteSongs(favouriteList:ArrayList<Song>
                              , songList:ArrayList<Song>
                              ,songMap:LinkedHashMap<Long,Int>
                              ,favouriteMap:LinkedHashMap<Int,Long>)

        fun createPlaylist(playlistTitle:String,songs:ArrayList<Song>)
        fun deletePlaylist(playlist:Playlist)
        fun getAllPlaylists(playlists:ArrayList<Playlist>
                            ,playlistSongMaps:ArrayList<LinkedHashMap<Int,Long>>
                            ,songList:ArrayList<Song>
                            ,songMap:LinkedHashMap<Long,Int>)
    }
    interface View{
        fun updateOnLike(song:Song)
        fun updateOnUnlike(song:Song)
        fun updateOnFavouritSongsLoaded(count:Int)
        fun updateOnPlaylistsLoaded(count:Int)
        fun showMessageOnPlaylistCreated(playlistId:Long)
    }
}

//steps:
//first, do the action like and dislike, which insert and delete the song into and from the database
//second, is the current song liked. by checking from the favorite list every time a new song is played
//third, display the entire favorite list