package com.example.valorantguide.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.valorantguide.MainActivity
import com.example.valorantguide.NullableTypAdapterFactory
import com.example.valorantguide.R
import com.example.valorantguide.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import org.jetbrains.anko.support.v4.uiThread
import java.net.URL

open class BaseFragment: Fragment() {
    private lateinit var authen: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authen = FirebaseAuth.getInstance()
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
            R.id.action_logout -> {
                authen.signOut()
                Toast.makeText(activity, "Logout Complete", Toast.LENGTH_LONG).show()

                val it = Intent(activity, LoginActivity::class.java)
                startActivity(it)
                activity?.finish()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}