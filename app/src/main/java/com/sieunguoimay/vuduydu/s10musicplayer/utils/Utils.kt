package com.sieunguoimay.vuduydu.s10musicplayer.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager

object Utils {
    fun animateRecyclerView(context: Context, view: View){
//        val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
//        view.startAnimation(animation)
    }
    fun hideKeyBoard(activity: Activity){
        val im = activity.getSystemService(Activity.INPUT_METHOD_SERVICE)
        var view = activity.currentFocus
        if(view == null){
            view = View(activity)
        }
        (im as InputMethodManager).hideSoftInputFromWindow(view.windowToken,0)
    }
    fun showKeyBoard(activity: Activity){
        val im = activity.getSystemService(Activity.INPUT_METHOD_SERVICE)
        var view = activity.currentFocus
        if(view == null){
            view = View(activity)
        }
        (im as InputMethodManager).showSoftInput(view,InputMethodManager.SHOW_IMPLICIT)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission(context: Context, permissions: Array<String>): Array<String>? {
        var builder = StringBuilder()
        for (permission in permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                builder.append(permission).append(";")
            }
        }
        val s = builder.toString()
        return if (s.equals(""))  null
        else s.split(";").toTypedArray()
    }

    fun DPToPX(dps: Int, context:Context): Int {
        val scale = context.getResources().getDisplayMetrics().density
        return (dps * scale + 0.5f).toInt()
    }
}