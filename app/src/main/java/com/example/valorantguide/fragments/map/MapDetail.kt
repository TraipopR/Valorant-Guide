package com.example.valorantguide.fragments.map

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.valorantguide.R
import com.example.valorantguide.databinding.ActivityMapDetailBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MapDetail : AppCompatActivity() {
    private lateinit var binding: ActivityMapDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapId = intent.getStringExtra(MAP_ID_EXTRA)
        val map = mapFromId(mapId)
        if (map != null) {
            try {
                doAsync {
                    val url = URL(map.displayIcon ?: map.splash);
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    uiThread {
                        binding.cover.setImageBitmap(bmp)
                    }
                }
            } catch(e: Exception) {}
            binding.title.text = map.displayName
        }
    }

    private fun mapFromId(mapId: String?): Map? {
        for (map in mapList) {
            if (map.uuid == mapId)
                return map
        }
        return null
    }
}