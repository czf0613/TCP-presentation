package com.czf.sampleApplication.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.czf.sampleApplication.R
import com.czf.sampleApplication.net.NetWork
import com.czf.sampleApplication.net.Student
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout
import kotlinx.android.synthetic.main.http.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Http: Fragment(R.layout.http) {
    private val simpleDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }

    override fun onStart() {
        super.onStart()

        refresh.setOnPullListener(object :QMUIPullRefreshLayout.OnPullListener{
            override fun onMoveTarget(offset: Int) {
            }

            override fun onMoveRefreshView(offset: Int) {
            }

            override fun onRefresh() {
                modifyList()
            }
        })

        modifyList()

        get.setOnClickListener {
            val imm: InputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)

            if(inputStudentId.text.isNullOrEmpty())
                return@setOnClickListener
            lifecycleScope.launch {
                getResult.text = NetWork.getExactStudent(inputStudentId.text.toString().toInt())
            }
        }

        post.setOnClickListener {
            if(createStudentId.text.isNullOrEmpty() || createStudentName.text.isNullOrEmpty())
                return@setOnClickListener

            val imm: InputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)

            lifecycleScope.launch {
                postResult.text = NetWork.addStudent(createStudentId.text.toString().toInt(), createStudentName.text.toString())
                modifyList()
            }
        }

        delete.setOnClickListener {
            if(deleteStudentId.text.isNullOrEmpty())
                return@setOnClickListener

            val imm: InputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)

            lifecycleScope.launch {
                deleteResult.text = NetWork.deleteStudent(deleteStudentId.text.toString().toInt())
                modifyList()
            }
        }
    }

    inner class MyAdapter(private val raw:List<Student>): BaseAdapter() {
        override fun getCount(): Int {
            return raw.size
        }

        override fun getItem(position: Int): Any {
            return raw[position]
        }

        override fun getItemId(position: Int): Long {
            return raw[position].id.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = View.inflate(this@Http.context, R.layout.student, null)
            val icon = view.findViewById<ImageView>(R.id.icon)
            val id = view.findViewById<TextView>(R.id.id)
            val name = view.findViewById<TextView>(R.id.name)
            val gender = view.findViewById<TextView>(R.id.gender)
            val major = view.findViewById<TextView>(R.id.major)
            val birthday = view.findViewById<TextView>(R.id.birthday)

            val current = raw[position]
            Glide.with(this@Http).load("https://pic-bed.xyz:2053/download/${current.id}").placeholder(R.drawable.placeholder).into(icon)
            id.text="学生id：${current.id}"
            name.text=current.name
            gender.text=current.gender
            major.text="专业：${current.major}"
            birthday.text="出生日期：${simpleDateFormat.format(current.birthday)}"

            return view
        }
    }

    private fun modifyList() {
        lifecycleScope.launch {
            val all = NetWork.getAllStudents()
            studentList.adapter=MyAdapter(all)
            refresh.finishRefresh()
        }
    }
}