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
import java.util.Calendar


class VideoViewFragment : Fragment() {
    private lateinit var videoView: VideoView
    private var currentVideoIndex = 0
    private var mediaPlayer= MediaPlayer()
    private var currentPosition: Int = 0
    private var playbackPosition = 0
    private var playWhenReady = false
    var currentTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        currentTime = Calendar.getInstance().timeInMillis
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
        prepareMedia(currentVideoIndex)
    }

    override fun onResume() {
        super.onResume()
        playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    private fun prepareMedia(index: Int) {
        val packageName = "com.example.myapp"
//        videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/${R.raw.test_sound}"))
//        videoView.start()
        val mediaController = MediaController(requireContext())
//        videoView.setMediaController(mediaController)
        val videoList = mutableListOf(R.raw.test_sound, R.raw.test_sound_2)
        if (index >= 0 && index < videoList.size) {
            videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/${videoList[index]}"))
            videoView.setOnPreparedListener { mediaPlayer ->
                this.mediaPlayer = mediaPlayer
                mediaPlayer.start()
                videoView.setMediaController(mediaController)
                mediaPlayer.setOnCompletionListener {
                    // Phát video tiếp theo khi video hiện tại phát xong
                    currentVideoIndex = (currentVideoIndex + 1) % videoList.size
                    prepareMedia(currentVideoIndex)
                }
                mediaPlayer.isLooping = true
            }
        } else {
            // Đã phát xong danh sách video
        }
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
        mediaPlayer.setOnInfoListener { mp, what, extra ->
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                val current = Calendar.getInstance().timeInMillis
                val playTime = current - currentTime
                Log.d("CHECK_LOG", "time start play: $playTime")
            }
            false
        }
    }
}