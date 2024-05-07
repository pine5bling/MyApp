package com.example.myapp

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapp.databinding.MainActivityBinding


@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private val exoPlayerFra: Fragment = ExoPlayerFragment()
    private val mediaPlayerFra: Fragment = MediaPlayerFragment()
    private val REQUEST_SCREENSHOT = 127
    private val REQ_PERMISSION = 2
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var windowManager: WindowManager
    private val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    companion object {
        var mediaProjection: MediaProjection? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        requestPermission()

        addFragment(R.id.flContainer1, mediaPlayerFra)

        mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(),
            REQUEST_SCREENSHOT
        )

        binding.btnCapture.setOnClickListener {

        }
    }

    private fun addFragment(containerViewId: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            add(containerViewId, fragment)
            commit()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCREENSHOT) {
            if (resultCode == RESULT_OK) {
                val i = Intent(this, ScreenshotService::class.java)
                    .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                    .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data)
//                startForegroundService(i)
            }
        }
    }

    //    private fun requestPermission() {
//        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
//            return ActivityCompat.requestPermissions(this, permissions, REQ_PERMISSION)
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            REQ_PERMISSION ->
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    println("permission granted")
//                } else {
//                    println("permission not granted")
//                }
//        }
//    }

}