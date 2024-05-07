package com.example.myapp

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Point
import android.media.ImageReader
import android.os.Build
import android.view.Surface
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream

@RequiresApi(Build.VERSION_CODES.O)

class ImageAvailableListener() : ImageReader.OnImageAvailableListener {

    private var width = 0
    private var height = 0
    private var imageReader: ImageReader? = null
    private var svc: ScreenshotService? = null
    private var latestBitmap: Bitmap? = null

    fun ImageTransmogrifier(svc: ScreenshotService) {
        this.svc = svc
        val display = svc.getWindowManager()!!.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        var width = size.x
        var height = size.y
        while (width * height > 2 shl 19) {
            width = width shr 1
            height = height shr 1
        }
        this.width = width
        this.height = height
        imageReader = ImageReader.newInstance(
            width, height,
            PixelFormat.RGBA_8888, 2
        )
        imageReader?.setOnImageAvailableListener(this, svc.getHandler())
    }

    override fun onImageAvailable(reader: ImageReader?) {
        val image = imageReader!!.acquireLatestImage()
        if (image != null) {
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width
            val bitmapWidth = width + rowPadding / pixelStride
            if (latestBitmap == null || latestBitmap!!.getWidth() != bitmapWidth || latestBitmap!!.getHeight() != height) {
                if (latestBitmap != null) {
                    latestBitmap!!.recycle()
                }
                latestBitmap = Bitmap.createBitmap(
                    bitmapWidth,
                    height, Bitmap.Config.ARGB_8888
                )
            }
            latestBitmap!!.copyPixelsFromBuffer(buffer)
            image.close()
            val baos = ByteArrayOutputStream()
            val cropped = Bitmap.createBitmap(
                latestBitmap!!, 0, 0,
                width, height
            )
            cropped.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val newPng = baos.toByteArray()
            svc!!.processImage(newPng)
        }
    }

    fun getSurface(): Surface {
        return imageReader!!.surface
    }

    fun getWidth(): Int {
        return width
    }

    fun getHeight(): Int {
        return height
    }

    fun close() {
        imageReader!!.close()
    }
}