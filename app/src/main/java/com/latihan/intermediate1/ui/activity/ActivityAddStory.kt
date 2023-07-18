package com.latihan.intermediate1.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.latihan.intermediate1.R
import com.latihan.intermediate1.databinding.ActivityAddStoryBinding
import com.latihan.intermediate1.ui.viewmodel.AddStoryViewModel
import com.latihan.intermediate1.ui.viewmodel.ViewModelFactory
import com.latihan.intermediate1.utils.Result
import com.latihan.intermediate1.utils.createTempFile
import com.latihan.intermediate1.utils.reduceFileImage
import com.latihan.intermediate1.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ActivityAddStory : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: AddStoryViewModel

    private var getFile: File? = null

    companion object {

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Story"
        factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[AddStoryViewModel::class.java]

        binding.btnAddCamera.setOnClickListener { startTakePhoto() }
        binding.btnAddGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener {
            addStory()
            startActivity(Intent(this, ActivityMain::class.java))

            finish()
        }

    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@ActivityAddStory,
                "com.latihan.intermediate1",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.ivPreview.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@ActivityAddStory)

            getFile = myFile

            binding.ivPreview.setImageURI(selectedImg)
        }
    }

    private fun addStory() {

        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val descriptionText = binding.etAdd.text.toString()
            val description = descriptionText.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name, requestImageFile
            )

            viewModel.getUser().observe(this) { getToken ->
                val token = "Bearer ${getToken.token}"
                val file = reduceFileImage(getFile as File)
                val desc =
                    "${binding.etAdd.text}".toRequestBody("text/plain".toMediaTypeOrNull())
                val reqImgFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part =
                    MultipartBody.Part.createFormData("photo", file.name, reqImgFile)

                viewModel.uploadFile(token, imageMultipart, desc)
                    .observe(this@ActivityAddStory) {
                        when (it) {
                            is Result.Success -> {
                                showLoading(false)
                                startActivity(Intent(this, ActivityMain::class.java))
                                Toast.makeText(this, R.string.uploadYes, Toast.LENGTH_SHORT)
                                    .show()
                                finish()
                            }

                            is Result.Loading -> {
                                showLoading(true)
                            }

                            is Result.Error -> {
                                showLoading(false)
                                Toast.makeText(this, R.string.uploadNo, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
