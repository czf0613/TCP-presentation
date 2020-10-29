package com.czf.sampleApplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.czf.sampleApplication.R
import com.czf.sampleApplication.fragment.Http
import com.czf.sampleApplication.fragment.WS
import kotlinx.android.synthetic.main.activity_main.*
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {
    private val http by lazy { Http() }
    private val ws by lazy { WS() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragment, http)
        transaction.commit()

        fragmentSelector.setOnTabSelectListener(object: AnimatedBottomBar.OnTabSelectListener{
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                val transaction1=supportFragmentManager.beginTransaction()
                when(newIndex) {
                    0 -> transaction1.replace(R.id.mainFragment, http)
                    else -> transaction1.replace(R.id.mainFragment, ws)
                }
                transaction1.commit()
            }
        })
    }
}