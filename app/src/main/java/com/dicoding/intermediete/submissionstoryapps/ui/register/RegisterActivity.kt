package com.dicoding.intermediete.submissionstoryapps.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.intermediete.submissionstoryapps.ViewModelFactory
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.RegisterResponse
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        setupAnimation()
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

        registerViewModel = ViewModelProvider(
            this@RegisterActivity,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]

        registerViewModel.isLoading.observe(
            this@RegisterActivity
        ) { loader ->
            showLoading(loader)
        }

    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {

            binding.nameEditTextLayout.isErrorEnabled = false
            binding.emailEditTextLayout.isErrorEnabled = false

            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = "Masukkan email"
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan password"
                }

                else -> {
                    val client = ApiConfig.getApiService().postRegister(name, email, password)
                    client.enqueue(object : Callback<RegisterResponse> {

                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()!!
                                if (!responseBody.error) {
                                    Log.d("REGISTER", "Register success")
                                    AlertDialog.Builder(this@RegisterActivity).apply {
                                        setTitle("Yes!")
                                        setMessage("Akunnya sudah jadi nih. Yuk, login dan bagikan pengalamanmu!.")
                                        setPositiveButton("Lanjut") { _, _ ->
                                            finish()
                                        }
                                        create()
                                        show()
                                    }
                                } else {
                                    Log.e("REGISTER_ERROR", "errorRegister: ${responseBody.registerResult}")
                                }
                            } else {
                                Log.e("REGISTER_ERROR", "registerError: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            Log.e("REGISTER_ERROR", "registerError: ${t.message.toString()}")
                        }

                    })
                }
            }
        }
    }

    private fun setupAnimation() {

        ObjectAnimator.ofFloat(binding.imageViewRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameEdit = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, nameEdit, emailEdit, passEdit, register)
            start()
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}