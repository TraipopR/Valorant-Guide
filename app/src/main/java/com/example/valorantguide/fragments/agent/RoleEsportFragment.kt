package com.example.valorantguide.fragments.agent

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.text.Spanned
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.valorantguide.Utils
import com.example.valorantguide.databinding.FragmentRoleEsportBinding
import kotlinx.parcelize.Parcelize
import kotlin.collections.ArrayList

private const val ARG_ROLE = "argRole"

class RoleESportFragment : Fragment() {
    private var role: RoleESport? = null
    private lateinit var binding: FragmentRoleEsportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            role = it.getParcelable(ARG_ROLE)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRoleEsportBinding.inflate(layoutInflater)

        role?.let { role ->
            binding.role.text = assignBullet(role.role)
            binding.agent.text = role.agent.map {
                val name = if (Utils.isValidUUID(it)) agentList.find { agent -> agent.uuid == it }?.displayName else it
                if (name == null) ""
                else assignBullet(name)
            }.filter { it != "" }.joinToString("\n")

            if (role.property != null)
                binding.property.text = assignBullet(role.property)
            else binding.propertyContainer.visibility = View.GONE

            if (role.caution != null)
                binding.caution.text = assignBullet(role.caution)
            else binding.cautionContainer.visibility = View.GONE

            if (role.canPlayedWith != null) {
                binding.canPlayedWithLabel.text = "ตำแหน่งที่สามารถเล่นร่วมกับ ${role.name.substringBefore("\n")} ได้"
                binding.canPlayedWith.text = assignBullet(role.canPlayedWith)
            } else binding.canPlayedWithContainer.visibility = View.GONE

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    private fun assignBullet(text: String): Spanned = Html.fromHtml("&#8226; $text", HtmlCompat.FROM_HTML_MODE_LEGACY)
    private fun assignBullet(textList: List<String>): String = textList.joinToString("\n") { assignBullet(it) }

    fun newInstance(role: RoleESport) =
        RoleESportFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_ROLE, role)
            }
        }
}

class ViewPagerRoleESportAdapter(fa: FragmentActivity) :
    FragmentStateAdapter(fa) {

    // declare arrayList to contain fragments and its title
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentRoleList = ArrayList<RoleESport>()

    override fun getItemCount(): Int = mFragmentList.size
    override fun createFragment(position: Int): Fragment =
        (mFragmentList[position] as RoleESportFragment)
            .newInstance(mFragmentRoleList[position])

    fun addFragment(fragment: Fragment, role: RoleESport) {
        // add each fragment and its title to the array list
        mFragmentList.add(fragment)
        mFragmentRoleList.add(role)
    }
}

@Parcelize
data class RoleESport(
    val name: String,
    val role: List<String>,
    val agent: List<String>,
    val property: List<String>? = null,
    val caution: List<String>? = null,
    val canPlayedWith: List<String>? = null
): Parcelable

