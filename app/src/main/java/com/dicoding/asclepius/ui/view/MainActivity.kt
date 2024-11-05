package com.dicoding.asclepius.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.local.database.Result
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.viewmodel.MainViewModel
import com.dicoding.asclepius.viewmodel.MainViewModelFactory
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var viewModel: MainViewModel

    private val REQUIRED_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResults(results: MutableList<Classifications>?) {
                    results?.let { moveToResult(results) }
                }
            }
        )

        binding.galleryButton.setOnClickListener { startGallery() }

        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let { uri ->
                analyzeImage(uri)
            } ?: showToast(getString(R.string.empty_image_warning))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                showToast("Izin diberikan, Anda dapat mengakses gambar.")
            } else {
                showToast("Izin ditolak, aplikasi tidak dapat mengakses gambar.")
            }
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            startCrop(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_image.jpg"))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f) // You can change the aspect ratio
            .withMaxResultSize(500, 500) // Set maximum result size
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                currentImageUri = resultUri
                showImage()
            } else {
                showToast("Cropping failed!")
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast(cropError?.message ?: "Unknown cropping error.")
        }
    }


    private fun showImage() {
        currentImageUri?.let { uri ->
            Log.d("Image URI", "showImage: $uri")
            binding.previewImageView.setImageURI(uri)
        }
    }

    private fun analyzeImage(uri: Uri) {

        imageClassifierHelper.classifyStaticImage(uri)

    }


    private fun moveToResult(result: MutableList<Classifications>?) {
        val resultString = StringBuilder()
        var highestCategory: String? = null
        var highestConfidence = 0f

        result?.forEach { classification ->
            val itemCategory = classification.categories.maxByOrNull { it.score }
            itemCategory?.let { category ->
                resultString.append("Terindikasi: ${category.label}\nKeakuratan: ${category.score * 100}%\n")
                if (category.score > highestConfidence) {
                    highestConfidence = category.score
                    highestCategory = category.label
                }
            }
        }

        // simpan gambar ke bitmap
        currentImageUri?.let { uri ->

            @Suppress("DEPRECATION")
            val bitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }

            // simpan bitmap dan dapatkan imageuri
            bitmap?.let {
                val savedImageUriString = saveImage(it, applicationContext)

                // Insert database
                highestCategory?.let { category ->
                    val resultEntity = Result(
                        category = category,
                        confidence = highestConfidence,
                        imageUri = savedImageUriString
                    )
                    viewModel.insert(resultEntity)
                }
            } ?: run {
                showToast("Error loading image bitmap.")
            }
        }

        // intent ke result activity
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
            putExtra(ResultActivity.EXTRA_RESULT, resultString.toString())
        }
        startActivity(intent)
    }

    private fun saveImage(bitmap: Bitmap, context: Context): String {
        val imageFile = File(context.filesDir, "${System.currentTimeMillis()}.jpg") // buat nama unik imageFile
        FileOutputStream(imageFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return Uri.fromFile(imageFile).toString() //simpan dalam bentuk string
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 1001 // Kode permintaan izin yang unik
    }
}
