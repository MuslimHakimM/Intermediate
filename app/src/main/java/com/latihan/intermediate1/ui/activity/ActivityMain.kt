package com.latihan.intermediate1.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.latihan.intermediate1.R
import com.latihan.intermediate1.data.model.adapter.ListAdapter
import com.latihan.intermediate1.data.model.adapter.LoadingAdapter
import com.latihan.intermediate1.databinding.ActivityMainBinding
import com.latihan.intermediate1.ui.viewmodel.MainViewModel
import com.latihan.intermediate1.ui.viewmodel.ViewModelFactory

class ActivityMain : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ListAdapter
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Home"

        factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        adapter = ListAdapter()

        validate()
        action()
    }


    private fun setRecyclerView() {
        binding.apply {
            rvStory.layoutManager = LinearLayoutManager(this@ActivityMain)
            rvStory.setHasFixedSize(true)
            rvStory.adapter = adapter

        }
    }

    private fun setupViewModel() {
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingAdapter { adapter.retry() }
        )
        viewModel.getStories().observe(this@ActivityMain) {
            adapter.submitData(lifecycle, it)
        }
    }



    private fun validate() {
        viewModel.getAllData().observe(this) { user ->
            if (user.isLogin) {
                setRecyclerView()
                setupViewModel()
            } else {
                startActivity(Intent(this, ActivityLogin::class.java))
                finish()
            }
        }
    }

    private fun action() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, ActivityAddStory::class.java)
            startActivity(intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this@ActivityMain as Activity).toBundle()
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.menu_logout -> {

                val builder = AlertDialog.Builder(this)
                builder.setMessage("Apakah Anda Mau Keluar?")
                builder.setCancelable(false)
                builder.setPositiveButton("Yes") { dialog, which ->
                    viewModel.logout()
                    startActivity(Intent(this, ActivityMain::class.java))
                    finish()
                }
                builder.setNegativeButton("No") { dialog, which ->
                    dialog.cancel()
                }

                val alertDialog = builder.create()
                alertDialog.show()
                true
            }

            R.id.menu_maps -> {
                val intent = Intent(this, ActivityMaps::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> {return super.onOptionsItemSelected(item)}
        }
    }
}