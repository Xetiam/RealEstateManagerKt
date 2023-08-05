package com.example.realestatemanager.ui.addestate

import com.example.realestatemanager.factory.ViewState

sealed class AddEstateState : ViewState<Nothing>() {
    object LoadingState : AddEstateState()
    object InitialState : AddEstateState()
    object WrongFormatAdress : AddEstateState()
    object WrongInputPrice : AddEstateState()
    object WrongInputSurface : AddEstateState()
}