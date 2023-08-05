package com.example.realestatemanager.ui

import android.content.Context
import com.example.realestatemanager.data.EstateRepository
import com.example.realestatemanager.factory.ViewModelAbstract
import com.openclassrooms.realestatemanager.Utils

class MainViewModel : ViewModelAbstract<MainState>() {
    private var estateRepository: EstateRepository? = null
    fun loadEstates(context: Context) {
        setState(MainState.LoadingState)
        estateRepository = Utils.getEstateRepository(context)
        val estates = estateRepository?.getAllEstates()
        if (estates.isNullOrEmpty()) {
            setState(MainState.WithoutEstateState)
        } else {
            setState(MainState.WithEstatesState(estates))
        }
    }


    override fun initialState(): MainState {
        return MainState.LoadingState
    }
}