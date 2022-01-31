package com.myl.mediacodedemo.decode.audio

import android.content.res.AssetFileDescriptor
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class AACAudioDecoderThread {

    companion object {
        private const val TIMEOUT_US = 10000L
        private const val AUDIO = "audio/"
        private const val TAG = "AACAudioDecoderThread"
    }

    private lateinit var extractor: MediaExtractor
    private lateinit var decoder: MediaCodec

    private var endOfReceived = false
    private var sampleRate = 0

    @RequiresApi(Build.VERSION_CODES.N)
    fun startPlay(file: AssetFileDescriptor) {
        endOfReceived = false
        extractor = MediaExtractor()
        extractor.setDataSource(file)
        var channel = 0

        (0 until extractor.trackCount).forEach { trackNumber ->
            val format = extractor.getTrackFormat(trackNumber)
            format.getString(MediaFormat.KEY_MIME).takeIf { it?.startsWith(AUDIO) == true }?.let {
                extractor.selectTrack(trackNumber)
                Log.d(TAG, "format : $format")
                sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                channel = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                return@forEach
            }
        }

    }

    fun stop() {
        endOfReceived = true
    }
}