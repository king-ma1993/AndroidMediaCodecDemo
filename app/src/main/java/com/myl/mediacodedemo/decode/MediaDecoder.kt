package com.myl.mediacodedemo.decode


open class MediaDecoder() : Thread() {

    override fun run() {
        super.run()
    }


    companion object {
        const val TIME_OUT_US= 1000L
    }

}