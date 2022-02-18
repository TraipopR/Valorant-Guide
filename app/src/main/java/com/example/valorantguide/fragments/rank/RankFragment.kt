package com.example.valorantguide.fragments.rank

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.valorantguide.NullableTypAdapterFactory
import com.example.valorantguide.databinding.CardCellBinding
import com.example.valorantguide.databinding.FragmentRankBinding
import com.example.valorantguide.fragments.BaseFragment
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.gson.GsonBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

var rankList = mutableListOf<Tier>()
const val RANK_ID_EXTRA = "rankExtra"

class RankFragment : BaseFragment(), RankClickListener {
    private lateinit var binding: FragmentRankBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)

        render()
        doAsync {
            if (rankList.isEmpty()) {
                val rankJson = URL("https://valorant-api.com/v1/competitivetiers?language=th-TH").readText()
                Log.d(javaClass.simpleName, rankJson)
                val gson = GsonBuilder().registerTypeAdapterFactory(NullableTypAdapterFactory()).create()
                rankList = gson.fromJson(rankJson, ResponseRank::class.java).data.last().tiers.filter { it.largeIcon != null }.toMutableList()
            }

            uiThread {
                render()
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun render() {
        val fragmentRank = this

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = CardRankAdapter(rankList, fragmentRank)
        }
    }

    override fun onClick(rank: Tier) {
        val intent = Intent(activity, RankDetail::class.java)
        intent.putExtra(RANK_ID_EXTRA, rank.tier)
        startActivity(intent)
    }
}

class CardRankViewHolder(
    private val cardCellBinding: CardCellBinding,
    private val clickListener: RankClickListener
): RecyclerView.ViewHolder(cardCellBinding.root) {
    fun bindRank(rank: Tier?) {
        if (rank == null) {
            cardCellBinding.cover.createSkeleton().showSkeleton()
            cardCellBinding.name.createSkeleton().showSkeleton()
        } else {
            try {
                doAsync {
                    val url = URL(rank.largeIcon);
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    uiThread {
                        cardCellBinding.cover.setImageBitmap(bmp)
                    }
                }
            } catch(e: Exception) {}
            cardCellBinding.name.text = rank.tierName

            cardCellBinding.cardView.setOnClickListener {
                clickListener.onClick(rank)
            }
        }
    }
}

class CardRankAdapter(
    private val ranks: List<Tier>,
    private val clickListener: RankClickListener
): RecyclerView.Adapter<CardRankViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardRankViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardCellBinding.inflate(from, parent, false)
        return CardRankViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CardRankViewHolder, position: Int) {
        holder.bindRank(if (ranks.isEmpty()) null else ranks[position])
    }

    override fun getItemCount(): Int = if (ranks.isEmpty()) 10 else ranks.size
}

// Model
interface RankClickListener {
    fun onClick(rank: Tier)
}

data class ResponseRank (
    val status: Long,
    val data: List<Rank>
)

data class Rank (
    val uuid: String,
    val assetObjectName: String,
    val tiers: MutableList<Tier>,
    val assetPath: String
)

data class Tier (
    val tier: Long,
    val tierName: String,
    val division: String,
    val divisionName: String,
    val color: String,
    val backgroundColor: String,
    val smallIcon: String? = null,
    val largeIcon: String? = null,
    val rankTriangleDownIcon: String? = null,
    val rankTriangleUpIcon: String? = null
)

