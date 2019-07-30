package com.sieunguoimay.vuduydu.s10musicplayer.visual_effects

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.Log
import android.view.View
import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.HomeScreenUtils.FlashScreenAnimation
import kotlinx.android.synthetic.main.player_bar_home_screen.*

object AnimationEffects {

    fun touchBoundEffect(view: View){

        view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).setListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(p0: Animator?) {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setListener(object:AnimatorListenerAdapter(){
                    override fun onAnimationEnd(p0: Animator?) {
                        view.scaleX = 1.0f
                        view.scaleY = 1.0f
                    }
                }).start()
            }
        }).start()
    }
}