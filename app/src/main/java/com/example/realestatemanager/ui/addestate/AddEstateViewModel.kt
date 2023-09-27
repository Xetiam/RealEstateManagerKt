package com.example.realestatemanager.ui.addestate

import android.content.Context
import android.net.Uri
import com.example.realestatemanager.R
import com.example.realestatemanager.data.EstateRepository
import com.example.realestatemanager.factory.ViewModelAbstract
import com.example.realestatemanager.model.EstateInterestPoint
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.model.EstateStatus
import com.example.realestatemanager.model.EstateType
import com.example.realestatemanager.ui.settings.SettingsFragment
import com.example.realestatemanager.Utils
import java.util.Date

class AddEstateViewModel :
    ViewModelAbstract<AddEstateState>() {
    private var startDate: Date? = null
    private var estateRepository: EstateRepository? = null
    private var estatePictureDescriptions = mutableListOf<String>()

    private fun createEstate(estateModel: EstateModel, context: Context) {
        estateRepository = Utils.getEstateRepository(context)
        estateRepository?.insertEstate(estateModel)
    }

    private fun isDescriptionPictureComplete(
        pictures: ArrayList<Uri>,
        picturesWithDescription: List<Pair<Uri, String>>
    ): Boolean =
        (pictures.size == estatePictureDescriptions.size) && picturesWithDescription.any { it.second.isNotEmpty() }

    fun initiateCreation(
        type: EstateType,
        price: String,
        surface: String,
        rooms: Triple<Int, Int, Int>,
        description: String,
        pictures: ArrayList<Uri>,
        address: String,
        interestPoints: ArrayList<EstateInterestPoint>,
        context: Context,
        isModifying: Boolean,
        estateIdModifying: Long?,
        checked: Boolean
    ) {
        val sharedPrefs =
            context.getSharedPreferences(SettingsFragment.USER_PREFS, Context.MODE_PRIVATE)
        val userName = sharedPrefs.getString(SettingsFragment.USER_NAME, "") ?: ""
        val currency = sharedPrefs.getString(SettingsFragment.USER_CURRENCY, "USD")
        val finalPrice: String

        val picturesWithDescription = pictures.zip(estatePictureDescriptions)
        if (Utils.isAddressValid(address) &&
            price.isNotEmpty() &&
            surface.isNotEmpty() &&
            isDescriptionPictureComplete(pictures, picturesWithDescription) &&
            userName.isNotEmpty()
        ) {
            finalPrice = if(currency == "USD") {
                price
            } else {
                Utils.convertEuroToDollar(price.toInt()).toString()
            }
            if (isModifying) {
                estateRepository?.updateEstate(
                    EstateModel(
                        id = estateIdModifying,
                        type = type,
                        dollarPrice = finalPrice.toInt(),
                        surface = surface.toInt(),
                        rooms = rooms,
                        description = description,
                        pictures = ArrayList(picturesWithDescription),
                        address = address,
                        interestPoints = interestPoints,
                        status = if (checked) EstateStatus.SOLD else EstateStatus.TO_SALE,
                        startDate = startDate ?: Date(),
                        modifyDate = Date(),
                        sellDate = if (checked) Date() else null,
                        agentName = userName
                    )
                )
                setState(AddEstateState.ToastMessageState(R.string.add_estate_modification_success))
            } else {
                createEstate(
                    EstateModel(
                        type = type,
                        dollarPrice = finalPrice.toInt(),
                        surface = surface.toInt(),
                        rooms = rooms,
                        description = description,
                        pictures = ArrayList(picturesWithDescription),
                        address = address,
                        interestPoints = interestPoints,
                        status = EstateStatus.TO_SALE,
                        startDate = Date(),
                        agentName = userName
                    ), context
                )
                setState(AddEstateState.ToastMessageState(R.string.add_estate_creation_success))
            }
        } else {
            if (!Utils.isAddressValid(address)) {
                setState(AddEstateState.WrongFormatAdress)
            }
            if (price.isEmpty()) {
                setState(AddEstateState.WrongInputPrice)
            }
            if (surface.isEmpty()) {
                setState(AddEstateState.WrongInputSurface)
            }
            if (!isDescriptionPictureComplete(pictures, picturesWithDescription)) {
                setState(AddEstateState.PictureDescriptionMissingState)
            }
            if(userName.isEmpty()) {
                setState(AddEstateState.ToastMessageState(R.string.add_estate_no_agent_name))
            }
        }
    }

    fun addDescriptionOrModify(description: String, position: Int) {
        if (position > estatePictureDescriptions.size - 1) {
            estatePictureDescriptions.add(position, description)
        } else {
            estatePictureDescriptions[position] = description
        }
    }

    override fun initUi() {
        setState(AddEstateState.InitialState)
    }

    fun getEstateData(estateId: Long?, context: Context) {
        if (estateRepository == null) {
            estateRepository = Utils.getEstateRepository(context)
        }
        if (estateId != null && estateId != 0L) {
            estateRepository?.getEstateById(estateId)?.let { model ->
                startDate = model.startDate
                estatePictureDescriptions = model.pictures.map { it.second }.toMutableList()
                setState(AddEstateState.EstateDataState(model))
            }
        }
    }
}