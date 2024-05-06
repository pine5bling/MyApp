package com.example.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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

        addFragment(R.id.flContainer1,videoContentFragment, "1")
        addFragment(R.id.flContainer2,videoViewFragment, "2")
        addFragment(R.id.flContainer3,VideoViewFragmentV3(), "3")
        addFragment(R.id.flContainer4,VideoViewFragmentV4(), "4")
    }

    private fun addFragment(containerViewId : Int,fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().apply {
            add(containerViewId, fragment, tag)
            commit()
        }
    }
}