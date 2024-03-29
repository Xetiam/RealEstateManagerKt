package com.example.realestatemanager.ui.map

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
import com.google.android.gms.maps.model.LatLng
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.invoke
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.util.Date

class MapViewModelTest {

    private lateinit var context: Context
    private lateinit var viewModel: MapViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val mockEstateRepository: EstateRepository = Mockito.mock(EstateRepository::class.java)


    @Before
    fun setUp() {
        context = mockk()
        mockkObject(Utils)
        every { Utils.isInternetAvailable(context) } returns true
        every { Utils.getEstateRepository(context) } returns mockEstateRepository
        viewModel = MapViewModel()
    }

    @Test
    fun testLoadEstates() {
        val fakeEstates = setGetMockEstateList()
        val observedStates = mutableListOf<MapState>()
        val estatesPosExpected: MutableList<LatLng?> = mutableListOf()
        estatesPosExpected.add(LatLng(0.0, 0.0))

        viewModel.viewState.observeForever { observedStates.add(it) }

        every {
            Utils.getLocationFromAdress("123 Street Name, City, Country", context, captureLambda())
        } answers {
            lambda<(LatLng?) -> Unit>().invoke(LatLng(0.0, 0.0))
        }

        every {
            Utils.getLocationFromAdress(
                "456 Another Street, City, Country",
                context,
                captureLambda()
            )
        } answers {
            lambda<(LatLng?) -> Unit>().invoke(LatLng(10.0, 10.0))
        }

        viewModel.loadNearEstates(LatLng(0.0, 0.0), context)

        assertEquals(
            listOf(
                MapState.LoadingState,
                MapState.WithEstatesState(listOf(fakeEstates[0]).zip(estatesPosExpected))
            ),
            observedStates
        )
    }

    @Test
    fun testLoadNoEstatesInternet() {
        val observedStates = mutableListOf<MapState>()
        viewModel.viewState.observeForever { observedStates.add(it) }
        every { Utils.isInternetAvailable(context) } returns true
        viewModel.loadNearEstates(LatLng(0.0, 0.0), context)
        assertEquals(
            listOf(
                MapState.LoadingState,
                MapState.WithoutEstateState(R.string.no_estate_found_with_internet)
            ),
            observedStates
        )
    }

    @Test
    fun testLoadNoEstatesNoInternet() {
        val observedStates = mutableListOf<MapState>()
        viewModel.viewState.observeForever { observedStates.add(it) }
        every { Utils.isInternetAvailable(context) } returns false

        viewModel.loadNearEstates(LatLng(0.0, 0.0), context)
        assertEquals(
            listOf(
                MapState.LoadingState,
                MapState.WithoutEstateState(R.string.no_estate_without_connexion)
            ),
            observedStates
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
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
        Mockito.`when`(mockEstateRepository.getAllEstates()).thenReturn(fakeEstates)
        return fakeEstates
    }
}