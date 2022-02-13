package com.example.valorantguide.fragments.weapon

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.example.valorantguide.R
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.valorantguide.databinding.FragmentWeaponSkinBinding
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.createSkeleton
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

private const val ARG_SKIN = "arg_skin"

class WeaponSkin : Fragment() {
    private var skin: String? = null
    private lateinit var binding: FragmentWeaponSkinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            skin = it.getString(ARG_SKIN)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeaponSkinBinding.inflate(layoutInflater)

        if (!skin.isNullOrEmpty())
            try {
                doAsync {
                    val url = URL(skin);
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    uiThread {
                        binding.cover.setImageBitmap(bmp)
                    }
                }
            } catch(e: Exception) {}

        // Inflate the layout for this fragment
        return binding.root
    }

    fun newInstance(skin: String) =
        WeaponSkin().apply {
            arguments = Bundle().apply {
                putString(ARG_SKIN, skin)
            }
        }
}

class ViewPagerWeaponSkinAdapter(fa: FragmentActivity) :
    FragmentStateAdapter(fa) {

    // declare arrayList to contain fragments and its title
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentSkinList = ArrayList<Skin>()

    override fun getItemCount(): Int = mFragmentList.size
    override fun createFragment(position: Int): Fragment =
        (mFragmentList[position] as WeaponSkin)
            .newInstance(mFragmentSkinList[position].levels[0].displayIcon ?: "")

    fun addFragment(fragment: Fragment, skin: Skin) {
        // add each fragment and its title to the array list
        mFragmentList.add(fragment)
        mFragmentSkinList.add(skin)
    }
}