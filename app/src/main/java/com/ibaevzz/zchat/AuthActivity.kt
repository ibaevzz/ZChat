package com. ibaevzz.zchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ibaevzz.zchat.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var editText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Firebase.auth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editText = binding.phone.editText
        editText?.setText("+7")
        editText?.setSelection(2)

        binding.sendPhone.setOnClickListener{
            val phone = editText?.text.toString()
        }

        editText?.addTextChangedListener(Watcher())

        startAnimation(savedInstanceState==null)
    }

    private fun startAnimation(isFirst: Boolean){
        if(isFirst) {
            val sunRiseAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.circle)
            binding.circle1.startAnimation(sunRiseAnimation)
            binding.circle2.startAnimation(sunRiseAnimation)
        }
    }

    inner class Watcher: TextWatcher{
        private var beforeText = "+7"

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val str = s.subSequence(start, start + count)
            var add = ""
            if (str.length==12&&str.subSequence(0, 2).toString()=="+7") {
                beforeText = str.toString()
            } else {
                for (i in str) {
                    if (i in '0'..'9') {
                        add += i.toString()
                    }
                }
            }

            if(s.length<=12) {
                if (s.toString() != beforeText) {
                    if (start < 2) {
                        beforeText += if(add.length+beforeText.length>12) add.substring(0..11-beforeText.length) else add
                        editText?.setText(beforeText)
                        editText?.setSelection(beforeText.length)
                    } else {
                        beforeText = s.subSequence(0, start).toString() +
                                if(add.length+beforeText.length>12) add.substring(0..11-beforeText.length) else add +
                                        s.subSequence(start + count, s.length).toString()
                        editText?.setText(beforeText)
                        editText?.setSelection(start + add.length)
                    }
                }
            }else{
                editText?.setText(beforeText)
                editText?.setSelection(start)
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }
}