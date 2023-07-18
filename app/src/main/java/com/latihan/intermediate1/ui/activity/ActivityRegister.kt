package com.latihan.intermediate1.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.latihan.intermediate1.R
import com.latihan.intermediate1.databinding.ActivityRegisterBinding
import com.latihan.intermediate1.ui.viewmodel.RegisterViewModel
import com.latihan.intermediate1.ui.viewmodel.ViewModelFactory
import com.latihan.intermediate1.utils.Result

class ActivityRegister : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Register"

        factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        action()
        playAnimation()
    }

    private fun action() {
        binding.btnRegister.setOnClickListener {
            register()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(email)
            startDelay = 500
        }.start()
    }

    private fun register() {
        val name = binding.tvName.text.toString().trim()
        val email = binding.tvEmail.text.toString().trim()
        val password = binding.tvPassword.text.toString().trim()
        viewModel.postRegister(name, email, password)

            .observe(this) {
                when (it) {
                    is Result.Success -> {
                        showLoading(false)
                        Toast.makeText(this, R.string.regyes, Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, ActivityLogin::class.java)
                        startActivity(intent,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this@ActivityRegister as Activity)
                                .toBundle()
                        )
                        finish()
                    }

                    is Result.Loading -> showLoading(true)
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, R.string.regno, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}