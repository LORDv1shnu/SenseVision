package com.example.sensevision

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var captionText: TextView
    private lateinit var statusText: TextView  // Added statusText
    private lateinit var startAutoButton: Button
    private lateinit var stopAutoButton: Button

    private lateinit var tts: TextToSpeech
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService

    private val handler = Handler(Looper.getMainLooper())
    private val autoCaptureRunnable = object : Runnable {
        override fun run() {
            capturePhoto()
            handler.postDelayed(this, 3000) // every 3 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Ensure the correct layout file is used

        // Initialize views from XML
        imageView = findViewById(R.id.imageView)
        captionText = findViewById(R.id.captionText)
        statusText = findViewById(R.id.statusText)  // Initialize statusText
        startAutoButton = findViewById(R.id.startAutoButton)
        stopAutoButton = findViewById(R.id.stopAutoButton)

        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
            }
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                101
            )
        }

        startAutoButton.setOnClickListener {
            statusText.text = "Auto capture started"
            handler.post(autoCaptureRunnable)
            Toast.makeText(this, "Auto capture started", Toast.LENGTH_SHORT).show()
        }

        stopAutoButton.setOnClickListener {
            handler.removeCallbacks(autoCaptureRunnable)
            statusText.text = "Auto capture stopped"
            Toast.makeText(this, "Auto capture stopped", Toast.LENGTH_SHORT).show()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder().build()
            val preview = Preview.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Binding failed", e)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturePhoto() {
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            createTempFile()
        ).build()

        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = imageProxyToBitmap(image)
                    image.close()
                    imageView.setImageBitmap(bitmap)

                    sendImageToServer(bitmap) { caption ->
                        captionText.text = caption
                        speak(caption)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@MainActivity, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    statusText.text = "Capture failed: ${exception.message}" // Update status text
                }
            })
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun sendImageToServer(bitmap: Bitmap, onResult: (String) -> Unit) {
        Thread {
            try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val byteArray = stream.toByteArray()
                val url = URL("http://192.168.43.50:5000/upload")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/octet-stream")
                connection.outputStream.use { it.write(byteArray) }

                val response = connection.inputStream.bufferedReader().use { it.readText() }

                runOnUiThread {
                    onResult(response)
                    statusText.text = "Image sent to server" // Update status text
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Server error: ${e.message}", Toast.LENGTH_LONG).show()
                    statusText.text = "Server error: ${e.message}" // Update status text
                }
            }
        }.start()
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        cameraExecutor.shutdown()
        handler.removeCallbacks(autoCaptureRunnable)
    }
}
