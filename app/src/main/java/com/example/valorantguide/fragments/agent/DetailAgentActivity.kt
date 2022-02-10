package com.example.valorantguide.fragments.agent

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.valorantguide.databinding.ActivityDetailBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class DetailAgentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val agentId = intent.getStringExtra(AGENT_ID_EXTRA)
        val agent = agentFromId(agentId)
        if (agent != null) {
            try {
                doAsync {
                    val url = URL(agent.displayIcon);
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    uiThread {
                        binding.cover.setImageBitmap(bmp)
                    }
                }
            } catch(e: Exception) {}
            binding.title.text = agent.displayName
            binding.desc.text = agent.description
        }
    }

    private fun agentFromId(agentId: String?): Agent? {
        for(agent in agentList) {
            if (agent.uuid == agentId)
                return agent
        }
        return null
    }
}