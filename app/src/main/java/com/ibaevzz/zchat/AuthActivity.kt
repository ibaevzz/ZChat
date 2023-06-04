package com. ibaevzz.zchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ibaevzz.zchat.databinding.ActivityAuthBinding
import java.util.concurrent.TimeUnit

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var editText: EditText? = null
    private var id: String = ""
    private val viewModel by lazy {
        ViewModelProvider(this)[AuthViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Firebase.auth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeVisible()

        editText = binding.phone.editText
        editText?.setText("+7")
        editText?.setSelection(2)

        editText?.addTextChangedListener(Watcher())

        binding.sendPhone.setOnClickListener{
            if(viewModel.isSendCode){
                sendCode()
            }else{
                sendPhone()
            }
        }

        binding.newPhone.setOnClickListener{
            if(viewModel.isCompleted){
                repeat()
            }
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

    private fun makeVisible(){
        if(viewModel.isSendCode){
            binding.phone.visibility= View.INVISIBLE
            binding.code.visibility = View.VISIBLE
            binding.newPhone.visibility = View.VISIBLE

            binding.sendPhone.text = "Подтвердить"

            if(!viewModel.isCompleted) {
                viewModel.startTimer().observe(this@AuthActivity) {
                    if (it != 0) binding.newPhone.text =
                        "Изменить номер или отправить повторно через: $it"
                    else binding.newPhone.text = "Изменить номер или отправить повторно"
                }
            }
        }else{
            binding.phone.visibility= View.VISIBLE
            binding.code.visibility = View.INVISIBLE
            binding.newPhone.visibility = View.INVISIBLE

            binding.sendPhone.text = "Отправить код"

            viewModel.isCompleted = false
        }
    }

    private fun sendPhone(){
        val phone = editText?.text.toString()
        if(phone.length!=12){
            //TODO
            return
        }
        binding.frame.visibility = View.VISIBLE

        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this@AuthActivity)
            .setCallbacks(object: OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(cred: PhoneAuthCredential) {
                    binding.frame.visibility = View.INVISIBLE
                    Firebase.auth.signInWithCredential(cred).addOnCompleteListener(this@AuthActivity){
                        if (it.isSuccessful) {
                            val intent = Intent(this@AuthActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            //TODO
                        }
                    }
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    binding.frame.visibility = View.INVISIBLE
                    //TODO
                }

                override fun onCodeSent(id: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    viewModel.isSendCode = true
                    this@AuthActivity.id = id
                    binding.frame.visibility = View.INVISIBLE
                    makeVisible()
                }

            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun sendCode(){
        binding.frame.visibility = View.VISIBLE

        val code = binding.code.editText?.text.toString()

        if(code.isNotEmpty()&&id.isNotEmpty()) {
            val cred = PhoneAuthProvider.getCredential(id, code)
            Firebase.auth.signInWithCredential(cred).addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    binding.frame.visibility = View.INVISIBLE
                    val intent = Intent(this@AuthActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    binding.frame.visibility = View.INVISIBLE
                    //TODO
                }
            }
        }else{
            //TODO
        }
    }

    private fun repeat(){
        viewModel.isSendCode = false
        viewModel.isCompleted = false
        makeVisible()
    }

    private inner class Watcher: TextWatcher{

        private var beforeText = "+7"

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val str = s.subSequence(start, start + count)
            var add = ""
            if (str.length==12&&str.subSequence(0, 2).toString()=="+7") {
                beforeText = str.toString()
            } else{
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