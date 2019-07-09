package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.AllSongsScreenFragment.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.AllSongsScreenFragment.*

private const val TAG = "ViewPagerAdapter"
class ViewPagerAdapter(
    fm: FragmentManager,
    var totalTabs:Int
): FragmentPagerAdapter(fm){
    override fun getItem(p0: Int): Fragment {
        Log.d(TAG,"item id "+p0)
        return when(p0){
            0-> SongsFragment()
            1-> FavouriteFragment()
            2-> AlbumsFragment()
            3-> PlaylistFragment()
            4-> ArtistsFragment()
            //more go here
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }

}