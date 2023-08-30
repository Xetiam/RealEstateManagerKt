package com.example.realestatemanager.ui

import android.content.Context
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.realestatemanager.R
import com.example.realestatemanager.Utils
import com.example.realestatemanager.data.EstateRepository
import com.example.realestatemanager.model.EstateInterestPoint
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.model.EstateStatus
import com.example.realestatemanager.model.EstateType
import com.example.realestatemanager.ui.addestate.AddEstateState
import com.example.realestatemanager.ui.addestate.AddEstateViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.util.Date

class AddEstateViewModelTest {

    private lateinit var context: Context
    private lateinit var viewModel: AddEstateViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val mockEstateRepository: EstateRepository = Mockito.mock(EstateRepository::class.java)


    @Before
    fun setUp() {
        context = mockk()
        mockkObject(Utils)
        every { Utils.isInternetAvailable(context) } returns true
        every { Utils.getEstateRepository(context) } returns mockEstateRepository
        viewModel = AddEstateViewModel()
    }

    @Test
    fun createEstateWithSuccess() {
        val fakeEstatesToCreate = setGetMockEstateList()[0]
        val observedStates = mutableListOf<AddEstateState>()
        every {
            context.getSharedPreferences("UserPrefs", 0)
        } returns mockk {
            every { getString("user_name", "") } returns "John Doe"
            every { getString("user_currency", "USD") } returns "USD"
        }
        every { Utils.isAddressValid(fakeEstatesToCreate.address) } returns true
        viewModel.viewState.observeForever { observedStates.add(it) }
        viewModel.addDescriptionOrModify(fakeEstatesToCreate.pictures[0].second, 0)
        viewModel.addDescriptionOrModify(fakeEstatesToCreate.pictures[1].second, 1)
        viewModel.initiateCreation(
            fakeEstatesToCreate.type,
            fakeEstatesToCreate.dollarPrice.toString(),
            fakeEstatesToCreate.surface.toString(),
            fakeEstatesToCreate.rooms,
            fakeEstatesToCreate.description,
            fakeEstatesToCreate.pictures.map { it.first } as ArrayList<Uri>,
            fakeEstatesToCreate.address,
            fakeEstatesToCreate.interestPoints,
            context,
            false,
            null,
            false
        )
        assertEquals(
            listOf(AddEstateState.ToastMessageState(R.string.add_estate_creation_success)),
            observedStates
        )
    }
    @Test
    fun modifyEstateWithSuccess() {
        val fakeEstatesToCreate = setGetMockEstateList()[0]
        val observedStates = mutableListOf<AddEstateState>()
        every {
            context.getSharedPreferences("UserPrefs", 0)
        } returns mockk {
            every { getString("user_name", "") } returns "John Doe"
            every { getString("user_currency", "USD") } returns "USD"
        }
        every { Utils.isAddressValid(fakeEstatesToCreate.address) } returns true
        viewModel.viewState.observeForever { observedStates.add(it) }
        viewModel.addDescriptionOrModify(fakeEstatesToCreate.pictures[0].second, 0)
        viewModel.addDescriptionOrModify(fakeEstatesToCreate.pictures[1].second, 1)
        viewModel.initiateCreation(
            EstateType.PENTHOUSE,
            fakeEstatesToCreate.dollarPrice.toString(),
            fakeEstatesToCreate.surface.toString(),
            fakeEstatesToCreate.rooms,
            fakeEstatesToCreate.description,
            fakeEstatesToCreate.pictures.map { it.first } as ArrayList<Uri>,
            fakeEstatesToCreate.address,
            fakeEstatesToCreate.interestPoints,
            context,
            true,
            null,
            false
        )
        assertEquals(
            listOf(AddEstateState.ToastMessageState(R.string.add_estate_modification_success)),
            observedStates
        )
    }
    @Test
    fun getEstateInfo() {
        val fakeEstatesToCreate = setGetMockEstateList()[0]
        val observedStates = mutableListOf<AddEstateState>()
        every {
            context.getSharedPreferences("UserPrefs", 0)
        } returns mockk {
            every { getString("user_name", "") } returns "John Doe"
            every { getString("user_currency", "USD") } returns "USD"
        }
        every { Utils.isAddressValid(fakeEstatesToCreate.address) } returns true
        viewModel.viewState.observeForever { observedStates.add(it) }
        viewModel.getEstateData(1L,context)
        assertEquals(
            listOf(AddEstateState.EstateDataState(fakeEstatesToCreate)),
            observedStates
        )
    }

