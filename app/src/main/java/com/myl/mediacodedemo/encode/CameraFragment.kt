package com.myl.mediacodedemo.encode

import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.myl.mediacodedemo.databinding.FragmentCameraBinding
import com.myl.mediacodedemo.encode.Constants.DEFAULT_OPENGL_VERSION
import com.myl.mediacodedemo.encode.viewmodel.RecordViewModel
import com.myl.mediacodedemo.ui.RecordButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CameraFragment : Fragment() {

    private lateinit var cameraBinding: FragmentCameraBinding
    private val recordViewModel: RecordViewModel by activityViewModels()
//    private lateinit var mRenderer: RecordRenderer

    companion object {
        private const val TAG = "CameraFragment"
        private const val DEFAULT_RECORD_S = 20
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cameraBinding = FragmentCameraBinding.inflate(inflater, container, false)
        return cameraBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordViewModel.init(requireActivity())
        recordViewModel.setRecordSeconds(DEFAULT_RECORD_S)
        initView()
        initObserver()
    }

    private fun initObserver() {
        recordViewModel.isShowViewLiveData.observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch(Dispatchers.Main) {
                showOrHideView(it)
            }
        })
        recordViewModel.recordProgressLiveData.observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch(Dispatchers.Main) {
                cameraBinding.recordProgressView.setProgress(it)
            }
        })
        recordViewModel.frameAvailableLiveData.observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch(Dispatchers.Main) {
                cameraBinding.glRecordView.requestRender()
            }
        })
        recordViewModel.surfaceTextureLiveData.observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch(Dispatchers.Main) {
                bindSurfaceTexture(it)
            }
        })
    }

    private fun showOrHideView(isShow: Boolean) {
        if (isShow) {
            cameraBinding.btnNext.visibility = View.VISIBLE
            cameraBinding.btnRecord.reset()
        } else {
            cameraBinding.btnNext.visibility = View.GONE
        }
    }

    private fun initView() {
        cameraBinding.glRecordView.apply {
            setEGLContextClientVersion(DEFAULT_OPENGL_VERSION)
            setRenderer(recordViewModel.mRenderer)
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }
        cameraBinding.btnRecord.addRecordStateListener(object : RecordButton.RecordStateListener {
            override fun onRecordStart() {
                recordViewModel.startRecord()
            }

            override fun onRecordStop() {
                recordViewModel.stopRecord()
            }

            override fun onZoom(percent: Float) {
            }

        })
    }

    override fun onResume() {
        super.onResume()
        cameraBinding.glRecordView.onResume()
        recordViewModel.openCamera()
    }

    /**
     * ?????????????????????SurfaceTexture
     * @param surfaceTexture
     */
    private fun bindSurfaceTexture(surfaceTexture: SurfaceTexture) {
        //?????????????????????????????????????????????UI???????????????OpenGL????????????????????????????????????????????????????????????Renderer???????????????
        // ?????????????????????????????????????????????????????????????????????UI?????????OpenGL??????????????????????????????
        //??????queueEvent()????????????????????????????????????????????????????????????????????????GLSurfaceView.Renderer???????????????????????????
        // ???????????????????????????????????????
        cameraBinding.glRecordView.queueEvent {
            recordViewModel.mRenderer.bindSurfaceTexture(
                surfaceTexture
            )
        }
    }

    override fun onPause() {
        super.onPause()
        cameraBinding.glRecordView.onPause()
        recordViewModel.closeCamera()
        recordViewModel.mRenderer.clear()
    }

    override fun onDestroy() {
        recordViewModel.release()
        super.onDestroy()
    }
}