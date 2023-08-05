package com.example.realestatemanager.ui

import com.example.realestatemanager.factory.ViewState
import com.example.realestatemanager.model.EstateModel

sealed class MainState : ViewState<List<EstateModel>>(){
    data class WithEstatesState(val estates: List<EstateModel>) : MainState()
    object WithoutEstateState : MainState()
    object LoadingState : MainState()
}