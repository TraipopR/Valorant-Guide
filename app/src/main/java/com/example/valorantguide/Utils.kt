package com.example.valorantguide

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class Utils: IUtils {
    override fun LoadImgAsync(url: String, element: CoordinatorLayout) {
        try {
            doAsync {
                val url = URL(url);
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                uiThread {
                    element.background = BitmapDrawable(element.resources, bmp)
                }
            }
        } catch (e: Exception) {}
    }
    override fun LoadImgAsync(url: String, element: ImageView) {
        try {
            doAsync {
                val url = URL(url);
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                uiThread {
                    element.setImageBitmap(bmp)
                }
            }
        } catch (e: Exception) {}
    }
}

interface IUtils {
    fun LoadImgAsync(url: String, element: ImageView)
    fun LoadImgAsync(url: String, element: CoordinatorLayout)
}