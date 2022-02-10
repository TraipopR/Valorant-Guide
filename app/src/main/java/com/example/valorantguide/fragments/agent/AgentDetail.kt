package com.example.valorantguide.fragments.agent

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.valorantguide.databinding.ActivityAgentDetailBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL
import androidx.appcompat.widget.Toolbar
import android.graphics.drawable.BitmapDrawable
import com.example.valorantguide.Utils

class AgentDetail : AppCompatActivity() {
    private lateinit var binding: ActivityAgentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val agentId = intent.getStringExtra(AGENT_ID_EXTRA)
        val agent = agentFromId(agentId)
        if (agent != null) {
            val toolbar: Toolbar = binding.toolbar
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.title = agent.displayName
            agent.fullPortrait?.let {
                try {
                    doAsync {
                        val url = URL(it);
                        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        uiThread {
                            binding.cover.setImageBitmap(bmp)
                        }
                    }
                } catch(e: Exception) {}
            }
            agent.background?.let {
                try {
                    doAsync {
                        val url = URL(it);
                        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        uiThread {
                            binding.root.background = BitmapDrawable(binding.root.resources, bmp)
                        }
                    }
                } catch(e: Exception) {}
            }
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