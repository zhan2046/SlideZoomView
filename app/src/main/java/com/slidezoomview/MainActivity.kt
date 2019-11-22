package com.slidezoomview

import android.os.Bundle
import android.widget.SeekBar

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

import zhan.slidezoomview.SlideZoomView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hnv1.numberStringArray = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        hnv2.numberStringArray = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
        hnv3.numberStringArray = arrayOf("我", "要", "放", "大", "我", "要", "滑", "动", "啊", "呀")

        hnv1.seekBar = seek_bar1
        hnv2.seekBar = seek_bar2
        hnv3.seekBar = seek_bar3
    }
}
