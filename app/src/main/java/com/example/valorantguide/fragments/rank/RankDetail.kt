package com.example.valorantguide.fragments.rank

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.valorantguide.databinding.ActivityRankDetailBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class RankDetail : AppCompatActivity() {
    private lateinit var binding: ActivityRankDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rankId = intent.getLongExtra(RANK_ID_EXTRA, -1)
        val rank = rankFromId(rankId)
        if (rank != null) {

            val toolbar: Toolbar = binding.toolbar
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.title = rank.tierName

            try {
                doAsync {
                    val url = URL(rank.largeIcon)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    uiThread {
                        binding.cover.setImageBitmap(bmp)
                    }
                }
            } catch(e: Exception) {}
            binding.title.text = rank.tierName
        }
    }

    private fun rankFromId(rankId: Long): Tier? {
        for (rank in rankList) {
            if (rank.tier == rankId)
                return rank
        }
        return null
    }
}