package com.example.valorantguide.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.valorantguide.MainActivity
import com.example.valorantguide.R
import com.example.valorantguide.databinding.FragmentRegisterFormBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterFormFragment : Fragment() {
    private lateinit var binding: FragmentRegisterFormBinding
    private lateinit var authen: FirebaseAuth
    private lateinit var _activity: LoginActivity
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterFormBinding.inflate(layoutInflater)
        _activity = activity as LoginActivity
        authen = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            if (isLoading) return@setOnClickListener
            setLoading(true)

            val email = binding.txtEmail.text.toString().trim()
            val pass = binding.txtPassword.text.toString().trim()
            val conPass = binding.txtConfirmPassword.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(_activity, "กรุณากรอก Email.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (pass.isEmpty()) {
                Toast.makeText(_activity, "กรุณากรอก Password.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (conPass != pass) {
                val msg = "กรุณากรอก Password ให้ตรงกัน"
                binding.txtConfirmPassword.error = msg
                Toast.makeText(_activity, msg, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            authen.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (pass.length < 8) {
                        binding.txtPassword.error = "กรอกรหัสผ่านให้มากกว่า 8 ตัวอักษร"
                    } else {
                        Toast.makeText(_activity, "Login ล้มเหลวเนื่องจาก: " + task.exception!!.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(_activity, "Login Success!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(_activity, MainActivity::class.java))
                    _activity.finish()
                }
                setLoading(false)
            }
        }

        binding.btnLogin.setOnClickListener {
            (activity as LoginActivity).binding.formPager.currentItem = 0
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    fun setLoading(loading: Boolean) {
        binding.btnRegister.text = if (loading) "Loading..." else getString(R.string.register)
        isLoading = loading
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }
}