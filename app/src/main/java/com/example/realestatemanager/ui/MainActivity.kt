package com.example.realestatemanager.ui

import EstateItemAdapter
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityMainBinding
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.ui.addestate.AddEstateFragment


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
        viewModel.loadEstates(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.initUi()
        viewModel.viewState.observe(this) { state ->
            when (state) {
                is MainState.InitialState -> showInitialState()
                is MainState.LoadingState -> showLoadingState()
                is MainState.WithEstatesState -> showEstatesState(state.estates)
                is MainState.WithoutEstateState -> showWithoutEstateState()
                is MainState.SliderValuesState -> showSliderValuesState(
                    state.minPrice,
                    state.maxPrice,
                    state.minSurface,
                    state.maxSurface
                )
            }
        }
    }

    private fun showSliderValuesState(
        minPrice: Int,
        maxPrice: Int,
        minSurface: Int,
        maxSurface: Int
    ) {
        var selectedPriceMin = minPrice
        var selectedPriceMax = maxPrice
        var selectedSurfaceMin = minSurface
        var selectedSurfaceMax = maxSurface
        binding.priceSlider.apply {

            isVisible = true
            valueFrom = minPrice.toFloat()
            valueTo = maxPrice.toFloat()
            setValues(minPrice.toFloat(), maxPrice.toFloat())
            addOnChangeListener { _, _, _ ->
                selectedPriceMin = values[0].toInt()
                selectedPriceMax = values[1].toInt()
            }
        }
        binding.surfaceSlider.apply {
            isVisible = true
            valueFrom = minSurface.toFloat()
            valueTo = maxSurface.toFloat()
            setValues(minSurface.toFloat(), maxSurface.toFloat())
            addOnChangeListener { _, _, _ ->
                selectedSurfaceMin = values[0].toInt()
                selectedSurfaceMax = values[1].toInt()
            }
        }
        binding.searchButton.apply {
            isVisible = true
            setOnClickListener {
                viewModel.onSliderChanged(
                    selectedPriceMin,
                    selectedPriceMax,
                    selectedSurfaceMin,
                    selectedSurfaceMax
                )
                isVisible = false
                binding.priceSlider.isVisible = false
                binding.surfaceSlider.isVisible = false
            }
        }
    }

    private fun showInitialState() {
        checkPermission()
        setCallBackListener()
    }

    private fun setCallBackListener() {
        supportFragmentManager.setFragmentResultListener(
            "yourFragmentDestroyedKey",
            this
        ) { _, _ ->
            binding.estateRecycler.isVisible = true
            binding.mainFrame.isVisible = false
            viewModel.loadEstates(this@MainActivity)
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.estateRecycler.isVisible == true) {
                    finish()
                } else {
                    binding.estateRecycler.isVisible = true
                    binding.mainFrame.isVisible = false
                    viewModel.loadEstates(this@MainActivity)
                }
            }
        })
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
        binding.estateRecycler.isVisible = false
        binding.mainFrame.isVisible = false
        binding.noEstateMessage.isVisible = true
    }

    private fun checkPermission() {
        val permission = ACTION_OPEN_DOCUMENT
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.loadEstates(this)
        } else {
            requestPermissionLauncher.launch(permission)
        }
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
                binding.noEstateMessage.isVisible = false
                supportFragmentManager.beginTransaction()
                    .replace(binding.mainFrame.id, AddEstateFragment())
                    .commit()
                true
            }

            R.id.estate_modify -> {

                true
            }

            R.id.estate_search -> {
                viewModel.addSurfaceAndPriceCursor()
                val searchView = item.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.searchEstates(newText)
                        return false
                    }
                })
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
}