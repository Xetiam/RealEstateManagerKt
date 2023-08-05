package com.example.realestatemanager.ui.addestate

import android.content.Context
import android.net.Uri
import com.example.realestatemanager.data.EstateRepository
import com.example.realestatemanager.factory.ViewModelAbstract
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.model.EstateType
import com.openclassrooms.realestatemanager.Utils
import java.util.Date

class AddEstateViewModel :
    ViewModelAbstract<AddEstateState>() {
    private var estateRepository: EstateRepository? = null
    override fun initialState(): AddEstateState {
        return AddEstateState.InitialState
    }

    fun createEstate(estateModel: EstateModel, context: Context) {
        estateRepository = Utils.getEstateRepository(context)
        estateRepository?.insertEstate(estateModel)
    }

    fun initiateCreation(
        type: EstateType,
        dollarPrice: String,
        surface: String,
        rooms: Triple<Int, Int, Int>,
        description: String,
        pictures: ArrayList<Uri>,
        address: String,
        context: Context
    ) {
        if(Utils.isAddressValid(address) &&
            !dollarPrice.isEmpty() &&
            !surface.isEmpty()){
            createEstate(
                EstateModel(
                    type = type,
                    dollarPrice = dollarPrice.toInt(),
                    surface = surface.toInt(),
                    rooms = rooms,
                    description = description,
                    pictures = pictures,
                    address = address,
                    interestPoints = arrayListOf(),
                    status = "To Sale",
                    startDate = Date(),
                    agentName = "Jason Momoa"
                ), context
            )
        } else {
            if(!Utils.isAddressValid(address)) { setState(AddEstateState.WrongFormatAdress) }
            if(dollarPrice.isEmpty()) { setState(AddEstateState.WrongInputPrice) }
            if(surface.isEmpty()) { setState(AddEstateState.WrongInputSurface) }
        }
    }
}