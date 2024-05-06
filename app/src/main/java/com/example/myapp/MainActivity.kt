package com.example.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapp.databinding.MainActivityBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private val videoContentFragment: Fragment = VideoContentFragment()
//    private val mediaPlayerFragment: Fragment = MediaPlayerFragment()
    private val videoViewFragment: Fragment = VideoViewFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.flContainer, videoViewFragment)
//            hide(videoContentFragment)
            commit()
        }
    }
}