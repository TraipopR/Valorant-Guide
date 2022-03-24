package com.example.valorantguide.fragments.gamemode

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.valorantguide.R
import com.example.valorantguide.databinding.ActivityGamemodeDetailBinding
import com.example.valorantguide.mode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

val gamemodeDetails = listOf(
    GamemodeDetailData("96bd3920-4f36-d026-2b28-c683eb0bcac5", "Unrated: โหมดเกมใน VALORANT แบบพื้นฐาน สลับระหว่างสนามของผู้โจมตี และผู้ป้องกัน ตัดสินโดยฝ่ายแรกที่เอาชนะได้ 13 รอบ\n\nCompetitive: โหมดเกมใน VALORANT แบบพื้นฐาน ใช้กฎเกณฑ์เดียวกับโหมด Unrated แต่เป็นโหมดที่มีการเดิมพันที่สูงกว่า ซึ่งคุณจะได้รับเงินและไต่เต้าอันดับในแรงค์"),
    GamemodeDetailData("e921d1e6-416b-c31f-1291-74930c330b7b", "Spike Rush: โหมดเกมใน VALORANT ที่สั้นกว่าและเสี่ยงน้อยกว่า ใช้กฎเกณฑ์เดียวกับโหมด Unrated ซึ่งจะมีการให้ออร์บเพิ่มพลังและจะได้อาวุธแบบสุ่ม ตัดสินโดยฝ่ายแรกที่เอาชนะได้ 4 รอบ"),
    GamemodeDetailData("a8790ec5-4237-f2f0-e93b-08a8e89865b2", "Deathmatch: การแข่งขัน Deathmatch แบบ Free for All ซึ่งเหมาะสำหรับฝึกเล่น VALORANT ไม่มีการใช้สกิลใด ๆ ทั้งนั้น คนที่ได้แต้มสังหารถึง 40 แต้มก่อนคือผู้ชนะ"),
    GamemodeDetailData("4744698a-4513-dc96-9c22-a9aa437e4a58", "Replication: ใช้กฎเกณฑ์เดียวกับโหมด Unrated แต่ผู้เล่นทุกคนในทีมเดียวกันจะได้เป็นเอเจนท์คนเดียว เครดิตต่อรอบคงที่ ทีมแรกที่ชนะ 5 รอบจะเป็นฝ่ายชนะ"),
)

class GamemodeDetail : AppCompatActivity() {
    private lateinit var binding: ActivityGamemodeDetailBinding

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamemodeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gamemodeId = intent.getStringExtra(GAMEMODE_ID_EXTRA)
        val gamemode = gamemodeList.find { it.uuid == gamemodeId }
        gamemode?.let { item ->

            val toolbar: Toolbar = binding.toolbar
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.title = item.displayName

            try {
                doAsync {
                    val url = URL(item.displayIcon);
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    uiThread {
                        binding.cover.setImageBitmap(bmp)
                        if (mode == 1)
                            binding.cover.setBackgroundColor(R.color.gray)
                    }
                }
            } catch(e: Exception) {}
            binding.title.text = item.displayName
            if (item.duration != null)
                binding.duration.text = "ระยะเวลา: ${item.duration}"

            if (item.gameFeatureOverrides != null)
                binding.feature.text = "คุณสมบัติเกม: " + item.gameFeatureOverrides.joinToString(", ") { it.featureName.substringAfterLast("::").addSpaceB4Upper() }

            if (item.gameRuleBoolOverrides != null)
                binding.rule.text = "กฎของเกม: " + item.gameRuleBoolOverrides.joinToString(", ") { it.ruleName.substringAfterLast("::").addSpaceB4Upper() }

            if (item.teamRoles != null)
                binding.roles.text = "บทบาททีม: " + item.teamRoles.joinToString(", ") { it.substringAfterLast("::").addSpaceB4Upper() }

            val gamemodeDetail = gamemodeDetails.find { it.uuid == gamemode.uuid }
            if (gamemodeDetail != null)
                binding.desc.text = gamemodeDetail.desc
            else
                binding.desc.visibility = View.INVISIBLE

        }
    }
}

fun String.addSpaceB4Upper(): String {
    return this.replace("([A-Z])".toRegex(), " $1").trim()
}