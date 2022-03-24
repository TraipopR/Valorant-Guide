package com.example.valorantguide.fragments.map

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.valorantguide.NullableTypAdapterFactory
import com.example.valorantguide.Utils
import com.example.valorantguide.databinding.CardCellBinding
import com.example.valorantguide.databinding.FragmentMapBinding
import com.example.valorantguide.fragments.BaseFragment
import com.example.valorantguide.fragments.gamemode.ResponseGamemode
import com.example.valorantguide.fragments.gamemode.gamemodeList
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

var mapList = mutableListOf<Map>()
const val MAP_ID_EXTRA = "mapExtra"

class MapFragment : BaseFragment(), MapClickListener {
    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)

        render()
        doAsync {
            if (mapList.isEmpty()) {
                val mapJson = URL("https://valorant-api.com/v1/maps?language=th-TH").readText()
                Log.d(javaClass.simpleName, mapJson)
                try {
                    val gson = GsonBuilder().registerTypeAdapterFactory(NullableTypAdapterFactory()).create()
                    mapList = gson.fromJson(mapJson, ResponseMap::class.java).data
                } catch (error: JsonParseException) {
                    uiThread {
                        binding.errorContainer.visibility = View.VISIBLE
                        binding.errorMessage.text = error.message
                    }
                }
            }

            uiThread {
                render()
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun render() {
        val fragmentMap = this

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = CardMapAdapter(mapList, fragmentMap)
        }
    }

    override fun onClick(map: Map) {
        val intent = Intent(activity, MapDetail::class.java)
        intent.putExtra(MAP_ID_EXTRA, map.uuid)
        startActivity(intent)
    }
}

class CardMapViewHolder(
    private val cardCellBinding: CardCellBinding,
    private val clickListener: MapClickListener
): RecyclerView.ViewHolder(cardCellBinding.root) {
    fun bindMap(map: Map?) {
        if (map == null) {
            cardCellBinding.cover.createSkeleton().showSkeleton()
            cardCellBinding.name.createSkeleton().showSkeleton()
        } else {
            Utils.loadImage(map.displayIcon ?: map.splash, cardCellBinding.cover)
            cardCellBinding.name.text = map.displayName

            cardCellBinding.cardView.setOnClickListener {
                clickListener.onClick(map)
            }
        }
    }
}

class CardMapAdapter(
    private val maps: List<Map>,
    private val clickListener: MapClickListener
): RecyclerView.Adapter<CardMapViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardMapViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardCellBinding.inflate(from, parent, false)
        return CardMapViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CardMapViewHolder, position: Int) {
        holder.bindMap(if (maps.isEmpty()) null else maps[position])
    }

    override fun getItemCount(): Int = if (maps.isEmpty()) 10 else maps.size
}

// Model
interface MapClickListener {
    fun onClick(map: Map)
}

data class ResponseMap (
    val status: Long,
    val data: MutableList<Map>
)

data class MapDetailData (
    val uuid: String,
    val image: Int,
    val lineup: Int? = null
)

data class Map (
    val uuid: String,
    val displayName: String,
    val coordinates: String,
    val displayIcon: String? = null,
    val listViewIcon: String,
    val splash: String? = null,
    val assetPath: String,

    val mapUrl: String,

    val xMultiplier: Double,
    val yMultiplier: Double,
    val xScalarToAdd: Double,
    val yScalarToAdd: Double,
    val callouts: List<Callout>? = null
)

data class Callout (
    val regionName: String,
    val superRegionName: SuperRegionName,
    val location: Location
)

data class Location (
    val x: Double,
    val y: Double
)

enum class SuperRegionName(val value: String) {
    A("A"),
    AttackerSide("Attacker Side"),
    B("B"),
    C("C"),
    DefenderSide("Defender Side"),
    Mid("Mid");

    companion object {
        public fun fromValue(value: String): SuperRegionName = when (value) {
            "A"             -> A
            "Attacker Side" -> AttackerSide
            "B"             -> B
            "C"             -> C
            "Defender Side" -> DefenderSide
            "Mid"           -> Mid
            else            -> throw IllegalArgumentException()
        }
    }
}
