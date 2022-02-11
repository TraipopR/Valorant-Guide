package com.example.valorantguide.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.valorantguide.MainActivity
import com.example.valorantguide.R

open class BaseFragment: Fragment() {

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

}