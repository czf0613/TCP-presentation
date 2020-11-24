package com.czf.http

import com.alibaba.fastjson.JSON
import com.czf.json.Student
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ConcurrentLinkedDeque

@RestController
@RequestMapping("/students")
class HttpController {
    private val students:ConcurrentLinkedDeque<Student> by lazy {
        ConcurrentLinkedDeque<Student>().apply {
            this.addAll(Student.randomGenerate(20))
        }
    }

    @RequestMapping("",method = [RequestMethod.GET])
    fun getStudents():ResponseEntity<String> {
        if(students.isEmpty())
            students.addAll(Student.randomGenerate(20))

        val temp = ArrayList(students)

        return ResponseEntity(JSON.toJSONString(temp.sortedBy { it.id }), HttpStatus.OK)
    }

    @RequestMapping("/{stuId}",method = [RequestMethod.GET])
    fun getStudent(@PathVariable("stuId")stuId:Int):ResponseEntity<String> {
        if(students.find { it.id == stuId } == null)
            return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(JSON.toJSONString(students.find { it.id == stuId }), HttpStatus.OK)
    }

    @RequestMapping("/new", method = [RequestMethod.POST])
    fun newStudent(id:Int, name:String):ResponseEntity<String> {
        if(students.find { it.id == id } != null)
            return ResponseEntity("Student already existed!", HttpStatus.BAD_REQUEST)

        val new = Student(id, name)

        students.add(new)

        return ResponseEntity(JSON.toJSONString(new), HttpStatus.OK)
    }

    @RequestMapping("/delete/{stuId}", method = [RequestMethod.DELETE])
    fun deleteStudent(@PathVariable("stuId")stuId:Int):ResponseEntity<String> {
        if(students.find { it.id == stuId } == null)
            return ResponseEntity("Student No.$stuId does not exist!", HttpStatus.NOT_FOUND)

        students.removeIf {
            it.id == stuId
        }

        return ResponseEntity("Delete success!", HttpStatus.OK)
    }
}