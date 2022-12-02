package com.dicoding.intermediete.submissionstoryapps.ui.splash

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.dicoding.intermediete.submissionstoryapps.R
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivitySplashScreenBinding
import com.dicoding.intermediete.submissionstoryapps.ui.main.MainActivity

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        const val DELAY_TIME = 3000
    }

    private val binding: ActivitySplashScreenBinding by lazy {
        ActivitySplashScreenBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
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

    private fun setupAction() {
        val image = binding.splashScreen
        Glide.with(
            this@SplashScreenActivity
        )
            .load(R.drawable.image_splash_screen)
            .into(image)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(
                this@SplashScreenActivity,
                MainActivity::class.java
            ))
            finish()
        }, DELAY_TIME.toLong())
    }
}