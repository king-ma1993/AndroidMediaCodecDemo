package com.myl.mediacodedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import com.myl.mediacodedemo.databinding.ActivityMainBinding
import com.myl.mediacodedemo.video.VideoDecodeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        initView()
    }

    private fun initView() {
        activityMainBinding.audioDecode.setOnClickListener {

        }
        activityMainBinding.videoDecode.setOnClickListener {
            VideoDecodeActivity.startVideoDecodeActivity(this)
        }
    }
}