package com.dicoding.intermediete.submissionstoryapps.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.LoginResult
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityLoginBinding
import com.dicoding.intermediete.submissionstoryapps.ui.main.MainActivity
import com.dicoding.intermediete.submissionstoryapps.utils.ViewModelFactory
import com.dicoding.intermediete.submissionstoryapps.utils.Result

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
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

    private fun setupAction() {
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
                    postLogin(email, password)
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

    private fun postLogin(email: String,password: String) {
        loginViewModel.postLogin(email, password).observe(this@LoginActivity) {
            if (it != null) {
                when (it) {

                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val user = it.data
                        userSave(user)

                        AlertDialog.Builder(this@LoginActivity).apply {
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
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@LoginActivity,
                            it.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
    }

    private fun userSave(user: LoginResult) {
        loginViewModel.userSave(user)
    }
}
