package com.czf.tcp

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.min

@RestController
class UDPCompanion {
    companion object {
        val messageQueue = ConcurrentLinkedQueue<UDPMessage>()
    }

    @GetMapping("/udp/recent")
    fun get(@RequestParam(required = false, defaultValue = "20") limit: Int = 20): ResponseEntity<List<UDPMessage>> {
        val temp = messageQueue.toList().sortedBy { it.time }
        val size = min(limit, temp.size)
        return ResponseEntity(temp.subList(0, size), HttpStatus.OK)
    }
}