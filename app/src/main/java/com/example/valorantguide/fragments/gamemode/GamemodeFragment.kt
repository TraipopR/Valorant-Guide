package com.example.valorantguide.fragments.gamemode

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.valorantguide.NullableTypAdapterFactory
import com.example.valorantguide.R
import com.example.valorantguide.databinding.CardCellBinding
import com.example.valorantguide.databinding.FragmentGamemodeBinding
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.gson.GsonBuilder
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

var gamemodeList = mutableListOf<Gamemode>()
const val GAMEMODE_ID_EXTRA = "gamemodeExtra"

class GamemodeFragment : Fragment(), GamemodeClickListener {
    private lateinit var binding: FragmentGamemodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGamemodeBinding.inflate(layoutInflater)

        render()
        doAsync {
            if (gamemodeList.isEmpty()) {
                val gamemodeJson = URL("https://valorant-api.com/v1/gamemodes?language=th-TH").readText()
                Log.d(javaClass.simpleName, gamemodeJson)
                val gson = GsonBuilder().registerTypeAdapterFactory(NullableTypAdapterFactory()).create()
                gamemodeList = gson.fromJson(gamemodeJson, ResponseGamemode::class.java).data
            }

            uiThread {
                render()
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun render() {
        val fragmentGamemode = this

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = CardGamemodeAdapter(gamemodeList, fragmentGamemode)
        }
    }

    override fun onClick(gamemode: Gamemode) {
        val intent = Intent(activity, GamemodeDetail::class.java)
        intent.putExtra(GAMEMODE_ID_EXTRA, gamemode.uuid)
        startActivity(intent)
    }
}

class CardGamemodeViewHolder(
    private val cardCellBinding: CardCellBinding,
    private val clickListener: GamemodeClickListener
): RecyclerView.ViewHolder(cardCellBinding.root) {

    @SuppressLint("ResourceAsColor")
    fun bindGamemode(gamemode: Gamemode?) {
        if (gamemode == null) {
            cardCellBinding.cover.createSkeleton().showSkeleton()
            cardCellBinding.name.createSkeleton().showSkeleton()
        } else {
            if (gamemode.displayIcon != null) {
                try {
                    doAsync {
                        val url = URL(gamemode.displayIcon)
                        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        uiThread {
                            cardCellBinding.cover.setImageBitmap(bmp)
                            cardCellBinding.cover.setBackgroundColor(R.color.gray)
                        }
                    }
                } catch(e: Exception) {}
            }
            cardCellBinding.name.text = gamemode.displayName

            cardCellBinding.cardView.setOnClickListener {
                clickListener.onClick(gamemode)
            }
        }
    }
}

class CardGamemodeAdapter(
    private val gamemodes: List<Gamemode>,
    private val clickListener: GamemodeClickListener
): RecyclerView.Adapter<CardGamemodeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardGamemodeViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardCellBinding.inflate(from, parent, false)
        return CardGamemodeViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CardGamemodeViewHolder, position: Int) {
        holder.bindGamemode(if (gamemodes.isEmpty()) null else gamemodes[position])
    }

    override fun getItemCount(): Int = if (gamemodes.isEmpty()) 10 else gamemodes.size
}

// Model
interface GamemodeClickListener {
    fun onClick(gamemode: Gamemode)
}

data class ResponseGamemode (
    val status: Long,
    val data: MutableList<Gamemode>
)

data class Gamemode (
    val uuid: String,
    val displayName: String,
    val duration: String? = null,
    val allowsMatchTimeouts: Boolean,
    val isTeamVoiceAllowed: Boolean,
    val isMinimapHidden: Boolean,
    val orbCount: Long,
    val teamRoles: List<String>? = null,
    val gameFeatureOverrides: List<GameFeatureOverride>? = null,
    val gameRuleBoolOverrides: List<GameRuleBoolOverride>? = null,
    val displayIcon: String? = null,
    val assetPath: String
)

data class GameFeatureOverride (
    val featureName: String,
    val state: Boolean
)

data class GameRuleBoolOverride (
    val ruleName: String,
    val state: Boolean
)
