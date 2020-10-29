package com.czf.ws

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@Component
@ServerEndpoint("/message/{userName}")
class WSController {
    companion object {
        private var connections = 0
        private val socketPool = ConcurrentHashMap<String, WSController>()

        @Synchronized
        fun syncUser() {
            connections = socketPool.size
        }

        fun broadCast(content: String) {
            socketPool.forEach {
                it.value.sendMessage(content)
            }
        }
    }

    private lateinit var session:Session
    private var userName = ""

    @OnOpen
    fun onOpen(session: Session, @PathParam("userName")userName:String) {
        this.session = session
        this.userName = userName
        socketPool[userName] = this
        sendMessage("连接成功")
        broadCast("$userName 已加入群聊")
        syncUser()
    }

    @OnClose
    fun onClose() {
        socketPool.remove(userName)
        broadCast("$userName 已离开群聊")
        syncUser()
    }

    @OnMessage
    fun onMessage(string:String) {
        socketPool.forEach {
            if(it.key != userName)
                it.value.sendMessage(string)
        }
    }

    @OnError
    fun onError(session: Session, error: Throwable) {
        println("用户 $userName 发生错误")
    }

    fun sendMessage(content:String) {
        this.session.asyncRemote.sendText(content)
    }
}