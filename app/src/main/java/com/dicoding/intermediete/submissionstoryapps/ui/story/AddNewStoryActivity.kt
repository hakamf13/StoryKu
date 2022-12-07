package com.dicoding.intermediete.submissionstoryapps.ui.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.intermediete.submissionstoryapps.*
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.AddNewStoryResponse
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityAddNewStoryBinding
import com.dicoding.intermediete.submissionstoryapps.ui.main.MainActivity
import com.dicoding.intermediete.submissionstoryapps.ui.main.MainActivity.Companion.STORY_UPLOADED
import com.dicoding.intermediete.submissionstoryapps.ui.welcome.WelcomeActivity
import com.dicoding.intermediete.submissionstoryapps.utils.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@Suppress("DEPRECATION")
class AddNewStoryActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val binding: ActivityAddNewStoryBinding by lazy {
        ActivityAddNewStoryBinding.inflate(layoutInflater)
    }

    private lateinit var addNewStoryViewModel: AddNewStoryViewModel

    private lateinit var currentPhotoPath: String

    private var getFile: File? = null

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
        ) { session ->
            if (session.isLogin) {
                Log.d("TOKEN", "Token has been found")
            } else {
                startActivity(Intent(
                    this@AddNewStoryActivity,
                    WelcomeActivity::class.java
                ))
                finish()
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
            MediaStore.ACTION_IMAGE_CAPTURE
        )
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@AddNewStoryActivity,
                "com.dicoding.intermediete.submissionstoryapps",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
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

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(
                    getFile?.path,
                ), isBackCamera
            )
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
        if (getFile != null) {

            addNewStoryViewModel.getUserToken().observe(
                this@AddNewStoryActivity
            ) {
                if (it.isLogin) {
                    val token = "Bearer ${it.token}"
                    val file = reduceFileImage(getFile as File)
                    val description = binding.edDescriptionImage.text
                        .toString()
                        .trim()
                        .toRequestBody("text/plain".toMediaType())
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipartBody: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )
                    val client = ApiConfig.getApiService().getStory(token, imageMultipartBody, description)
                    client.enqueue(object : Callback<AddNewStoryResponse> {

                        override fun onResponse(
                            call: Call<AddNewStoryResponse>,
                            response: Response<AddNewStoryResponse>
                        ) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()!!
                                if (!responseBody.error) {
                                    Log.d("STORY", "Story has been fetched")
                                    AlertDialog.Builder(
                                        this@AddNewStoryActivity
                                    ).apply {
                                        setTitle("Yes!")
                                        setMessage("Story berhasil di-upload")
                                        setPositiveButton("Lanjut") { _, _ ->
                                            val intent = Intent(
                                                this@AddNewStoryActivity,
                                                MainActivity::class.java
                                            )
                                            intent.putExtra(STORY_UPLOADED, true)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                } else {
                                    Log.e("STORY_ERROR", "Story not fetch yet")
                                }
                            } else {
                                Log.e("STORY_ERROR", "Story not fetch yet")
                            }
                        }

                        override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                            Log.e("STORY_ERROR", "Story not fetch yet")
                        }

                    })
                }
            }

        } else {
            Toast.makeText(
                this@AddNewStoryActivity,
                "Masukkan gambar dan deskripsinya dulu ya!",
                Toast.LENGTH_SHORT
            ).show()
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