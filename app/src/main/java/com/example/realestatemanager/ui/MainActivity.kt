package com.example.realestatemanager.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityMainBinding
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.ui.adapter.EstateItemAdapter
import com.example.realestatemanager.ui.addestate.AddEstateFragment


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(binding.estateRecycler.isVisible == true) {
                    finish()
                } else {
                    binding.estateRecycler.isVisible = true
                    binding.mainFrame.isVisible = false
                    viewModel.loadEstates(this@MainActivity)                }
            }
        })
        viewModel.viewState.observe(this) { state ->
            when (state) {
                is MainState.LoadingState -> showLoadingState()
                is MainState.WithEstatesState -> showEstatesState(state.estates)
                is MainState.WithoutEstateState -> showWithoutEstateState()
            }
        }

        viewModel.loadEstates(this)
    }

    private fun showLoadingState() {
        // Afficher l'état de chargement dans l'activité
    }

    private fun showEstatesState(estates: List<EstateModel>) {
        val adapter = EstateItemAdapter(estates)
        binding.estateRecycler.layoutManager = LinearLayoutManager(this)
        binding.estateRecycler.adapter = adapter
    }

    private fun showWithoutEstateState() {
        // Afficher l'état sans données d'Estate dans l'activité
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.estate_add -> {
                binding.estateRecycler.isVisible = false
                binding.mainFrame.isVisible = true
                supportFragmentManager.beginTransaction()
                    .replace(binding.mainFrame.id, AddEstateFragment())
                    .commit()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
}