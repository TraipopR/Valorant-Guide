package com.example.valorantguide.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.valorantguide.MainActivity
import com.example.valorantguide.databinding.ActivityLoginBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var authen: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authen = FirebaseAuth.getInstance()

        if (authen.currentUser != null) {
            val it = Intent(this, MainActivity::class.java)
            startActivity(it)
            finish()
        }

        val adapter = ViewPagerLoginAdapter(this@LoginActivity)
        adapter.addFragment(LoginFormFragment())
        adapter.addFragment(RegisterFormFragment())

        binding.formPager.adapter = adapter
        binding.formPager.setPageTransformer(ZoomOutPageTransformer())

    }
}

class ViewPagerLoginAdapter(fa: FragmentActivity) :
    FragmentStateAdapter(fa) {

    // declare arrayList to contain fragments and its title
    private val mFragmentList = ArrayList<Fragment>()

    override fun getItemCount(): Int = mFragmentList.size
    override fun createFragment(position: Int): Fragment = mFragmentList[position]

    fun addFragment(fragment: Fragment) {
        // add each fragment and its title to the array list
        mFragmentList.add(fragment)
    }
}

private const val MIN_SCALE = 0.85f
private const val MIN_ALPHA = 0.5f

class ZoomOutPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                    val vertMargin = pageHeight * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * (1 - scaleFactor) / 2
                    translationX = if (position < 0) {
                        horzMargin - vertMargin / 2
                    } else {
                        horzMargin + vertMargin / 2
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Fade the page relative to its size.
                    alpha = (MIN_ALPHA +
                            (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}