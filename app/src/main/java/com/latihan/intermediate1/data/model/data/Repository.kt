package com.latihan.intermediate1.data.model.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.latihan.intermediate1.LoginPreference
import com.latihan.intermediate1.data.model.UserSession
import com.latihan.intermediate1.data.model.login.LoginResponse
import com.latihan.intermediate1.data.model.login.RegisterResponse
import com.latihan.intermediate1.data.model.stories.AddResponse
import com.latihan.intermediate1.data.model.stories.StoriesResponse
import com.latihan.intermediate1.data.model.stories.Story
import com.latihan.intermediate1.data.remote.ApiService
import com.latihan.intermediate1.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(private val pref: LoginPreference, private val apiService: ApiService) {

    fun requestLogin(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.loginUser(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.d("Login", e.message.toString())
            emit(Result.Error(e.message.toString()))
        }
    }

    fun requestRegister(name: String, email: String, password: String)
            : LiveData<Result<RegisterResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.registerUser(name, email, password)
                emit(Result.Success(response))
            } catch (e: Exception) {
                Log.d("Signup", e.message.toString())
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getLocationStory(token: String): LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories(token, location = 1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.d("Signup", e.message.toString())
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStory(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                SourcePaging(apiService, pref)
            }
        ).liveData
    }

    fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<AddResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadImage(token, file, description)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getUser(): LiveData<UserSession> {
        return pref.getUser().asLiveData()
    }

    suspend fun saveSessionData(user: UserSession) {
        pref.setUser(user)
    }

    suspend fun logout() {
        pref.logout()
    }
}