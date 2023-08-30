package com.example.realestatemanager.ui.addestate

import com.example.realestatemanager.factory.ViewState
import com.example.realestatemanager.model.EstateModel

sealed class AddEstateState : ViewState<Nothing>() {
    data class EstateDataState(val estate: EstateModel) : AddEstateState()
    object LoadingState : AddEstateState()
    object InitialState : AddEstateState()
    object WrongFormatAdress : AddEstateState()
    object WrongInputPrice : AddEstateState()
    object WrongInputSurface : AddEstateState()
    object PictureDescriptionMissingState : AddEstateState()
    data class ToastMessageState(val message: Int) : AddEstateState()
}