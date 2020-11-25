package com.czf.ws

import org.springframework.stereotype.Component
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@Component
@ServerEndpoint("/ws/{userName}")
class WSController {
    private lateinit var session: Session
    private var userName = ""

    @OnOpen
    fun onOpen(session: Session, @PathParam("userName")userName:String) {
        this.session = session
        this.userName = userName
        sendMessage("connection success")
    }

    @OnMessage
    fun onMessage(content: String) {
        sendMessage("RepeatMessage: $userName says: $content")
    }

    @OnError
    fun onError(session: Session, error: Throwable) {
        println("user: $userName has error")
    }

    @OnClose
    fun onClose() {
        sendMessage("connection closed")
        System.gc()
    }

    private fun sendMessage(content: String){
        session.asyncRemote.sendText(content)
    }
}