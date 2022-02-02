package com.myl.mediacodedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myl.mediacodedemo.databinding.ActivityMainBinding
import com.myl.mediacodedemo.decode.audio.AudioDecodeActivity
import com.myl.mediacodedemo.decode.video.VideoDecodeActivity
import com.myl.mediacodedemo.encode.CameraActivity

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
            AudioDecodeActivity.startAudioDecodeActivity(this)
        }
        activityMainBinding.videoDecode.setOnClickListener {
            VideoDecodeActivity.startVideoDecodeActivity(this)
        }
        activityMainBinding.videoEncode.setOnClickListener {
            CameraActivity.startCameraActivity(this)
        }
    }
}