    @Test
    fun cannotCreateEstate() {
        val fakeEstatesToCreate = setGetMockEstateList()[0]
        val observedStates = mutableListOf<AddEstateState>()
        every {
            context.getSharedPreferences("UserPrefs", 0)
        } returns mockk {
            every { getString("user_name", "") } returns "John Doe"
            every { getString("user_currency", "USD") } returns "USD"
        }
        every { Utils.isAddressValid(fakeEstatesToCreate.address) } returns false
        viewModel.viewState.observeForever { observedStates.add(it) }
        viewModel.initiateCreation(
            fakeEstatesToCreate.type,
            "",
            "",
            fakeEstatesToCreate.rooms,
            fakeEstatesToCreate.description,
            fakeEstatesToCreate.pictures.map { it.first } as ArrayList<Uri>,
            fakeEstatesToCreate.address,
            fakeEstatesToCreate.interestPoints,
            context,
            false,
            null,
            false
        )
        assertEquals(
            listOf(
                AddEstateState.WrongFormatAdress,
                AddEstateState.WrongInputPrice,
                AddEstateState.WrongInputSurface,
                AddEstateState.PictureDescriptionMissingState
            ),
            observedStates
        )
    }

    @Test
    fun cannotCreateEstateWithoutAgentName() {
        val fakeEstatesToCreate = setGetMockEstateList()[0]
        val observedStates = mutableListOf<AddEstateState>()
        every {
            context.getSharedPreferences("UserPrefs", 0)
        } returns mockk {
            every { getString("user_name", "") } returns ""
            every { getString("user_currency", "USD") } returns "USD"
        }
        every { Utils.isAddressValid(fakeEstatesToCreate.address) } returns false
        viewModel.viewState.observeForever { observedStates.add(it) }
        viewModel.initiateCreation(
            fakeEstatesToCreate.type,
            "",
            "",
            fakeEstatesToCreate.rooms,
            fakeEstatesToCreate.description,
            fakeEstatesToCreate.pictures.map { it.first } as ArrayList<Uri>,
            fakeEstatesToCreate.address,
            fakeEstatesToCreate.interestPoints,
            context,
            false,
            null,
            false
        )
        assertEquals(
            listOf(
                AddEstateState.WrongFormatAdress,
                AddEstateState.WrongInputPrice,
                AddEstateState.WrongInputSurface,
                AddEstateState.PictureDescriptionMissingState,
                AddEstateState.ToastMessageState(R.string.add_estate_no_agent_name)
            ),
            observedStates
        )
    }


    private fun setGetMockEstateList(): List<EstateModel> {
        val mockUri1 = Mockito.mock(Uri::class.java)
        Mockito.`when`(mockUri1.toString()).thenReturn("https://example.com/image1.jpg")
        val mockUri2 = Mockito.mock(Uri::class.java)
        Mockito.`when`(mockUri2.toString()).thenReturn("https://example.com/image2.jpg")
        val mockUri3 = Mockito.mock(Uri::class.java)
        Mockito.`when`(mockUri3.toString()).thenReturn("https://example.com/image3.jpg")
        val mockUri4 = Mockito.mock(Uri::class.java)
        Mockito.`when`(mockUri4.toString()).thenReturn("https://example.com/image4.jpg")

        val fakeEstates = listOf(
            EstateModel(
                id = 1L,
                type = EstateType.FLAT,
                dollarPrice = 100000,
                surface = 120,
                rooms = Triple(3, 2, 1),
                description = "Beautiful flat in city center",
                pictures = arrayListOf(
                    Pair(mockUri1, "Living room"),
                    Pair(mockUri2, "Kitchen")
                ),
                address = "123 Street Name, City, Country",
                interestPoints = arrayListOf(
                    EstateInterestPoint.SCHOOL,
                    EstateInterestPoint.SHOPS
                ),
                status = EstateStatus.TO_SALE,
                startDate = Date(),
                agentName = "John Doe"
            ),
            EstateModel(
                id = 2L,
                type = EstateType.HOUSE,
                dollarPrice = 300000,
                surface = 300,
                rooms = Triple(5, 3, 2),
                description = "Spacious house with garden",
                pictures = arrayListOf(
                    Pair(mockUri3, "Garden"),
                    Pair(mockUri4, "Bedroom")
                ),
                address = "456 Another Street, City, Country",
                interestPoints = arrayListOf(
                    EstateInterestPoint.PARK,
                    EstateInterestPoint.HOSPITAL
                ),
                status = EstateStatus.SOLD,
                startDate = Date(),
                sellDate = Date(),
                agentName = "Jane Smith"
            )
        )
        Mockito.`when`(mockEstateRepository.getEstateById(1L)).thenReturn(fakeEstates[0])
        return fakeEstates
    }
}