val roleESportList = listOf(
    RoleESport(
        "ENTRY FRAGGER\n(ตัวเปิด)",
        listOf("หาข้อมูลตำแหน่งใน Site ให้ได้มากที่สุด และบอกเพื่อนร่วมทีม", "เก็บ Kill แรกของทีม"),
        listOf("5f8d3a7f-467b-97f3-062c-13acf203c006", "eb93336a-449b-9c1b-0a54-a891f7921d69", "a3bfb853-43b2-7238-a4f1-ad90e9e46bcc", "f94c3b30-42be-e959-889c-5aa313dba261", "add6443a-41bd-e414-f6ad-e58d267f4e95"),
        property = listOf("มั่นใจในสายตาและความเร็ว", "ชำนาญเรื่องจุดดักใน Site", "มี Reflex ที่รวดเร็ว", "ชำนาญเรื่องมุม Pre-fire", "สื่อสารกระชับรวดเร็ว"),
        caution = listOf("ไม่ใช่ตำแหน่งที่วิ่งไปตายคนเดียวแล้วบอกทำหน้าที่สำเร็จแล้ว", "ต้องรอให้เพื่อนร่วมทีมเดินกันมาให้ครบก่อน สื่อสารให้ดีแล้วนับ 3.2.1 ลุยไปพร้อมกัน โดยมีเราที่เป็น Entry Fragger เดืนนำหน้าสุด"),
    ),
    RoleESport(
        "PLAY MAKER\n(คนตามตัวเปิด/ตัวแบก/จะเป็นตำแหน่งที่สกอร์สูงสุด)",
        listOf("เดินตามหลัง Entry Fragger", "รับข้อมูลทุกอย่าง และจัดการ Kill ศัตรูตามข้อมูลที่ได้มา"),
        listOf("ตัวไหนก็ได้ เพราะเน้นไปที่สกิลการเล่นของผู้เล่น"),
        property = listOf("มีสกิลรอบด้านสูงที่สุด", "ความแม่นยำสูง", "Reflex สูง", "คุมแรงดีดปืนเก่ง"),
        canPlayedWith = listOf("สามารถทำหน้าที่ประสมกับหน้าที่ Sniper ได้")
    ),
    RoleESport(
        "SUPPORT ROLE\n(เป๋าตังของทีม)",
        listOf("เซ็ต Smoke Slow Heal สร้างเสียงรบกวน", "ซื้อปืนให้ตัวแบกของทีม และมือ Sniper", "สื่อสารกับ Entry Fragger เรื่องตำแหน่งการ Smoke Slow Timing", "ดัน Performance ของทีมให้ได้มากที่สุด"),
        listOf("5f8d3a7f-467b-97f3-062c-13acf203c006", "601dbbe7-43ce-be57-2a40-4abd24953621", "6f2a04ca-43e0-be17-7f36-b3908627744d", "117ed9e3-49f3-6512-3ccf-0cada7e3823b", "707eab51-4836-f488-046a-cda6bf494859", "41fb69c1-4189-7b37-f117-bcaf1e96f1bf", "9f0d8ba9-4140-b941-57d3-a7ad57c6b417", "569fdd95-4d10-43ab-ca70-79becc718b46", "8e253930-4c05-31dd-1b6c-968525494517", "ทุกตัวที่มี Smoke หรือ Flash หรือ Heal"),
        canPlayedWith = listOf("ส่วนใหญ่เล่นตำแหน่ง IGL ด้วย เพราะโฟกัสกับหน้าจอน้อยที่สุด")
    ),
    RoleESport(
        "OPER/SNIPER\n(มือสไนเปอร์)",
        listOf("สร้างความกดดันให้มากที่สุด"),
        listOf("add6443a-41bd-e414-f6ad-e58d267f4e95", "ตัวไหนก็ได้ไม่ตายตัว"),
        property = listOf("มีความแม่นยำแบบ 1 Tap มากที่สุดในทีม", "Reaction สูงสุดในทีม", "มี Performance สูงสุดในทีม"),
        caution = listOf("ไม่ทำอะไรเดิม ๆ ซ้ำ ๆ ให้ฝ่ายตรงข้ามจับทางได้ ต้องเล่นได้หลากหลาย Flow ไปกับเกมให้ได้มากที่สุด")
    ),
    RoleESport(
        "LURKER FLANKER ROLE\n(ตัวตุ๋ยหลัง)",
        listOf("เล่นแยกกับทีม", "หาข้อมูลให้กับทีม เป็นหูและตาของทีม", "ไม่เน้นการฆ่า เน้นไปที่การสเก๊าจุดเพื่อหาข้อมูลว่า Agent ฝั่งตรงข้ามอยู่ตำแหน่งไหนบ้าง", "เป็น Key Information ของทีมที่จะทำให้ IGL คาดเดาตำแหน่งของฝ่ายตรงข้ามใน Map และเอาไปใช้วางแผนรับมือได้ในแต่ละตา", "หากเป็นฝ่ายบุกจะเล่นแยกกับทีม Hold ตำแหน่งไม่ให้ฝ่ายตรงข้ามมาตุ๋ยหลังทีมเรา นั่งซุ่มฟังเสียงสกิลเสียงเท้าตอน Rotate", "หากเป็นฝ่ายรับจะเป็นคนที่ค่อย ๆ ดันออกมาจากจุด เพื่อมาตุ๋ยหลังทีมฝ่ายตรงข้าม หรือดันมาฟังเสียงฝ่ายตรงข้ามและดักยิงตอน Rotate"),
        listOf("ตัวไหนก็ได้"),
        property = listOf("มีสไตล์การเล่นแบบ Lone Wolf", "มี Game Sense ที่ดีในการเดาทางเดาจิตใจฝ่ายตรงข้าม", "ไม่ต้องยิงแม่น หรือ Kill เยอะ เน้น Game Sense ที่ดี"),
    ),
    RoleESport(
        "ANCHOR ROLE\n(เฝ้า Site/ตำแหน่งสำหรับฝ่ายรับเท่านั้น)",
        listOf("เฝ้า Site รับผิดชอบจุดใดจุดหนึ่งแบบ 100%", "เซ็ตกับดัก", "หลบยืนซุ่มใน Site ลึก ๆ", "ไม่เน้นการฆ่า เน้นเฝ้าจุด", "ทำให้ทีมบุกลำบากมากที่สุด", "ป้องกันจุดไว้ และรอเพื่อน Rotate มาเติมจุดเราได้ทัน"),
        listOf("Sentinel ทั้งหมด และรองลงมาคือ Controller"),
    ),
    RoleESport(
        "IGL\n(IN-GAME LEADER/หัวหน้าทีม)",
        listOf("สั่งคำสั่ง", "สั่งแผน (โจมตีจุดไหน, ตั้งรับยังไง, Rotate ยังไง, Move วินาทีที่เท่าไหร่)", "ระบุตำแหน่งการยืนของทีม"),
        listOf("ตัวไหนก็ได้"),
        property = listOf("เข้าใจเกมสูงที่สุดในทุก ๆ ด้าน", "เข้าใจทีมเรา", "เข้าใจศัตรู", "มีการควบคุมอารมณ์ และการเลือกใช้คำพูดได้ดี", "มีความเป็นผู้นำ", "มี Game Sense ที่ดี"),
        caution = listOf("ระวังอารมณ์ และคำพูดของตนเองที่จะส่งให้ทีม"),
        canPlayedWith = listOf("เล่นตาม Role ที่ผู้เล่นเสริมเข้ามา เช่น Support, Entry Fragger")
    ),
)