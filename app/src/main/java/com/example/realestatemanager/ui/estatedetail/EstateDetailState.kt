package com.example.realestatemanager.ui.estatedetail

import com.example.realestatemanager.factory.ViewState
import com.example.realestatemanager.model.EstateModel

sealed class EstateDetailState: ViewState<EstateModel>() {
    object LoadingState : EstateDetailState()
    object InitialState : EstateDetailState()
    data class WithEstateState(val estate: EstateModel) : EstateDetailState()
    object WithoutEstateState : EstateDetailState()
}