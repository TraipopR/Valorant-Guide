package com.example.valorantguide.fragments.map

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.valorantguide.R
import com.example.valorantguide.databinding.ActivityMapDetailBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

val mapDetails = listOf(
    MapDetailData("7eaecc1b-4337-bbf6-6ab9-04b8f06b3319", R.drawable.ascent_minimap),
    MapDetailData("d960549e-485c-e861-8d71-aa9d1aed12a2", R.drawable.split_minimap),
    MapDetailData("b529448b-4d60-346e-e89e-00a4c527a405", R.drawable.fracture_minimap),
    MapDetailData("2c9d57ec-4431-9c5e-2939-8f9ef6dd5cba", R.drawable.bind_minimap),
    MapDetailData("2fb9a4fd-47b8-4e7d-a969-74b4046ebd53", R.drawable.breeze_minimap),
    MapDetailData("e2ad5c54-4114-a870-9641-8ea21279579a", R.drawable.icebox_minimap),
    MapDetailData("2bee0dc9-4ffe-519b-1cbd-7fbe763a6047", R.drawable.haven_minimap),
)

class MapDetail : AppCompatActivity() {
    private lateinit var binding: ActivityMapDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapId = intent.getStringExtra(MAP_ID_EXTRA)
        val map = mapFromId(mapId)
        if (map != null) {

            val toolbar: Toolbar = binding.toolbar
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.title = map.displayName

            val mapDetail = mapDetails.find { detail -> detail.uuid == mapId }
            if (mapDetail != null) {
                binding.contentImage.image.setImageResource(mapDetail.image)
            } else {
                try {
                    doAsync {
                        val url = URL(map.displayIcon ?: map.splash);
                        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        uiThread {
                            binding.contentImage.image.setImageBitmap(bmp)
                        }
                    }
                } catch(e: Exception) {}
            }
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