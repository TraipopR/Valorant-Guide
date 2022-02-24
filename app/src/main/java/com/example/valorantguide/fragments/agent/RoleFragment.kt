package com.example.valorantguide.fragments.agent

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.valorantguide.R
import com.example.valorantguide.databinding.FragmentRoleBinding
import com.example.valorantguide.mode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

private const val ARG_ROLE = "arg_role"

class RoleFragment : Fragment() {
    private var role: Role? = null
    private lateinit var binding: FragmentRoleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            role = it.getParcelable(ARG_ROLE)
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRoleBinding.inflate(layoutInflater)

        role?.let {
            try {
                doAsync {
                    val url = URL(it.displayIcon)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    uiThread {
                        binding.cover.setImageBitmap(bmp)
                        if (mode == 1)
                            binding.cover.setBackgroundColor(R.color.gray)
                    }
                }
            } catch(e: Exception) {}
            binding.name.text = it.displayName.value
            binding.desc.text = it.description.replace("\n", "")
        }

        return binding.root
    }

    fun newInstance(role: Role) =
        RoleFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_ROLE, role)
            }
        }
}

class ViewPagerRoleAdapter(fa: FragmentActivity) :
    FragmentStateAdapter(fa) {

    // declare arrayList to contain fragments and its title
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentRoleList = ArrayList<Role>()

    override fun getItemCount(): Int = mFragmentList.size
    override fun createFragment(position: Int): Fragment =
        (mFragmentList[position] as RoleFragment)
            .newInstance(mFragmentRoleList[position])

    fun addFragment(fragment: Fragment, role: Role) {
        // add each fragment and its title to the array list
        mFragmentList.add(fragment)
        mFragmentRoleList.add(role)
    }
}