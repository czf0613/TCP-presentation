package com.czf.sampleApplication.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.alibaba.fastjson.JSON
import com.bumptech.glide.Glide
import com.czf.sampleApplication.R
import com.czf.sampleApplication.net.Message
import com.czf.sampleApplication.net.NetWork
import kotlinx.android.synthetic.main.ws.*
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WS: Fragment(R.layout.ws) {
    private var webSocket: WebSocket? = null
    private val webSocketListener = object:WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            lifecycleScope.launch {
                try {
                    val dialogBuilder = AlertDialog.Builder(this@WS.context!!)
                    dialogBuilder.setCancelable(true)
                    dialogBuilder.setTitle("成功")
                    dialogBuilder.setMessage("您已加入聊天")
                    dialogBuilder.setPositiveButton("开始吧！") { dialog, _ ->
                        dialog.dismiss()
                    }
                    dialogBuilder.create().show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            lifecycleScope.launch {
                try {
                    val message= JSON.parseObject(text, Message::class.java)
                    modifyList(message)
                } catch (e: Exception) {
                    modifyList(text)
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            lifecycleScope.launch {
                try {
                    val dialogBuilder = AlertDialog.Builder(this@WS.context!!)
                    dialogBuilder.setCancelable(true)
                    dialogBuilder.setTitle("错误")
                    dialogBuilder.setMessage("网络错误")
                    dialogBuilder.setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    dialogBuilder.create().show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        private var chatList = ArrayList<View>()
        private var myName = ""
    }

    private var adapter=MyAdapter()

    fun modifyList(chat: Message) {
        val view = if(chat.sender==myName)
            View.inflate(this.context, R.layout.self_send, null)
        else {
            View.inflate(this.context, R.layout.others_send, null).apply {
                val user=this.findViewById<TextView>(R.id.user)
                user.text=chat.sender
            }
        }

        val icon = view.findViewById<ImageView>(R.id.icon)
        val content = view.findViewById<TextView>(R.id.content)

        Glide.with(this).load("https://pic-bed.xyz:2053/download/${(1..20).random()}").placeholder(R.drawable.placeholder).into(icon)
        content.text = chat.content

        append(view)
    }

    fun modifyList(content: String) {
        val view = View.inflate(this.context, R.layout.simple_text, null)
        val textView=view.findViewById<TextView>(R.id.text)
        textView.text=content

        append(view)
    }

    @Synchronized
    fun append(view: View) {
        chatList.add(view)
        adapter.notifyDataSetChanged()
    }

    inner class MyAdapter: BaseAdapter() {
        override fun getCount(): Int {
            return chatList.size
        }

        override fun getItem(position: Int): Any {
            return chatList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0L
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return chatList[position]
        }
    }

    override fun onStart() {
        super.onStart()
        chats.adapter=adapter

        join.setOnClickListener {
            if(name.text.isNullOrEmpty())
                return@setOnClickListener
            webSocket?.cancel()
            myName=name.text.toString()
            webSocket=NetWork.startWS(name.text.toString(), webSocketListener)
            val imm: InputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        }

        send.setOnClickListener {
            if(say.text.isNullOrEmpty()||webSocket==null)
                return@setOnClickListener
            val message=Message(myName, say.text.toString())
            webSocket?.send(JSON.toJSONString(message))
            say.text.clear()
            modifyList(message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket?.cancel()
    }
}