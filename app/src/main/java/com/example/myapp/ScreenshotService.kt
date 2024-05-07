package com.example.myapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.AudioManager
import android.media.ToneGenerator
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

@RequiresApi(Build.VERSION_CODES.O)

class ScreenshotService : Service() {
    private var mediaProjection: MediaProjection? = null
    private var vdisplay: VirtualDisplay? = null
    private val handlerThread = HandlerThread(
        javaClass.getSimpleName(),
        Process.THREAD_PRIORITY_BACKGROUND
    )
    private var handler: Handler? = null
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var windowManager: WindowManager? = null
    private var imageAvailableListener: ImageAvailableListener? = null
    private var resultCode = 0
    private var resultData: Intent? = null
    private val beeper = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    companion object {
        private const val CHANNEL_WHATEVER = "channel_whatever"
        private const val NOTIFY_ID = 9906
        const val EXTRA_RESULT_CODE = "resultCode"
        const val EXTRA_RESULT_INTENT = "resultIntent"
        val ACTION_RECORD: String = BuildConfig.APPLICATION_ID + ".RECORD"
        val ACTION_SHUTDOWN: String = BuildConfig.APPLICATION_ID + ".SHUTDOWN"
        const val VIRT_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
    }

    override fun onCreate() {
        super.onCreate()
        mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        handlerThread.start()
        handler = Handler(handlerThread.getLooper())
    }

    override fun onStartCommand(i: Intent, flags: Int, startId: Int): Int {
        if (i.action == null) {
//            resultCode = i.getIntExtra(EXTRA_RESULT_CODE, 1337)
//            resultData = i.getParcelableExtra(EXTRA_RESULT_INTENT)
//            foregroundify()
        } else if (ACTION_RECORD == i.action) {
            if (resultData != null) {
                startCapture()
            } else {
                val ui = Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(ui)
            }
        } else if (ACTION_SHUTDOWN == i.action) {
            beeper.startTone(ToneGenerator.TONE_PROP_NACK)
            stopForeground(true)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopCapture()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        throw IllegalStateException("Binding not supported. Go away.")
    }

    fun getWindowManager(): WindowManager? {
        return windowManager
    }

    fun getHandler(): Handler? {
        return handler
    }

    fun processImage(png: ByteArray?) {
        object : Thread() {
            override fun run() {
                val filename = "${System.currentTimeMillis()}.png"
                if (png != null) {
                    try {
                        val fos = openFileOutput(filename, Context.MODE_PRIVATE)
                        fos.write(png)
                        fos.close()
                        Log.d("SaveImage", "Image saved to internal storage.")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("SaveImage", "Error saving image to internal storage.")
                    }
                }
            }
        }.start()
        beeper.startTone(ToneGenerator.TONE_PROP_ACK)
        stopCapture()
    }

    private fun stopCapture() {
        if (mediaProjection != null) {
            mediaProjection!!.stop()
            vdisplay!!.release()
            mediaProjection = null
        }
    }

    private fun startCapture() {
        mediaProjection = mediaProjectionManager!!.getMediaProjection(resultCode, resultData!!)
        imageAvailableListener = ImageAvailableListener()
        val cb: MediaProjection.Callback = object : MediaProjection.Callback() {
            override fun onStop() {
                vdisplay!!.release()
            }
        }
        if (imageAvailableListener != null) {
            vdisplay = mediaProjection?.createVirtualDisplay(
                "andshooter",
                imageAvailableListener!!.getWidth(), imageAvailableListener!!.getHeight(),
                resources.displayMetrics.densityDpi,
                VIRT_DISPLAY_FLAGS, imageAvailableListener!!.getSurface(), null, handler
            )
        }
        mediaProjection?.registerCallback(cb, handler)
    }

    private fun foregroundify() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            notificationManager.getNotificationChannel(CHANNEL_WHATEVER) == null
        ) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_WHATEVER,
                    "Whatever", NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        val b: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_WHATEVER)
        b.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
        b.setContentTitle(getString(R.string.app_name))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker(getString(R.string.app_name))
        b.addAction(
            androidx.core.R.drawable.ic_call_answer,
            "ScreenShot",
            buildPendingIntent(ACTION_RECORD)
        )
        startForeground(NOTIFY_ID, b.build())
    }

    private fun buildPendingIntent(action: String): PendingIntent {
        val i = Intent(this, javaClass)
        i.setAction(action)
        return PendingIntent.getService(this, 0, i, PendingIntent.FLAG_IMMUTABLE)
    }
}