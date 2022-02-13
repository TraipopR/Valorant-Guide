package com.example.valorantguide.fragments.weapon

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import com.example.valorantguide.MainActivity
import com.example.valorantguide.databinding.ActivityWeaponDetailBinding
import com.example.valorantguide.fragments.weapon.WEAPON_ID_EXTRA
import com.example.valorantguide.fragments.weapon.weaponList
import com.example.valorantguide.fragments.weapon.weaponList
import com.google.android.material.tabs.TabLayoutMediator
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class WeaponDetail : AppCompatActivity() {
    private lateinit var binding: ActivityWeaponDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeaponDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val weaponId = intent.getStringExtra(WEAPON_ID_EXTRA)
        val weapon = weaponFromId(weaponId)
        if (weapon != null) {

            val toolbar: Toolbar = binding.toolbar
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.title = "${weapon.displayName} - ${weapon.shopData?.categoryText ?: ""}"

            try {
                doAsync {
                    val url = URL(weapon.displayIcon);
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    uiThread {
                        binding.cover.setImageBitmap(bmp)
                        if (weapon.shopData?.newImage == null) {
                            binding.coverShop.setImageBitmap(bmp)
                        }
                    }
                }
            } catch(e: Exception) {}

            weapon.shopData?.let {
                binding.category.text = it.categoryText
                try {
                    doAsync {
                        val url = URL(it.newImage);
                        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        uiThread {
                            binding.coverShop.setImageBitmap(bmp)
                        }
                    }
                } catch(e: Exception) {}
                binding.price.text = "%,d".format(it.cost)
            }
            binding.name.text = weapon.displayName

            if (weapon.weaponStats != null)
                with(weapon.weaponStats) {
                    binding.fireRate.text = fireRate.toString()
                    binding.runSpeed.text = runSpeedMultiplier.toString()
                    binding.equipSpeed.text = equipTimeSeconds.toString()
                    binding.firstShotSpread.text = firstBulletAccuracy.toString()
                    binding.reloadSpeed.text = reloadTimeSeconds.toString()
                    binding.magazine.text = magazineSize.toString()
                    wallPenetration?.let {
                        binding.wall.text = it.value.substringAfter("::")
                    }
                    adsStats?.let {
                        binding.burstCount.text = it.burstCount.toString()
                        binding.zoom.text = it.zoomMultiplier.toString()
                    }
                    binding.shortgunPelletCount.text = shotgunPelletCount.toString()
                    val range1 = listOf(
                        listOf(binding.range1, binding.range1Head, binding.range1Body, binding.range1Legs),
                        listOf(binding.range2, binding.range2Head, binding.range2Body, binding.range2Legs),
                        listOf(binding.range3, binding.range3Head, binding.range3Body, binding.range3Legs)
                    )

                    damageRanges.forEachIndexed { index, dmg ->
                        with(dmg) {
                            range1[index][0].text = "$rangeStartMeters - $rangeEndMeters"
                            range1[index][1].text = headDamage.toString()
                            range1[index][2].text = bodyDamage.toString()
                            range1[index][3].text = legDamage.toString()
                        }
                    }
                }
            else {
                binding.weaponStats.visibility = GridLayout.GONE
                binding.weaponDamageRange.visibility = CardView.GONE
            }

            val adapter = ViewPagerWeaponSkinAdapter(this@WeaponDetail)
            weapon.skins.forEach {
                if (!it.displayName.startsWith("Standard"))
                    adapter.addFragment(WeaponSkin(), it)
            }
            binding.skinPager.adapter = adapter

            val tabLayout = binding.skinTab
            TabLayoutMediator(tabLayout, binding.skinPager) { tab, position ->
                tab.text = weapon.skins[position].displayName.replace(" ${weapon.displayName}", "")
            }.attach()
        }
    }

    private fun weaponFromId(weaponId: String?): Weapon? {
        for(weapon in weaponList) {
            if (weapon.uuid == weaponId)
                return weapon
        }
        return null
    }
}