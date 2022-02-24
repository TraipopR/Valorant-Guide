package com.example.valorantguide.fragments.agent

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.valorantguide.NullableTypAdapterFactory
import com.example.valorantguide.Utils
import com.example.valorantguide.databinding.CardCellBinding
import com.example.valorantguide.databinding.FragmentAgentBinding
import com.example.valorantguide.fragments.BaseFragment
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import kotlinx.parcelize.Parcelize
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

var agentList = mutableListOf<Agent>()
const val AGENT_ID_EXTRA = "agentExtra"

class AgentFragment : BaseFragment(), AgentClickListener {
    private lateinit var binding: FragmentAgentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgentBinding.inflate(layoutInflater)

        // set appbar has option menu
        setHasOptionsMenu(true)

        render()

        doAsync {
            if (agentList.isEmpty()) {
                val agentJson = URL("https://valorant-api.com/v1/agents?language=th-TH&isPlayableCharacter=true").readText()
                Log.d(javaClass.simpleName, agentJson)
                try {
                    val gson = GsonBuilder().registerTypeAdapterFactory(NullableTypAdapterFactory()).create()
                    agentList = gson.fromJson(agentJson, ResponseAgents::class.java).data
                } catch (error: JsonParseException) {
                    uiThread {
                        binding.errorContainer.visibility = View.VISIBLE
                        binding.errorMessage.text = error.message
                    }
                }
            }

            uiThread {
                render(agentList.size)
                if (agentList.isNotEmpty()) {
                    binding.roleContainer.visibility = View.VISIBLE
                    binding.roleESportContainer.visibility = View.VISIBLE

                    val adapterRole = ViewPagerRoleAdapter(requireActivity())
                    val roleList = agentList.map { it.role }.distinctBy { it?.uuid }
                    roleList.forEach {
                        if (it != null) {
                            adapterRole.addFragment(RoleFragment(), it)
                        }
                    }
                    binding.viewPagerRole.adapter = adapterRole

                    TabLayoutMediator(binding.tabLayoutRole, binding.viewPagerRole) { tab, position ->
                        tab.text = roleList[position]?.displayName?.value
                    }.attach()

                    val adapterRoleESport = ViewPagerRoleESportAdapter(requireActivity())
                    roleESportList.forEach {
                        adapterRoleESport.addFragment(RoleESportFragment(), it)
                    }
                    binding.viewPagerRoleEsport.adapter = adapterRoleESport

                    TabLayoutMediator(binding.tabLayoutRoleEsport, binding.viewPagerRoleEsport) { tab, position ->
                        tab.text = roleESportList[position].name
                    }.attach()
                }

            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun render(count: Int? = null) {
        val fragmentAgent = this

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = CardAgentAdapter(agentList, fragmentAgent, count)
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
            Utils.loadImage(agent.displayIcon, cardCellBinding.cover)
            cardCellBinding.name.text = agent.displayName

            cardCellBinding.cardView.setOnClickListener {
                clickListener.onClick(agent)
            }
        }
    }
}

class CardAgentAdapter(
    private val agents: List<Agent>,
    private val clickListener: AgentClickListener,
    private val count: Int?
): RecyclerView.Adapter<CardAgentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAgentViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardCellBinding.inflate(from, parent, false)
        return CardAgentViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CardAgentViewHolder, position: Int) {
        holder.bindAgent(if (agents.isEmpty()) null else agents[position])
    }

    override fun getItemCount(): Int = count ?: if (agents.isEmpty()) 10 else agents.size
}

// Model
interface AgentClickListener {
    fun onClick(agent: Agent)
}

class ResponseAgent(
    val data: Agent,
    val status: Int
)
class ResponseAgents(
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
    var voiceLine: VoiceLine? = null,
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

@Parcelize
data class Role(
    val uuid: String,
    val assetPath: String,
    val displayIcon: String,
    val displayName: DisplayName,
    val description: String
) : Parcelable

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