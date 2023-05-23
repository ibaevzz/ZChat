package com.ibaevzz.zchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ibaevzz.zchat.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Firebase.auth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendPhone.setOnClickListener{
            val phone = binding.phone.editText?.text.toString()
        }

        startAnimation(savedInstanceState==null)
    }

    private fun startAnimation(isFirst: Boolean){
        if(isFirst) {
            val sunRiseAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.circle)
            binding.circle1.startAnimation(sunRiseAnimation)
            binding.circle2.startAnimation(sunRiseAnimation)
        }
    }
}