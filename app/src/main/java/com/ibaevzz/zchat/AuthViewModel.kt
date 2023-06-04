package com.ibaevzz.zchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class AuthViewModel: ViewModel() {

    private val sec = MutableLiveData<Int>()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null
    private val isRunning: Boolean
        get() = job?.isActive?:false
    var isSendCode = false
    var isCompleted = false

    fun startTimer(): LiveData<Int>{
        if(!isRunning) {
            job = scope.launch {
                var i = 60
                withContext(Dispatchers.Main) {
                    sec.value = i
                }
                while (i > 0) {
                    delay(1000)
                    withContext(Dispatchers.Main) {
                        sec.value = --i
                    }
                }
                isCompleted = true
            }
        }
        return sec
    }
}