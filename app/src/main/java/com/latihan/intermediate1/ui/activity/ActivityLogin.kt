package com.latihan.intermediate1.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.latihan.intermediate1.R
import com.latihan.intermediate1.data.model.UserSession
import com.latihan.intermediate1.databinding.ActivityLoginBinding
import com.latihan.intermediate1.ui.viewmodel.LoginViewModel
import com.latihan.intermediate1.ui.viewmodel.ViewModelFactory
import com.latihan.intermediate1.utils.Result

class ActivityLogin : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Login"

        factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        action()
        playAnimation()

    }


    private fun action() {
        binding.btnLogin.setOnClickListener {
            login()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

        }
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, ActivityRegister::class.java)
            startActivity(
                intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this@ActivityLogin as Activity)
                    .toBundle()
            )
        }
    }

    private fun login() {
        val email = binding.tvEmail.text.toString().trim()
        val password = binding.tvPassword.text.toString().trim()
        when {
            email.isEmpty() -> {
                binding.tilEmail.error = "Masukkan email"
            }

            password.isEmpty() -> {
                binding.textInputLayout.error = "Masukkan password"
            }

            else -> {
                viewModel.postLogin(email, password).observe(this) {
                    binding.progressBar.visibility = View.VISIBLE
                    if (it != null) {
                        when (it) {
                            is Result.Success -> {
                                showLoading(false)
                                Toast.makeText(this, R.string.loginYes, Toast.LENGTH_SHORT).show()
                                val response = it.data
                                saveSessionData(
                                    UserSession(
                                        response.loginResult.name,
                                        response.loginResult.token,
                                        response.loginResult.userId,
                                        true
                                    )
                                )
                                val mainPage = Intent(this@ActivityLogin, ActivityMain::class.java)
                                startActivity(mainPage)
                                finish()
                            }

                            is Result.Loading -> showLoading(true)
                            is Result.Error -> {
                                Toast.makeText(this, R.string.loginNo, Toast.LENGTH_SHORT).show()
                                showLoading(false)
                            }

                            else -> {
                                Toast.makeText(this, R.string.errorsys, Toast.LENGTH_SHORT).show()
                                showLoading(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveSessionData(user: UserSession) {
        viewModel.saveSession(user)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
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
}