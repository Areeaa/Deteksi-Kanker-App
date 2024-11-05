package com.dicoding.asclepius.view
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapter.HistoryAdapter
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.viewmodel.MainViewModel
import com.dicoding.asclepius.viewmodel.MainViewModelFactory
import android.Manifest
import android.util.Log
import android.widget.Toast

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: MainViewModel

    private companion object {
        private const val REQUEST_CODE_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Memeriksa izin untuk READ_EXTERNAL_STORAGE
        checkPermissions()

        // Inisialisasi ViewModel dengan custom factory
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe data dari ViewModel
        viewModel.getAllResult().observe(this) { results ->
            val adapter = HistoryAdapter(results)
            binding.recyclerView.adapter = adapter
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("HistoryActivity", "Permission granted")
            } else {
                Log.e("HistoryActivity", "Permission denied")
                Toast.makeText(this, "Permission to access media denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
