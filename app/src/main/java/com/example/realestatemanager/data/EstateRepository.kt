package com.example.realestatemanager.data

import com.example.realestatemanager.model.EstateModel

interface EstateRepository {
    fun insertEstate(estate: EstateModel)
    fun getAllEstates(): List<EstateModel>
    fun getEstateById(estateId: Long): EstateModel
    fun updateEstate(estate: EstateModel)
}
