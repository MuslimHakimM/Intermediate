package com.latihan.intermediate1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.latihan.intermediate1.data.model.UserSession
import com.latihan.intermediate1.data.model.data.Repository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel (private val storyRepository: Repository) : ViewModel() {
    fun uploadFile(token: String, file: MultipartBody.Part, description: RequestBody) =
        storyRepository.addStory(token, file, description)

    fun getUser(): LiveData<UserSession> {
        return storyRepository.getUser()
    }
}