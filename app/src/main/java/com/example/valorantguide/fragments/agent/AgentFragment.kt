package com.example.valorantguide.fragments.agent

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.valorantguide.NullableTypAdapterFactory
import com.example.valorantguide.Utils
import com.example.valorantguide.databinding.CardCellBinding
import com.example.valorantguide.databinding.FragmentAgentBinding
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.gson.GsonBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

var agentList = mutableListOf<Agent>()
const val AGENT_ID_EXTRA = "agentExtra"

class AgentFragment : Fragment(), AgentClickListener {
    private lateinit var binding: FragmentAgentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgentBinding.inflate(layoutInflater)

        render()
        doAsync {
            if (agentList.isEmpty()) {
                val agentJson = URL("https://valorant-api.com/v1/agents?language=th-TH&isPlayableCharacter=true").readText()
                Log.d(javaClass.simpleName, agentJson)
                val gson = GsonBuilder().registerTypeAdapterFactory(NullableTypAdapterFactory()).create()
                agentList = gson.fromJson(agentJson, ResponseAgent::class.java).data
            }

            uiThread {
                render()
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun render() {
        val fragmentAgent = this

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = CardAgentAdapter(agentList, fragmentAgent)
        }
    }

    override fun onClick(agent: Agent) {
        val intent = Intent(activity, AgentDetail::class.java)
        intent.putExtra(AGENT_ID_EXTRA, agent.uuid)
        startActivity(intent)
    }
}

class CardAgentViewHolder(
    private val cardCellBinding: CardCellBinding,
    private val clickListener: AgentClickListener
): RecyclerView.ViewHolder(cardCellBinding.root) {
    fun bindAgent(agent: Agent?) {
        if (agent == null) {
            cardCellBinding.cover.createSkeleton().showSkeleton()
            cardCellBinding.name.createSkeleton().showSkeleton()
        } else {
            try {
                doAsync {
                    val url = URL(agent.displayIcon)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    uiThread {
                        cardCellBinding.cover.setImageBitmap(bmp)
                    }
                }
            } catch(e: Exception) {}
            cardCellBinding.name.text = agent.displayName

            cardCellBinding.cardView.setOnClickListener {
                clickListener.onClick(agent)
            }
        }
    }
}

class CardAgentAdapter(
    private val agents: List<Agent>,
    private val clickListener: AgentClickListener
): RecyclerView.Adapter<CardAgentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAgentViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardCellBinding.inflate(from, parent, false)
        return CardAgentViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CardAgentViewHolder, position: Int) {
        holder.bindAgent(if (agents.isEmpty()) null else agents[position])
    }

    override fun getItemCount(): Int = if (agents.isEmpty()) 10 else agents.size
}

// Model
interface AgentClickListener {
    fun onClick(agent: Agent)
}

class ResponseAgent(
    val data: MutableList<Agent>,
    val status: Int
)

class Agent(
    val abilities: ArrayList<Abilities>,
    val background: String? = null,
    val bustPortrait: String? = null,
    val characterTags: ArrayList<String>? = null,
    val description: String,
    val developerName: String,
    val displayIcon: String,
    val displayIconSmall: String,
    val displayName: String,
    val fullPortrait: String? = null,
    val isAvailableForTest: Boolean,
    val isBaseContent: Boolean,
    val isFullPortraitRightFacing: Boolean,
    val isPlayableCharacter: Boolean,
    val killfeedPortrait: Boolean,
    val role: Role? = null,
    val uuid: String,
    val voiceLine: VoiceLine? = null,
)

class Abilities(
    val slot: Slot,
    val displayName: String,
    val displayIcon: String? = null,
    val description: String
)

enum class Slot(val value: String) {
    Ability1("Ability1"),
    Ability2("Ability2"),
    Grenade("Grenade"),
    Passive("Passive"),
    Ultimate("Ultimate");

    companion object {
        public fun fromValue(value: String): Slot = when (value) {
            "Ability1" -> Ability1
            "Ability2" -> Ability2
            "Grenade"  -> Grenade
            "Passive"  -> Passive
            "Ultimate" -> Ultimate
            else       -> throw IllegalArgumentException()
        }
    }
}

class Role(
    val uuid: String,
    val assetPath: String,
    val displayIcon: String,
    val displayName: DisplayName,
    val description: String
)

enum class DisplayName(val value: String) {
    Controller("Controller"),
    Duelist("Duelist"),
    Initiator("Initiator"),
    Sentinel("Sentinel");

    companion object {
        public fun fromValue(value: String): DisplayName = when (value) {
            "Controller" -> Controller
            "Duelist"    -> Duelist
            "Initiator"  -> Initiator
            "Sentinel"   -> Sentinel
            else         -> throw IllegalArgumentException()
        }
    }
}

class VoiceLine(
    val maxDuration: Double,
    val minDuration: Double,
    val mediaList: ArrayList<Media>
)

class Media(
    val id: Long,
    val wave: String,
    val wwise: String
)