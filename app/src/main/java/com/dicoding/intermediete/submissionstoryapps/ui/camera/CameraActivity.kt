package com.dicoding.intermediete.submissionstoryapps.ui.camera

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dicoding.intermediete.submissionstoryapps.createFile
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityCameraBinding
import com.dicoding.intermediete.submissionstoryapps.ui.story.AddNewStoryActivity

class CameraActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PHOTO = "extra_photo"
    }

    private val binding: ActivityCameraBinding by lazy {
        ActivityCameraBinding.inflate(layoutInflater)
    }

    private var imageCapture: ImageCapture?= null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupCamera()

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupCamera() {
        binding.cameraCapture.setOnClickListener {
            takePhoto()
        }
        binding.cameraSwitch.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
    }

    public override fun onResume() {
        super.onResume()
        setupView()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(
            this@CameraActivity
        )
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this@CameraActivity,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(
            this@CameraActivity
        ))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(photoFile)
            .build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(
                this@CameraActivity
            ), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil foto",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra(EXTRA_PHOTO, photoFile)
                    intent.putExtra(
                        "isBackCamera",
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    setResult(AddNewStoryActivity.RESULT_OK, intent)
                    finish()
                }
            }
        )
    }
}