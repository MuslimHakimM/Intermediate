package com.latihan.intermediate1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.latihan.intermediate1.data.model.UserSession
import com.latihan.intermediate1.data.model.data.Repository

class MapsViewModel(private val repo: Repository) : ViewModel() {
    fun getLocation(token: String) =
        repo.getLocationStory(token)

    fun getAllData(): LiveData<UserSession> {
        return repo.getUser()
    }
}

