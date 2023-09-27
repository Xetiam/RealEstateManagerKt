package com.example.realestatemanager.ui.map

import android.content.Context
import com.example.realestatemanager.R
import com.example.realestatemanager.data.EstateRepository
import com.example.realestatemanager.factory.ViewModelAbstract
import com.example.realestatemanager.model.EstateModel
import com.google.android.gms.maps.model.LatLng
import com.example.realestatemanager.Utils

class MapViewModel : ViewModelAbstract<MapState>() {
    private var estateRepository: EstateRepository? = null

    override fun initUi() {
        setState(MapState.InitialState)
    }

    fun loadNearEstates(userLatLng: LatLng, context: Context) {
        estateRepository = Utils.getEstateRepository(context)
        val fetchedEstate = estateRepository?.getAllEstates()?.let { ArrayList(it) }
        val estates = fetchedEstate ?: ArrayList()
        setState(MapState.LoadingState)
        if (estates.isEmpty()) {
            setState(
                MapState.WithoutEstateState(
                    if (Utils.isInternetAvailable(context)) R.string.no_estate_found_with_internet
                    else R.string.no_estate_without_connexion
                )
            )
        } else {
            buildPos(estates, context) { listPos: ArrayList<LatLng?> ->
                val indexes = mutableListOf<Int>()
                val itemsToKeep = listPos.filter {
                    if((it?.let { estateLatLng ->
                            Utils.computeDistanceBetweenTwoPoints(userLatLng, estateLatLng) }
                            ?: 101.0) <= 100.0){
                        indexes.add(listPos.indexOf(it))
                        true
                    } else {
                        false
                    }
                }
                val estatesToKeep = mutableListOf<EstateModel>()
                indexes.forEach {
                    estatesToKeep.add(estates[it])
                }
                postState(MapState.WithEstatesState(estatesToKeep.zip(itemsToKeep)))
            }
        }
    }

    private fun buildPos(estates: java.util.ArrayList<EstateModel>,context: Context, callback: (ArrayList<LatLng?>) -> Unit) {
        val estatesPos: MutableList<LatLng?> = mutableListOf()
        estates.forEach {
            Utils.getLocationFromAdress(it.address,context) { latLng: LatLng? ->
                estatesPos.add(latLng)
                if(estatesPos.size == estates.size) {
                    callback(estatesPos as ArrayList<LatLng?>)
                }
            }
        }
    }
}