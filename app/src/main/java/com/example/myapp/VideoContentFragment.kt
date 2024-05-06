package com.example.myapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapp.databinding.VideoContentFragmentBinding
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class VideoContentFragment : Fragment() {
    private lateinit var binding: VideoContentFragmentBinding
    private var player: ExoPlayer? = null
    private var playbackPosition = 0L

    companion object {
        const val URI_STRING = "asset:///test_sound.mp4"
        const val _URI_STRING =
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = VideoContentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("CHECK_LOG", "onAttach: ")
        initializePlayer(requireContext())
        setListener()
        preparePlayer()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("CHECK_LOG", "onViewCreated")
        preparePlayer()
        binding.exoplayerView.player = player
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        super.onDestroyView()
    }

    private fun initializePlayer(context: Context) {
        // setup buffer
        val renderersFactory = DefaultRenderersFactory(requireContext())
        val minBufferMs = 100
        val maxBufferMs = 200
        val bufferForPlaybackMs = 100
        val bufferForPlaybackAfterRebufferMs = 50
        val loadControl = DefaultLoadControl.Builder().setBufferDurationsMs(
            minBufferMs, maxBufferMs, bufferForPlaybackMs, bufferForPlaybackAfterRebufferMs
        ).setTargetBufferBytes(-1).setPrioritizeTimeOverSizeThresholds(true).build()
        player = ExoPlayer.Builder(context, renderersFactory).setLoadControl(loadControl).build()
    }

    private fun preparePlayer() {
        if (player != null) {
            with(player!!) {
                val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(URI_STRING))
                clearMediaItems()
                setMediaItem(mediaItem)
                prepare()
                seekTo(playbackPosition)
                repeatMode = Player.REPEAT_MODE_ONE
//                play()
            }
        } else println("exoplayer is null")
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            exoPlayer.apply {
                stop()
                release()
                clearVideoSurface()
            }
        }
        player = null
        with(binding) {
            exoplayerView.apply {
                player?.release()
                player = null
                removeAllViews()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    private fun setListener() {

        player?.addListener(object : Player.Listener {

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.e("CHECK_LOG", "onPlayerError: $error")
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    Log.d("CHECK_LOG", "isPlaying: $isPlaying")
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onPositionDiscontinuity(reason: Int) {
                Player.DISCONTINUITY_REASON_INTERNAL
                if (player?.playWhenReady == true) player?.playWhenReady = true
            }

            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) { // check player play back state
                    Player.STATE_BUFFERING -> {
                        Log.e("CHECK_LOG", "PlaybackStatus.STATE_BUFFERING")
                    }

                    Player.STATE_ENDED -> {
                        Log.e("CHECK_LOG", "STATE_ENDED")
                        player?.playWhenReady = true
                    }

                    Player.STATE_IDLE -> {
                        Log.e("CHECK_LOG", "STATE_IDLE")
                        player?.playWhenReady = true
                    }

                    Player.STATE_READY -> {
                        if (playWhenReady) {
                            Log.e("CHECK_LOG", "PlaybackStatus.PLAYING")
                        } else {
                            Log.e("CHECK_LOG", "PlaybackStatus.PAUSED")
                        }
                    }

                    else -> {
                        Log.e("CHECK_LOG", "PlaybackStatus.IDLE")
                    }
                }
            }
        })
    }
}