package com.example.myapp

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.example.myapp.databinding.MediaPlayerFragmentBinding


class VideoViewFragment : Fragment() {
    private lateinit var videoView: VideoView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = MediaPlayerFragmentBinding.inflate(inflater, container, false)
        videoView = binding.videoView
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("CHECK_LOG", "onAttach: ")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("CHECK_LOG", "onViewCreated")
        setListener()
        prepareMedia()
    }

    private fun prepareMedia() {
        val packageName = "com.example.myapp"
        videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/${R.raw.test_sound}"))
        videoView.start()
        val mediaController = MediaController(requireContext())
        videoView.setMediaController(mediaController)
    }

    private fun setListener() {
        videoView.setOnPreparedListener {
            Log.d("CHECK_LOG", "Video prepare")
        }
        videoView.setOnCompletionListener {
            Log.d("CHECK_LOG", "Video play end")
        }
        videoView.setOnErrorListener { mediaPlayer, what, extra ->
            false // Trả về true nếu bạn xử lý lỗi, ngược lại trả về false để VideoView xử lý mặc định.
        }
    }
}