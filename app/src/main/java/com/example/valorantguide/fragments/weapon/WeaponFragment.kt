package com.example.valorantguide.fragments.weapon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.valorantguide.MainActivity
import com.example.valorantguide.NullableTypAdapterFactory
import com.example.valorantguide.R
import com.example.valorantguide.Utils
import com.example.valorantguide.databinding.CardCellBinding
import com.example.valorantguide.databinding.FragmentWeaponBinding
import com.example.valorantguide.fragments.BaseFragment
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

var weaponList = mutableListOf<Weapon>()
const val WEAPON_ID_EXTRA = "weaponExtra"

class WeaponFragment : BaseFragment(), WeaponClickListener {
    private lateinit var binding: FragmentWeaponBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeaponBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)

        render()
        doAsync {
            if (weaponList.isEmpty()) {
                val weaponJson = URL("https://valorant-api.com/v1/weapons").readText()
                Log.d(javaClass.simpleName, weaponJson)
                try {
                    val gson = GsonBuilder().registerTypeAdapterFactory(NullableTypAdapterFactory()).create()
                    weaponList = gson.fromJson(weaponJson, ResponseWeapon::class.java).data
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.appbar_toggle_mode, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_mode -> {
                (activity as MainActivity).toggleMode()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun render() {
        val fragmentWeapon = this

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = CardWeaponAdapter(weaponList, fragmentWeapon)
        }
    }

    override fun onClick(weapon: Weapon) {
        val intent = Intent(activity, WeaponDetail::class.java)
        intent.putExtra(WEAPON_ID_EXTRA, weapon.uuid)
        startActivity(intent)
    }
}

class CardWeaponViewHolder(
    private val cardCellBinding: CardCellBinding,
    private val clickListener: WeaponClickListener
): RecyclerView.ViewHolder(cardCellBinding.root) {
    fun bindWeapon(weapon: Weapon?) {
        if (weapon == null) {
            cardCellBinding.cover.createSkeleton().showSkeleton()
            cardCellBinding.name.createSkeleton().showSkeleton()
        } else {
            Utils.loadImage(weapon.displayIcon, cardCellBinding.cover)
            cardCellBinding.name.text = weapon.displayName

            cardCellBinding.cardView.setOnClickListener {
                clickListener.onClick(weapon)
            }
        }
    }
}

class CardWeaponAdapter(
    private val weapons: List<Weapon>,
    private val clickListener: WeaponClickListener
): RecyclerView.Adapter<CardWeaponViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardWeaponViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardCellBinding.inflate(from, parent, false)
        return CardWeaponViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CardWeaponViewHolder, position: Int) {
        holder.bindWeapon(if (weapons.isEmpty()) null else weapons[position])

    }

    override fun getItemCount(): Int = if (weapons.isEmpty()) 10 else weapons.size
}

// Model
interface WeaponClickListener {
    fun onClick(weapon: Weapon)
}

data class ResponseWeapon (
    val status: Long,
    val data: MutableList<Weapon>
)

data class Weapon (
    val uuid: String,
    val displayName: String,
    val category: String,

    val defaultSkinUuid: String,

    val displayIcon: String,
    val killStreamIcon: String,
    val assetPath: String,
    val weaponStats: WeaponStats? = null,
    val shopData: ShopData? = null,
    val skins: List<Skin>
)

data class ShopData (
    val cost: Long,
    val category: String,
    val categoryText: String,
    val gridPosition: GridPosition? = null,
    val canBeTrashed: Boolean,
    val image: Any? = null,
    val newImage: String,
    val newImage2: Any? = null,
    val assetPath: String
)

data class GridPosition (
    val row: Long,
    val column: Long
)

data class Skin (
    val uuid: String,
    val displayName: String,

    val themeUuid: String,

    val contentTierUuid: String? = null,

    val displayIcon: String? = null,
    val wallpaper: String? = null,
    val assetPath: String,
    val chromas: List<Chroma>,
    val levels: List<Level>
)

data class Chroma (
    val uuid: String,
    val displayName: String,
    val displayIcon: String? = null,
    val fullRender: String,
    val swatch: String? = null,
    val streamedVideo: String? = null,
    val assetPath: String
)

data class Level (
    val uuid: String,
    val displayName: String? = null,
    val levelItem: LevelItem? = null,
    val displayIcon: String? = null,
    val streamedVideo: String? = null,
    val assetPath: String
)

