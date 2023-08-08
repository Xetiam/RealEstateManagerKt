package com.example.realestatemanager.ui

import android.content.Context
import com.example.realestatemanager.data.EstateRepository
import com.example.realestatemanager.factory.ViewModelAbstract
import com.example.realestatemanager.model.EstateModel
import com.openclassrooms.realestatemanager.Utils

class MainViewModel : ViewModelAbstract<MainState>() {
    private var estateRepository: EstateRepository? = null
    private var estates: List<EstateModel> = ArrayList()

    fun loadEstates(context: Context) {
        estateRepository = Utils.getEstateRepository(context)
        estates = ArrayList(estateRepository?.getAllEstates())
        if (estates.isNullOrEmpty()) {
            setState(MainState.WithoutEstateState)
        } else {
            setState(MainState.WithEstatesState(estates))
        }
    }

    override fun initUi() {
        setState(MainState.InitialState)
    }

    fun searchEstates(newText: String?) {
        val filteredEstates = estates.filter {
            it.address.contains(newText.toString(), true) ||
                    it.type.toString().contains(newText.toString(), true) ||
                    it.interestPoints.any { interestPoint ->
                        interestPoint.contains(
                            newText.toString(),
                            true
                        )
                    }
        }
        setState(MainState.WithEstatesState(filteredEstates))
    }

    fun addSurfaceAndPriceCursor() {
        if(estates.isNotEmpty()) {
            val maxPrice = estates.maxOf { it.dollarPrice }
            val minPrice = estates.minOf { it.dollarPrice }
            val maxSurface = estates.maxOf { it.surface }
            val minSurface = estates.minOf { it.surface }
            if(maxPrice > minPrice && maxSurface > minSurface) {
                setState(MainState.SliderValuesState(minPrice, maxPrice, minSurface, maxSurface))
            }
        }
    }

    fun onSliderChanged(minPrice: Int, maxPrice: Int, minSurface: Int, maxSurface: Int) {
        val filteredEstates = estates.filter {
            it.dollarPrice in minPrice..maxPrice &&
                    it.surface in minSurface..maxSurface
        }
        setState(MainState.WithEstatesState(filteredEstates))
    }
}