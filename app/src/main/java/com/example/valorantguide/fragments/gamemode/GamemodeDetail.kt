package com.example.valorantguide.fragments.gamemode

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.valorantguide.R
import com.example.valorantguide.databinding.ActivityGamemodeDetailBinding
import com.example.valorantguide.mode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class GamemodeDetail : AppCompatActivity() {
    private lateinit var binding: ActivityGamemodeDetailBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamemodeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gamemodeId = intent.getStringExtra(GAMEMODE_ID_EXTRA)
        val gamemode = gamemodeList.find { it.uuid == gamemodeId }
        gamemode?.let {

            val toolbar: Toolbar = binding.toolbar
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.title = it.displayName

            try {
                doAsync {
                    val url = URL(it.displayIcon);
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    uiThread {
                        binding.cover.setImageBitmap(bmp)
                        if (mode == 1)
                            binding.cover.setBackgroundColor(R.color.gray)
                    }
                }
            } catch(e: Exception) {}
            binding.title.text = it.displayName

        }
    }
}