enum class LevelItem(val value: String) {
    EEquippableSkinLevelItemAnimation("EEquippableSkinLevelItem::Animation"),
    EEquippableSkinLevelItemFinisher("EEquippableSkinLevelItem::Finisher"),
    EEquippableSkinLevelItemInspectAndKill("EEquippableSkinLevelItem::InspectAndKill"),
    EEquippableSkinLevelItemKillBanner("EEquippableSkinLevelItem::KillBanner"),
    EEquippableSkinLevelItemKillCounter("EEquippableSkinLevelItem::KillCounter"),
    EEquippableSkinLevelItemRandomizer("EEquippableSkinLevelItem::Randomizer"),
    EEquippableSkinLevelItemTopFrag("EEquippableSkinLevelItem::TopFrag"),
    EEquippableSkinLevelItemVFX("EEquippableSkinLevelItem::VFX"),
    EEquippableSkinLevelItemVoiceover("EEquippableSkinLevelItem::Voiceover");

    companion object {
        public fun fromValue(value: String): LevelItem = when (value) {
            "EEquippableSkinLevelItem::Animation"      -> EEquippableSkinLevelItemAnimation
            "EEquippableSkinLevelItem::Finisher"       -> EEquippableSkinLevelItemFinisher
            "EEquippableSkinLevelItem::InspectAndKill" -> EEquippableSkinLevelItemInspectAndKill
            "EEquippableSkinLevelItem::KillBanner"     -> EEquippableSkinLevelItemKillBanner
            "EEquippableSkinLevelItem::KillCounter"    -> EEquippableSkinLevelItemKillCounter
            "EEquippableSkinLevelItem::Randomizer"     -> EEquippableSkinLevelItemRandomizer
            "EEquippableSkinLevelItem::TopFrag"        -> EEquippableSkinLevelItemTopFrag
            "EEquippableSkinLevelItem::VFX"            -> EEquippableSkinLevelItemVFX
            "EEquippableSkinLevelItem::Voiceover"      -> EEquippableSkinLevelItemVoiceover
            else                                       -> throw IllegalArgumentException()
        }
    }
}

data class WeaponStats (
    val fireRate: Double,
    val magazineSize: Long,
    val runSpeedMultiplier: Double,
    val equipTimeSeconds: Double,
    val reloadTimeSeconds: Double,
    val firstBulletAccuracy: Double,
    val shotgunPelletCount: Long,
    val wallPenetration: WallPenetration? = null,
    val feature: String? = null,
    val fireMode: String? = null,
    val altFireType: AltFireType? = null,
    val adsStats: AdsStats? = null,
    val altShotgunStats: AltShotgunStats? = null,
    val airBurstStats: AirBurstStats? = null,
    val damageRanges: List<DamageRange>
)

data class AdsStats (
    val zoomMultiplier: Double,
    val fireRate: Double,
    val runSpeedMultiplier: Double,
    val burstCount: Long,
    val firstBulletAccuracy: Double
)

data class AirBurstStats (
    val shotgunPelletCount: Long,
    val burstDistance: Double
)

enum class AltFireType(val value: String) {
    EWeaponAltFireDisplayTypeADS("EWeaponAltFireDisplayType::ADS"),
    EWeaponAltFireDisplayTypeAirBurst("EWeaponAltFireDisplayType::AirBurst"),
    EWeaponAltFireDisplayTypeShotgun("EWeaponAltFireDisplayType::Shotgun");

    companion object {
        public fun fromValue(value: String): AltFireType = when (value) {
            "EWeaponAltFireDisplayType::ADS"      -> EWeaponAltFireDisplayTypeADS
            "EWeaponAltFireDisplayType::AirBurst" -> EWeaponAltFireDisplayTypeAirBurst
            "EWeaponAltFireDisplayType::Shotgun"  -> EWeaponAltFireDisplayTypeShotgun
            else                                  -> throw IllegalArgumentException()
        }
    }
}

data class AltShotgunStats (
    val shotgunPelletCount: Long,
    val burstRate: Double
)

data class DamageRange (
    val rangeStartMeters: Long,
    val rangeEndMeters: Long,
    val headDamage: Double,
    val bodyDamage: Long,
    val legDamage: Double
)

enum class WallPenetration(val value: String) {
    EWallPenetrationDisplayTypeHigh("EWallPenetrationDisplayType::High"),
    EWallPenetrationDisplayTypeLow("EWallPenetrationDisplayType::Low"),
    EWallPenetrationDisplayTypeMedium("EWallPenetrationDisplayType::Medium");

    companion object {
        public fun fromValue(value: String): WallPenetration = when (value) {
            "EWallPenetrationDisplayType::High"   -> EWallPenetrationDisplayTypeHigh
            "EWallPenetrationDisplayType::Low"    -> EWallPenetrationDisplayTypeLow
            "EWallPenetrationDisplayType::Medium" -> EWallPenetrationDisplayTypeMedium
            else                                  -> throw IllegalArgumentException()
        }
    }
}
