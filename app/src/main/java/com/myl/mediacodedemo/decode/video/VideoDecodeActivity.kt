package com.myl.mediacodedemo.decode.video

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.myl.mediacodedemo.R

class VideoDecodeActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private var videoDecoder: VideoDecodeThread? = null

    companion object {
        fun startVideoDecodeActivity(context: Context) {
            context.startActivity(Intent(context, VideoDecodeActivity::class.java))
        }
    }

    private val videoPath by lazy {
        resources.openRawResourceFd(R.raw.sample_video)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SurfaceView(this).apply {
            holder.addCallback(this@VideoDecodeActivity)
            setContentView(this)
            videoDecoder = VideoDecodeThread()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (videoDecoder?.init(holder.surface, videoPath) == true) {
                videoDecoder?.start()
            } else {
                videoDecoder = null
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        videoDecoder?.close()
    }

    override fun onPause() {
        super.onPause()
        videoDecoder?.close()
    }
}