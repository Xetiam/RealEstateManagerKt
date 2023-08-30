package com.example.realestatemanager.ui.map

import com.example.realestatemanager.factory.ViewState
import com.example.realestatemanager.model.EstateModel
import com.google.android.gms.maps.model.LatLng

sealed class MapState: ViewState<List<Pair<EstateModel,LatLng?>>>() {
    data class WithEstatesState(val estates: List<Pair<EstateModel,LatLng?>>) : MapState()
    data class WithoutEstateState(val message: Int) : MapState()
    object LoadingState : MapState()
    object InitialState : MapState()
}