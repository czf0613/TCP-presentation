package com.czf

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.integration.ip.udp.UnicastSendingMessageHandler
import org.springframework.integration.support.MessageBuilder


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NetworkApplicationTests {

    @Test
    fun contextLoads() {

    }

}
