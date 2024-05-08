package com.example.myapp

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapp.databinding.MainActivityBinding
import eu.bolt.screenshotty.Screenshot
import eu.bolt.screenshotty.ScreenshotActionOrder
import eu.bolt.screenshotty.ScreenshotBitmap
import eu.bolt.screenshotty.ScreenshotManagerBuilder
import eu.bolt.screenshotty.rx.asRxScreenshotManager
import io.reactivex.disposables.Disposables
import java.io.File
import java.io.FileOutputStream


@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private val exoPlayerFra: Fragment = ExoPlayerFragment()
    private val mediaPlayerFra: Fragment = MediaPlayerFragment()

    companion object {
        private const val REQUEST_SCREENSHOT_PERMISSION = 1234
    }

    private val screenshotManager by lazy {
        ScreenshotManagerBuilder(this)
            .withPermissionRequestCode(REQUEST_SCREENSHOT_PERMISSION)
            .withCustomActionOrder(ScreenshotActionOrder.pixelCopyFirst())
            .build()
            .asRxScreenshotManager()
    }

    private var screenshotSubscription = Disposables.disposed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addFragment(R.id.flContainer1, mediaPlayerFra)

        binding.btnCapture.setOnClickListener {
            makeScreenshot()
        }
    }

    private fun makeScreenshot() {
        screenshotSubscription.dispose()
        screenshotSubscription = screenshotManager
            .makeScreenshot()
            .subscribe(
                ::handleScreenshot,
                ::handleScreenshotError
            )
    }

    private fun addFragment(containerViewId: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            add(containerViewId, fragment)
            commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        screenshotSubscription.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        screenshotManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleScreenshot(screenshot: Screenshot) {
        val bitmap = when (screenshot) {
            is ScreenshotBitmap -> screenshot.bitmap
        }
        binding.ivPreview.setImageBitmap(bitmap)
        saveScreenShot(bitmap)
    }

    private fun saveScreenShot(bitmap: Bitmap) {
        val file = File(filesDir, "screenshot.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    private fun handleScreenshotError(t: Throwable) {
        Log.e(javaClass.simpleName, t.message, t)
        Toast.makeText(this, t.message, Toast.LENGTH_LONG).show()
    }
}