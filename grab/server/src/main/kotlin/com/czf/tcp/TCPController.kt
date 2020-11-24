package com.czf.tcp

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/tcp")
class TCPController {
    @GetMapping("/get/{name}")
    fun get(@PathVariable name: String): ResponseEntity<String> {
        return ResponseEntity("Hello $name, you are trying to get something.", HttpStatus.OK)
    }

    @PostMapping("/post")
    fun post(file: MultipartFile): ResponseEntity<FileDetail> {
        val name=file.originalFilename
        val student = FileDetail(name?.substringBeforeLast(".")?:"empty",name?.substringAfterLast(".")?:"",file.size, Date())
        return ResponseEntity(student, HttpStatus.OK)
    }

    @DeleteMapping("/delete/{name}")
    fun delete(@PathVariable name: String): ResponseEntity<String> {
        return ResponseEntity("Hello $name, you are trying to delete something.", HttpStatus.OK)
    }
}