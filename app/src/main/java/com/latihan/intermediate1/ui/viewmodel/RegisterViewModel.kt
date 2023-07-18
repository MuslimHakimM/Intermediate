package com.latihan.intermediate1.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.latihan.intermediate1.data.model.data.Repository

class RegisterViewModel(private val repo: Repository) : ViewModel() {
    fun postRegister(name: String, email: String, password: String) =
        repo.requestRegister(name, email, password)
}
