package com.czf.udp

import com.czf.tcp.UDPCompanion
import com.czf.tcp.UDPMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.annotation.Transformer
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter
import org.springframework.messaging.Message

@Configuration
class UDPController {
    @Value("\${udp.port}")
    private var port = 12345

    @Bean
    fun getUnicastReceivingChannelAdapter(): UnicastReceivingChannelAdapter {
        return UnicastReceivingChannelAdapter(port).apply {
            setOutputChannelName("udp")
        }
    }

    @Transformer(inputChannel = "udp", outputChannel = "udpString")
    fun transformer(message: Message<*>): String? {
        return String((message.payload as ByteArray))
    }

    @ServiceActivator(inputChannel="udpString")
    fun save(message: String) {
        println(message)
        UDPCompanion.messageQueue.add(UDPMessage(message))
    }
}