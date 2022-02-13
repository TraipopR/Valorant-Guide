package com.example.valorantguide.fragments.agent

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.valorantguide.databinding.ActivityAgentDetailBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL
import androidx.appcompat.widget.Toolbar
import android.graphics.drawable.BitmapDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.LeadingMarginSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.valorantguide.R
import com.example.valorantguide.databinding.AgentSkillBinding
import android.widget.Toast

import android.media.AudioManager

import android.media.MediaPlayer
import android.media.AudioAttributes
import android.util.Log
import com.example.valorantguide.NullableTypAdapterFactory
import com.example.valorantguide.mode
import com.google.gson.GsonBuilder


class AgentDetail : AppCompatActivity() {
    private lateinit var binding: ActivityAgentDetailBinding
    private var mediaPlayer: MediaPlayer? = null

    @SuppressLint("SetTextI18n", "ResourceAsColor")
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
            toolbar.title = "${agent.displayName} - ${agent.role?.displayName ?: ""}"

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

            binding.desc.text = textIndent(agent.description.replace("\n", ""))

            binding.role.text = "${agent.role?.displayName ?: ""}"
            binding.name.text = agent.displayName

            binding.recyclerViewSkill.apply {
                layoutManager = object : LinearLayoutManager(this@AgentDetail, LinearLayoutManager.VERTICAL ,false){ override fun canScrollVertically(): Boolean { return false } }
                adapter = SkillAgentAdapter(agent.abilities)
            }

            agent.role?.let {
                binding.roleDesc.text = textIndent(it.description.replace("\n", ""))
                binding.roleTitle.text = "Role - ${it.displayName}"
                try {
                    doAsync {
                        val url = URL(it.displayIcon);
                        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        uiThread {
                            binding.roleIcon.setImageBitmap(bmp)
                            if (mode == 1)
                                binding.roleIcon.setBackgroundColor(R.color.gray)
                        }
                    }
                } catch(e: Exception) {}
            }

            if (agent.voiceLine == null) {
                doAsync {
                    val agentJson = URL("https://valorant-api.com/v1/agents/$agentId").readText()
                    Log.d(javaClass.simpleName, agentJson)
                    val gson = GsonBuilder().registerTypeAdapterFactory(NullableTypAdapterFactory()).create()
                    val temp = gson.fromJson(agentJson, ResponseAgent::class.java).data
                    agentList.forEach {
                        if (it.uuid == agentId)
                            it.voiceLine = temp.voiceLine
                    }

                    if (temp.voiceLine != null)
                        uiThread {
                            playAudio(temp.voiceLine!!.mediaList[0].wave)
                        }
                }
            } else {
                playAudio(agent.voiceLine!!.mediaList[0].wave)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        try {
            if (mediaPlayer?.isPlaying == true) {
                // pausing the media player if media player
                // is playing we are calling below line to
                // stop our media player.
                mediaPlayer!!.stop();
                mediaPlayer!!.reset();
                mediaPlayer!!.release();

            }
        } catch(e: Exception) {}
    }

    private fun playAudio(audioUrl: String) {
        // initializing media player
        mediaPlayer = MediaPlayer()

        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer!!.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())

        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer!!.setDataSource(audioUrl)
            // below line is use to prepare
            // and start our media player.
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()

            mediaPlayer!!.setOnCompletionListener {
                try {
                    it.stop();
                    it.reset();
                    it.release();
                } catch(e: Exception) {}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun textIndent(text: String): SpannableString {
        val spannable = SpannableString(text)
        val span = LeadingMarginSpan.Standard(50, 0)
        spannable.setSpan(span, 0, spannable.count(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    private fun agentFromId(agentId: String?): Agent? {
        for(agent in agentList) {
            if (agent.uuid == agentId)
                return agent
        }
        return null
    }
}

class SkillAgentViewHolder(
    private val agentSkillBinding: AgentSkillBinding
): RecyclerView.ViewHolder(agentSkillBinding.root) {

    @SuppressLint("ResourceAsColor", "SetTextI18n")

    fun bindSkill(skill: Abilities) {
        if (skill.displayIcon != null)
            try {
                doAsync {
                    val url = URL(skill.displayIcon);
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    uiThread {
                        agentSkillBinding.skillIcon.setImageBitmap(bmp)
                        if (mode == 1)
                            agentSkillBinding.skillIcon.setBackgroundColor(R.color.gray)
                    }
                }
            } catch(e: Exception) {}
        agentSkillBinding.skillName.text = "${skill.slot} - ${skill.displayName}"
        agentSkillBinding.skillDesc.text = skill.description.replace("\n","")
    }
}

class SkillAgentAdapter(
    private val skills: List<Abilities>
): RecyclerView.Adapter<SkillAgentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillAgentViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = AgentSkillBinding.inflate(from, parent, false)
        return SkillAgentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillAgentViewHolder, position: Int) {
        holder.bindSkill(skills[position])
    }

    override fun getItemCount(): Int = skills.size
}