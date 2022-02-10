package com.example.valorantguide.fragments.weapon

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.valorantguide.databinding.ActivityWeaponDetailBinding
import com.example.valorantguide.fragments.weapon.WEAPON_ID_EXTRA
import com.example.valorantguide.fragments.weapon.weaponList
import com.example.valorantguide.fragments.weapon.weaponList
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class WeaponDetail : AppCompatActivity() {
    private lateinit var binding: ActivityWeaponDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeaponDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val weaponId = intent.getStringExtra(WEAPON_ID_EXTRA)
        val weapon = weaponFromId(weaponId)
        if (weapon != null) {
            try {
                doAsync {
                    val url = URL(weapon.displayIcon);
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    uiThread {
                        binding.cover.setImageBitmap(bmp)
                    }
                }
            } catch(e: Exception) {}
            binding.title.text = weapon.displayName
            binding.desc.text = weapon.category
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