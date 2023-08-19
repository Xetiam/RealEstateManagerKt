package com.example.realestatemanager.ui

import EstateItemAdapter
import NoEstateItemAdapter
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityMainBinding
import com.example.realestatemanager.model.EstateInterestPoint
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.ui.addestate.AddEstateFragment
import com.example.realestatemanager.ui.estatedetail.EstateDetailFragment
import com.google.android.material.slider.RangeSlider


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var estateIdCursor: Long? = null
    private lateinit var searchView: SearchView
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
                is MainState.WithoutEstateState -> showWithoutEstateState(state.message)
                is MainState.SliderValuesState -> showSliderValuesState(
                    state.minPrice,
                    state.maxPrice,
                    state.minSurface,
                    state.maxSurface
                )

                is MainState.ShowDetailFragmentState -> showDetailFragmentState()
            }
        }
    }

    private fun showDetailFragmentState() {
        val fragment = EstateDetailFragment()
        fragment.arguments = Bundle().apply {
            estateIdCursor?.let { putLong(ARG_ESTATE_ID, it) }
        }
        mainFragmentLauncher(fragment)
    }

    private fun showInitialState() {
        checkPermission()
        setCallBackListener()
        EstateInterestPoint.values().forEach { option ->
            val checkBox = CheckBox(this)
            checkBox.text = option.label
            checkBox.isChecked = false
            binding.interestPoints.addView(checkBox)
        }
    }

    private fun showSliderValuesState(
        minPrice: Int,
        maxPrice: Int,
        minSurface: Int,
        maxSurface: Int
    ) {
        binding.priceSlider.isVisible = true
        binding.surfaceSlider.isVisible = true
        binding.interestPoints.isVisible = true
        binding.sellDate.isVisible = true
        var selectedPriceMin = minPrice
        var selectedPriceMax = maxPrice
        var selectedSurfaceMin = minSurface
        var selectedSurfaceMax = maxSurface
        binding.interestPoints.children.forEach { view ->
            val checkBox = view as CheckBox
            checkBox.isChecked = false
        }
        setSlider(binding.priceSlider, minPrice, maxPrice) { pair ->
            selectedPriceMin = pair.first
            selectedPriceMax = pair.second
        }
        setSlider(binding.surfaceSlider, minSurface, maxSurface) { pair ->
            selectedSurfaceMin = pair.first
            selectedSurfaceMax = pair.second
        }
        binding.searchButton.apply {
            isVisible = true
            setOnClickListener {
                invalidateOptionsMenu()
                viewModel.onSliderChanged(
                    selectedPriceMin,
                    selectedPriceMax,
                    selectedSurfaceMin,
                    selectedSurfaceMax,
                    ArrayList(
                        binding.interestPoints.children.filterIsInstance<CheckBox>()
                            .filter { it.isChecked }
                            .map { EstateInterestPoint.fromLabel(it.text.toString()) }.toList()
                    ),
                    binding.sellDate.isChecked
                )
                isVisible = false
                binding.priceSlider.isVisible = false
                binding.surfaceSlider.isVisible = false
                binding.interestPoints.isVisible = false
                binding.sellDate.isVisible = false
            }
        }
    }

    private fun setSlider(
        slider: RangeSlider,
        min: Int,
        max: Int,
        callback: (Pair<Int, Int>) -> Unit
    ) {
        slider.apply {
            isVisible = true
            valueFrom = min.toFloat()
            valueTo = max.toFloat()
            setValues(min.toFloat(), max.toFloat())
            addOnChangeListener { _, _, _ ->
                callback(Pair(values[0].toInt(), values[1].toInt()))
            }
        }
    }

    private fun setCallBackListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!binding.slidingPaneLayout.isOpen) {
                    finish()
                } else {
                    binding.slidingPaneLayout.closePane()
                }
            }
        })
    }

    private fun showLoadingState() {
        // Afficher l'état de chargement dans l'activité
    }

    private fun showEstatesState(estates: List<EstateModel>) {
        val adapter = EstateItemAdapter(estateIdCursor) {
            viewModel.shouldShowDetailFragment(estateIdCursor == it)
            estateIdCursor = it
            viewModel.loadEstates(this)
        }
        adapter.submitList(estates)
        binding.estateRecycler.layoutManager = LinearLayoutManager(this)
        binding.estateRecycler.adapter = adapter
    }

    private fun showWithoutEstateState(message: Int) {
        val adapter = NoEstateItemAdapter(message)
        binding.estateRecycler.layoutManager = LinearLayoutManager(this)
        binding.estateRecycler.adapter = adapter
    }

    private fun checkPermission() {
        var permission = READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = READ_MEDIA_IMAGES
        }
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
                mainFragmentLauncher(AddEstateFragment())
                true
            }

            R.id.estate_modify -> {
                val fragment = AddEstateFragment()
                fragment.arguments = Bundle().apply {
                    estateIdCursor?.let { putLong(ARG_ESTATE_ID, it) }
                }
                mainFragmentLauncher(fragment)
                true
            }

            R.id.estate_search -> {
                viewModel.addSurfaceAndPriceCursor()
                searchView = item.actionView as SearchView
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.menu, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    private fun mainFragmentLauncher(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainFrame.id, fragment)
            .commit()
        binding.slidingPaneLayout.openPane()
    }

    companion object {
        const val ARG_ESTATE_ID = "estateId"
    }
}