package com.example.realestatemanager.ui

import android.content.Context
import com.example.realestatemanager.R
import com.example.realestatemanager.data.EstateRepository
import com.example.realestatemanager.factory.ViewModelAbstract
import com.example.realestatemanager.model.EstateInterestPoint
import com.example.realestatemanager.model.EstateModel
import com.openclassrooms.realestatemanager.Utils
import java.util.Calendar
import java.util.Date

class MainViewModel : ViewModelAbstract<MainState>() {
    private var estateRepository: EstateRepository? = null
    private var estates: List<EstateModel> = ArrayList()
    private var displayedEstates: List<EstateModel> = ArrayList()
    private var filterText: String? = null

    fun loadEstates(context: Context) {
        if (displayedEstates.isEmpty()) {
            estateRepository = Utils.getEstateRepository(context)
            val fetchedEstate = estateRepository?.getAllEstates()?.let { ArrayList(it) }
            estates = fetchedEstate ?: ArrayList()
            if (estates.isEmpty()) {
                setState(
                    MainState.WithoutEstateState(
                        if (Utils.isInternetAvailable(context)) R.string.no_estate_found_with_internet
                        else R.string.no_estate_without_connexion
                    )
                )
            } else {
                setState(MainState.WithEstatesState(estates))
            }
        } else {
            setState(MainState.WithEstatesState(displayedEstates))
        }
    }

    override fun initUi() {
        setState(MainState.InitialState)
    }

    fun searchEstates(newText: String?) {
        filterText = newText
    }

    fun addSurfaceAndPriceCursor() {
        if (estates.isNotEmpty()) {
            val maxPrice = estates.maxOf { it.dollarPrice }
            val minPrice = estates.minOf { it.dollarPrice }
            val maxSurface = estates.maxOf { it.surface }
            val minSurface = estates.minOf { it.surface }
            if (maxPrice > minPrice && maxSurface > minSurface) {
                setState(MainState.SliderValuesState(minPrice, maxPrice, minSurface, maxSurface))
            }
        }
    }

    fun onSliderChanged(
        minPrice: Int,
        maxPrice: Int,
        minSurface: Int,
        maxSurface: Int,
        interestPoints: ArrayList<EstateInterestPoint>,
        isRecentlySold: Boolean
    ) {
        val filteredEstates = estates.filter { estate ->
            estate.dollarPrice in minPrice..maxPrice &&
                    estate.surface in minSurface..maxSurface &&
                    estate.interestPoints.containsAll(interestPoints) &&
                    if (!filterText.isNullOrEmpty()) {
                        estate.address.contains(filterText.toString(), true) ||
                                estate.type.toString().contains(filterText.toString(), true) ||
                                estate.interestPoints.any { interestPoint ->
                                    interestPoint.label.contains(
                                        filterText.toString(),
                                        true
                                    )
                                }

                    } else {
                        true
                    } &&
                    if (isRecentlySold) {
                        estate.sellDate != null && isDateWithinLastThreeMonths(estate.sellDate)
                    } else {
                        true
                    }
        }
        displayedEstates = filteredEstates
        setState(MainState.WithEstatesState(filteredEstates))
    }

    fun shouldShowDetailFragment(isTouchedTwoTimes: Boolean) {
        if (isTouchedTwoTimes) {
            setState(MainState.ShowDetailFragmentState)
        }
    }

    private fun isDateWithinLastThreeMonths(dateToCheck: Date): Boolean {
        val currentDate = Calendar.getInstance().time

        val calendar = Calendar.getInstance()
        calendar.time = dateToCheck

        calendar.add(Calendar.MONTH, 3)

        return currentDate.before(calendar.time)
    }
}