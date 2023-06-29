package com.ibaevzz.zchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ibaevzz.zchat.databinding.ActivityMainBinding
import com.ibaevzz.zchat.databinding.ChatLayoutBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val chats = getChats()

        if(chats.isEmpty()){
            binding.isChat.visibility = View.VISIBLE
            binding.chats.visibility = View.INVISIBLE
        }else{
            binding.chats.layoutManager = LinearLayoutManager(this)
            binding.chats.adapter = Adapter(chats)
        }
    }

    private fun getChats(): List<Chat>{
        //TODO
        return emptyList()
    }

    private class Holder(private val chatLayoutBinding: ChatLayoutBinding): RecyclerView.ViewHolder(chatLayoutBinding.root){
        fun makeChat(chat:Chat){
            //TODO
        }
    }

    private class Adapter(private val chats: List<Chat>): RecyclerView.Adapter<Holder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val chatBinding = ChatLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return Holder(chatBinding)
        }

        override fun getItemCount(): Int {
            return chats.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.makeChat(chats[position])
        }

    }
}