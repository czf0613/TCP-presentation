package com.czf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class TcpApplication

fun main(args: Array<String>) {
    runApplication<TcpApplication>(*args)
}

fun generateDate(yearsBefore:Int) = Date(System.currentTimeMillis() - yearsBefore*365*24*3600*1000L + (1..31536000000L).random())
