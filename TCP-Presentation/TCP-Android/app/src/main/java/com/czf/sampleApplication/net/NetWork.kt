package com.czf.sampleApplication.net

import com.alibaba.fastjson.JSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.util.concurrent.TimeUnit

object NetWork {
    private val client by lazy {
        OkHttpClient.Builder()
            .pingInterval(8, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    private const val httpDomain="https://moral-helper.online:8248"
    private const val wsDomain="wss://moral-helper.online:8248/message"

    suspend fun getAllStudents():List<Student> {
        return withContext(Dispatchers.IO) {
            val request=Request.Builder()
                .get()
                .url("$httpDomain/students")
                .build()

            val response = client.newCall(request).execute()

            try {
                JSON.parseArray(response.body!!.string(), Student::class.java)
            } catch (e:Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getExactStudent(stuId:Int):String {
        return withContext(Dispatchers.IO) {
            val request=Request.Builder()
                .get()
                .url("$httpDomain/students/$stuId")
                .build()

            val response = client.newCall(request).execute()

            try {
                """
                    状态码：${response.code}
                    ${response.body!!.string()}
                """.trimIndent()
            } catch (e:Exception) {
                e.printStackTrace()
                "网络连接故障"
            }
        }
    }

    suspend fun addStudent(stuId:Int, name:String):String {
        return withContext(Dispatchers.IO) {
            val requestBody = FormBody.Builder()
                .add("id",stuId.toString())
                .add("name",name)
                .build()

            val request=Request.Builder()
                .post(requestBody)
                .url("$httpDomain/students/new")
                .build()

            val response = client.newCall(request).execute()

            try {
                """
                    状态码：${response.code}
                    ${response.body!!.string()}
                """.trimIndent()
            } catch (e:Exception) {
                e.printStackTrace()
                "网络连接故障"
            }
        }
    }

    suspend fun deleteStudent(stuId:Int):String {
        return withContext(Dispatchers.IO) {
            val request=Request.Builder()
                .delete()
                .url("$httpDomain/students/delete/$stuId")
                .build()

            val response = client.newCall(request).execute()

            try {
                """
                    状态码：${response.code}
                    ${response.body!!.string()}
                """.trimIndent()
            } catch (e:Exception) {
                e.printStackTrace()
                "网络连接故障"
            }
        }
    }

    fun startWS(userName:String, listener: WebSocketListener):WebSocket {
        val request=Request.Builder()
            .url("$wsDomain/$userName")
            .build()

        return client.newWebSocket(request, listener)
    }
}