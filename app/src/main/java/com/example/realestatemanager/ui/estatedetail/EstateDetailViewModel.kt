package com.example.realestatemanager.ui.estatedetail

import android.content.Context
import com.example.realestatemanager.data.EstateRepository
import com.example.realestatemanager.factory.ViewModelAbstract

class EstateDetailViewModel : ViewModelAbstract<EstateDetailState>() {
    private var estateRepository: EstateRepository? = null
    override fun initUi() {
        setState(EstateDetailState.InitialState)
    }

    fun getEstateDetail(estateId: Long, context: Context) {
        estateRepository = com.example.realestatemanager.Utils.getEstateRepository(context)
        val estate = estateRepository?.getEstateById(estateId)
        if (estate != null) {
            setState(EstateDetailState.WithEstateState(estate))
        }
    }
}