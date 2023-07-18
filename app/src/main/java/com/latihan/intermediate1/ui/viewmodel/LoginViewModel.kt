package com.latihan.intermediate1.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latihan.intermediate1.data.model.UserSession
import com.latihan.intermediate1.data.model.data.Repository
import kotlinx.coroutines.launch

class LoginViewModel (private val repo: Repository) : ViewModel() {
    fun postLogin(email:String, password:String) = repo.requestLogin(email, password)

    fun saveSession(userModel: UserSession) {
        viewModelScope.launch {
            repo.saveSessionData(userModel)
        }
    }
}