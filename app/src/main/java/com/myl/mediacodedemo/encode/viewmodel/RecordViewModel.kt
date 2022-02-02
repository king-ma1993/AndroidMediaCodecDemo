package com.myl.mediacodedemo.encode.viewmodel

import android.content.Context
import android.opengl.EGLContext
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myl.mediacodedemo.encode.OnRecordStateListener
import com.myl.mediacodedemo.encode.record.AudioConfig
import com.myl.mediacodedemo.encode.record.MediaRecorder
import com.myl.mediacodedemo.encode.record.MediaType
import com.myl.mediacodedemo.encode.record.RecordInfo
import com.myl.mediacodedemo.encode.record.VideoConfig
import com.myl.mediacodedemo.utils.TimeUtils
import java.io.File

class RecordViewModel : ViewModel(), OnRecordStateListener {

    // 音视频参数
    private var mVideoParams: VideoConfig = VideoConfig()
    private var mAudioParams: AudioConfig = AudioConfig()

    // 录制操作开始
    // 录制操作开始
    private var mOperateStarted: Boolean = false
//    private val isRecordStart = false

    // 当前录制进度
    private var mCurrentProgress = 0f

    // 最大时长
    private var mMaxDuration = 0L

    // 剩余时长
    private var mRemainDuration = 0L

    // 录制音频信息
    private var mAudioInfo: RecordInfo? = null

    // 录制视频信息
    private var mVideoInfo: RecordInfo? = null

    // 视频录制器
    private lateinit var mHWMediaRecorder: MediaRecorder

    lateinit var context: Context

    val isShowViewLiveData by lazy { MutableLiveData<Boolean>() }
    val recordProgressLiveData by lazy { MutableLiveData<Float>() }


    fun init(context: Context) {
        this.context = context
        // 视频录制器
        mHWMediaRecorder = MediaRecorder(this)
        // 视频参数
        mVideoParams.videoPath = getVideoTempPath(context)
        // 音频参数
        mAudioParams.audioPath = getAudioTempPath(context)

    }


    /**
     * 获取音频缓存绝对路径
     * @param context
     * @return
     */
    private fun getAudioTempPath(context: Context): String {
        // 判断外部存储是否可用，如果不可用则使用内部存储路径
        val directoryPath: String =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                context.externalCacheDir!!.absolutePath
            } else { // 使用内部存储缓存目录
                context.cacheDir.absolutePath
            }
        val path = directoryPath + File.separator + "temp.aac"
        val file = File(path)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        return path
    }

    /**
     * 获取视频缓存绝对路径
     * @param context
     * @return
     */
    private fun getVideoTempPath(context: Context): String {
        // 判断外部存储是否可用，如果不可用则使用内部存储路径
        val directoryPath: String =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && context.externalCacheDir != null) {
                context.externalCacheDir!!.absolutePath
            } else { // 使用内部存储缓存目录
                context.cacheDir.absolutePath
            }
        val path = directoryPath + File.separator + "temp.mp4"
        val file = File(path)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        return path
    }

    /**
     * 设置录制时长
     * @param seconds
     */
    fun setRecordSeconds(seconds: Int) {
        mRemainDuration = TimeUtils.sToUs(seconds.toLong())
        mMaxDuration = mRemainDuration
        mVideoParams.maxDuration = mMaxDuration
        mAudioParams.maxDuration = mMaxDuration
    }

    /**
     * 绑定EGLContext
     * @param context
     */
    fun onBindSharedContext(context: EGLContext?) {
        mVideoParams.eglContext = context
    }

    /**
     * 开始录制
     */
    fun startRecord() {
        if (mOperateStarted) {
            return
        }
        mHWMediaRecorder.startRecord(mVideoParams, mAudioParams)
        mOperateStarted = true
    }

    /**
     * 打开相机
     */
    fun openCamera() {
//        mCameraController.setFront(false)
//        mCameraController.openCamera()
//        calculateImageSize()
    }

    /**
     * 停止录制
     */
    fun stopRecord() {
        if (!mOperateStarted) {
            return
        }
        mOperateStarted = false
        mHWMediaRecorder.stopRecord()
    }


    override fun onRecordStart() {
        isShowViewLiveData.postValue(false)
    }

    override fun onRecording(duration: Long) {
        val progress: Float = duration * 1.0f / mVideoParams.maxDuration
        recordProgressLiveData.postValue(progress)
        if (duration > mRemainDuration) {
            stopRecord()
        }
    }

    override fun onRecordFinish(info: RecordInfo) {
        if (info.mediaType == MediaType.AUDIO) {
            mAudioInfo = info
        } else if (info.mediaType === MediaType.VIDEO) {
            mVideoInfo = info
            mCurrentProgress = info.duration * 1.0f / mVideoParams.maxDuration
        }
        isShowViewLiveData.postValue(true)
        if ((mAudioInfo == null || mVideoInfo == null)) {
            return
        }
        //todo待完成
        mOperateStarted = false
    }

    /**
     * 录制帧可用
     * @param texture
     * @param timestamp
     */
    fun onRecordFrameAvailable(texture: Int, timestamp: Long) {
        if (mOperateStarted && mHWMediaRecorder != null && mHWMediaRecorder.isRecording()) {
            mHWMediaRecorder.frameAvailable(texture, timestamp)
        }
    }
}