package com.darklycoder.android.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darklycoder.android.demo.sensor.camera.CameraIndexPage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnCamera.setOnClickListener { startActivity(Intent(this, CameraIndexPage::class.java)) }
    }
}
