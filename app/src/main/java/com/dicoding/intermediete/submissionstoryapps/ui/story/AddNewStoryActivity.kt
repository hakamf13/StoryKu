package com.dicoding.intermediete.submissionstoryapps.ui.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.intermediete.submissionstoryapps.ViewModelFactory
import com.dicoding.intermediete.submissionstoryapps.compressQuality
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityAddNewStoryBinding
import com.dicoding.intermediete.submissionstoryapps.rotateBitmap
import com.dicoding.intermediete.submissionstoryapps.ui.camera.CameraActivity
import com.dicoding.intermediete.submissionstoryapps.ui.welcome.WelcomeActivity
import com.dicoding.intermediete.submissionstoryapps.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class AddNewStoryActivity : AppCompatActivity() {

    companion object {
        const val RESULT_OK = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val binding: ActivityAddNewStoryBinding by lazy {
        ActivityAddNewStoryBinding.inflate(layoutInflater)
    }

    private lateinit var addNewStoryViewModel: AddNewStoryViewModel

    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this@AddNewStoryActivity,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupView()
        setupViewModel()
        setupAction()
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

    private fun setupViewModel() {

        addNewStoryViewModel = ViewModelProvider(
            this@AddNewStoryActivity,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddNewStoryViewModel::class.java]

        addNewStoryViewModel.getUserToken().observe(
            this@AddNewStoryActivity
        ) { state ->
            if (state.login) {
                this.token = state.token
            } else {
                startActivity(Intent(
                    this@AddNewStoryActivity,
                    WelcomeActivity::class.java
                ))
            }
        }

        addNewStoryViewModel.isLoading.observe(
            this@AddNewStoryActivity
        ) { loader ->
            showLoading(loader)
        }

    }

    private fun setupAction() {

        binding.buttonCamera.setOnClickListener {
            startCamera()
        }

        binding.buttonGalery.setOnClickListener {
            startGallery()
        }

        binding.buttonUpload.setOnClickListener {
            uploadImage()
        }

    }

    private fun startCamera() {
        val intent = Intent(
            this@AddNewStoryActivity,
            CameraActivity::class.java
        )
        launcherIntentCamera.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(
            intent,
            "Pilih gambar"
        )
        launcherIntentGallery.launch(chooser)
    }

    private var getFile: File? = null
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile

            val result = rotateBitmap(
                BitmapFactory.decodeFile(
                    getFile?.path,
                ), isBackCamera
            )
            result.compress(
                Bitmap.CompressFormat.JPEG,
                compressQuality(myFile),
                FileOutputStream(getFile))

            binding.viewPreviewImage.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImage: Uri = it.data?.data as Uri
            val myFile = uriToFile(
                selectedImage,
                this@AddNewStoryActivity
            )
            getFile = myFile

            binding.viewPreviewImage.setImageURI(selectedImage)
        }
    }

    private fun uploadImage() {
        when {

            getFile == null -> {
                Toast.makeText(
                    this@AddNewStoryActivity,
                    "Masukkan gambarnya dulu ya.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.edDescriptionImage.text?.isNotEmpty() == false -> {
                Toast.makeText(
                    this@AddNewStoryActivity,
                    "Tulis deskripsinya dulu ya.",
                    Toast.LENGTH_SHORT
                ).show()
//                return
            }

            else -> {
                binding.progressBar.visibility = View.VISIBLE
                val file = getFile as File
                val description = binding.edDescriptionImage.text.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                addNewStoryViewModel.uploadImage(imageMultiPart, description, token)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this@AddNewStoryActivity,
                    "Maaf ya, Kamu tidak ada izin akses.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}