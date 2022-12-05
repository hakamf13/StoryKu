package com.dicoding.intermediete.submissionstoryapps.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
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
import com.dicoding.intermediete.submissionstoryapps.data.local.UserModel
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.LoginResponse
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityLoginBinding
import com.dicoding.intermediete.submissionstoryapps.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var loginViewModel: LoginViewModel

    private lateinit var user: UserModel


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
        loginViewModel = ViewModelProvider(
            this@LoginActivity,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getUserToken().observe(
            this@LoginActivity
        ) {
            this.user = it
        }

        loginViewModel.isLoading.observe(
            this@LoginActivity
        ) { loader ->
            showLoading(loader)
        }
    }

    private fun setupAction(){
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {

                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan Email"
                }

                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan Password"
                }

                else -> {
//                    loginViewModel.login(email, password)
                    val client = ApiConfig.getApiService().postLogin(email, password)
                    client.enqueue(object : Callback<LoginResponse> {

                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()!!
                                if (!responseBody.error) {
                                    loginViewModel.userLogin()
                                    loginViewModel.userSave(responseBody.loginResult)
                                    Log.d("LOGIN", responseBody.loginResult.toString())
                                    AlertDialog.Builder(
                                        this@LoginActivity
                                    ).apply {
                                        setTitle("Yes!")
                                        setMessage("Kamu berhasil masuk. Sudah tidak sabar untuk membagikan pengalamanmu ya?")
                                        setPositiveButton("Lanjut") { _, _ ->
                                            val intent = Intent(
                                                this@LoginActivity,
                                                MainActivity::class.java
                                            )
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                            startActivity(intent)
                                            finish()
                                        }
                                        create()
                                        show()
                                    }
                                } else {
                                    Log.e("LOGIN_ERROR", "loginError: ${responseBody.message}")
                                }
                            } else {
                                Log.e("LOGIN_ERROR", "loginError: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Log.e("LOGIN_ERROR", "loginError: ${t.message.toString()}")
                        }
                    })
                }
            }
        }
    }

    private fun setupAnimation() {

        ObjectAnimator.ofFloat(binding.imageViewLogin, View.TRANSLATION_X, -30F, 30F).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(600)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(600)
        val emailText = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(600)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(600)
        val passText = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(600)
        val passEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(600)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(600)

        AnimatorSet().apply {
            playSequentially(title, message, emailText, emailEdit, passText, passEdit, login)
            start()
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
