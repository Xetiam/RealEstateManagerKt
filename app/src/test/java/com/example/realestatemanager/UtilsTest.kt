package com.example.realestatemanager

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.realestatemanager.Utils.getLocationFromAdress
import com.google.android.gms.maps.model.LatLng
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.unmockkConstructor
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.pow

class UtilsTest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCapabilities: NetworkCapabilities
    private lateinit var geocoder: Geocoder


    @Before
    fun setUp() {
        context = mockk()
        connectivityManager = mockk()
        networkCapabilities = mockk()
        geocoder = mockk()
    }

    @Test
    fun testConvertDollarToEuro() {
        assert(Utils.convertDollarToEuro(1000) == 901)
    }

    @Test
    fun testConvertEuroToDollar() {
        assert(Utils.convertEuroToDollar(901) == 1000)
    }

    @Test
    fun testTodayDate() {
        val expectedDate = Utils.todayDate
        assert(expectedDate.isNotEmpty())
    }

    @Test
    fun testIsInternetAvailable() {
        setStaticFieldViaReflection(Build.VERSION::class.java.getDeclaredField("SDK_INT"), 22)

        every {
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
        } returns connectivityManager
        mockkConstructor(Socket::class)

        every {
            anyConstructed<Socket>().connect(InetSocketAddress("www.google.com", 80), 1500)
        } answers {}
        assert(Utils.isInternetAvailable(context))
    }

    @Test
    fun testIsInternetUnavailable() {
        setStaticFieldViaReflection(Build.VERSION::class.java.getDeclaredField("SDK_INT"), 22)
        every {
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
        } returns connectivityManager
        mockkConstructor(Socket::class)

        every {
            anyConstructed<Socket>().connect(InetSocketAddress("www.google.com", 80), 1500)
        } answers {
            throw IOException()
        }
        assertFalse(Utils.isInternetAvailable(context))
    }

    @Test
    fun testIsInternetAvailableAPI23() {
        setStaticFieldViaReflection(Build.VERSION::class.java.getDeclaredField("SDK_INT"), 23)
        every {
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
        } returns connectivityManager
        every {
            connectivityManager.getNetworkCapabilities(any())
        } returns networkCapabilities
        every {
            connectivityManager.activeNetwork
        } returns mockk()
        every {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } returns true
        assert(Utils.isInternetAvailable(context))
    }

    @Test
    fun testIsInternetUnavailableAPI23() {
        setStaticFieldViaReflection(Build.VERSION::class.java.getDeclaredField("SDK_INT"), 23)
        every {
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
        } returns connectivityManager
        every {
            connectivityManager.getNetworkCapabilities(any())
        } returns networkCapabilities
        every {
            networkCapabilities.hasTransport(any())
        } returns false
        every {
            connectivityManager.activeNetwork
        } returns mockk()
        assertFalse(Utils.isInternetAvailable(context))
    }

    @Test
    fun testIsAddressValid() {
        assert(Utils.isAddressValid("123 Main St, 12345 City, Country"))
    }

    @Test
    fun testExtractCityFromAddress() {
        assert(Utils.extractCityFromAddress("123 Main St, 12345 City, Country") == "City")
    }

    @Test
    fun testFormatPriceNumber() {
        assert(Utils.formatPriceNumber(1000000) == "1,000,000")
    }

    @Test
    fun testGetLocationFromAddress() {
        val mockAddress = mockk<Address>(relaxed = true)
        every { mockAddress.latitude } returns 37.4235243
        every { mockAddress.longitude } returns -122.0866564
        val testAddress = "1600 Amphitheatre Parkway, Mountain View, CA"
        val expectedLatLng = LatLng(37.4235243, -122.0866564)
        val mockContext = mock(Context::class.java)
        mockkConstructor(Geocoder::class)
        slot<(MutableList<Address>) -> Unit>()
        every {
            anyConstructed<Geocoder>().getFromLocationName(any(), any(), any())
        } answers {
            val lambda = args[2] as Geocoder.GeocodeListener
            lambda.onGeocode(mutableListOf(mockAddress))
        }
        every {
            anyConstructed<Geocoder>().getFromLocationName(
                any(),
                any()
            )
        } returns mutableListOf(mockAddress)
        var resultLatLng: LatLng? = null
        getLocationFromAdress(testAddress, mockContext) {
            resultLatLng = it
        }
        assertEquals(expectedLatLng, resultLatLng)
        unmockkConstructor(Geocoder::class)
    }

    @Test
    fun testGetLocationFromAddressInvalid() {
        mockk<Address>(relaxed = true)
        // Define test parameters
        val testAddress = "1600 Amphitheatre Parkway, CA"
        val expectedLatLng: LatLng? = null
        val mockContext = mock(Context::class.java)

        // Mock Geocoder constructor
        mockkConstructor(Geocoder::class)

        // Capture the lambda function passed into `getFromLocationName`
        slot<(MutableList<Address>) -> Unit>()

        // Mock `getFromLocationName` method
        every {
            anyConstructed<Geocoder>().getFromLocationName(any(), any(), any())
        } answers {
            val lambda = args[2] as Geocoder.GeocodeListener
            lambda.onGeocode(mutableListOf())
        }
        every {
            anyConstructed<Geocoder>().getFromLocationName(any(), any())
        } returns mutableListOf()
        // Invoke the method to test and capture its result using a callback
        var resultLatLng: LatLng? = null
        getLocationFromAdress(testAddress, mockContext) {
            resultLatLng = it
        }

        // Assert that the returned LatLng matches the expected LatLng
        assertEquals(expectedLatLng, resultLatLng)

        // Clear mock after test
        unmockkConstructor(Geocoder::class)
    }

    @Test
    fun testGetLocationFromAddressAPI33() {
        setStaticFieldViaReflection(Build.VERSION::class.java.getDeclaredField("SDK_INT"), 33)
        val mockAddress = mockk<Address>(relaxed = true)
        every { mockAddress.latitude } returns 37.4235243
        every { mockAddress.longitude } returns -122.0866564
        val testAddress = "1600 Amphitheatre Parkway, Mountain View, CA"
        val expectedLatLng = LatLng(37.4235243, -122.0866564)
        val mockContext = mock(Context::class.java)
        mockkConstructor(Geocoder::class)
        slot<(MutableList<Address>) -> Unit>()
        every {
            anyConstructed<Geocoder>().getFromLocationName(any(), any(), any())
        } answers {
            val lambda = args[2] as Geocoder.GeocodeListener
            lambda.onGeocode(mutableListOf(mockAddress))
        }
        every {
            anyConstructed<Geocoder>().getFromLocationName(
                any(),
                any()
            )
        } returns mutableListOf(mockAddress)
        var resultLatLng: LatLng? = null
        getLocationFromAdress(testAddress, mockContext) {
            resultLatLng = it
        }
        assertEquals(expectedLatLng, resultLatLng)
        unmockkConstructor(Geocoder::class)
    }

    @Test
    fun testGetLocationFromAddressInvalidAPI33() {
        setStaticFieldViaReflection(Build.VERSION::class.java.getDeclaredField("SDK_INT"), 33)
        mockk<Address>(relaxed = true)
        // Define test parameters
        val testAddress = "1600 Amphitheatre Parkway, CA"
        val expectedLatLng: LatLng? = null
        val mockContext = mock(Context::class.java)

        // Mock Geocoder constructor
        mockkConstructor(Geocoder::class)

        // Capture the lambda function passed into `getFromLocationName`
        slot<(MutableList<Address>) -> Unit>()

        // Mock `getFromLocationName` method
        every {
            anyConstructed<Geocoder>().getFromLocationName(any(), any(), any())
        } answers {
            val lambda = args[2] as Geocoder.GeocodeListener
            lambda.onGeocode(mutableListOf())
        }
        every {
            anyConstructed<Geocoder>().getFromLocationName(any(), any())
        } returns mutableListOf()
        // Invoke the method to test and capture its result using a callback
        var resultLatLng: LatLng? = null
        getLocationFromAdress(testAddress, mockContext) {
            resultLatLng = it
        }

        // Assert that the returned LatLng matches the expected LatLng
        assertEquals(expectedLatLng, resultLatLng)

        // Clear mock after test
        unmockkConstructor(Geocoder::class)
    }

    @Test
    fun testComputeDistanceBetweenTwoPoints() {
        val pointA = LatLng(0.0, 0.0)
        val pointB = LatLng(45.0, 45.0)

        val expectedDistance = calculateDistanceBetweenPoints(pointA, pointB)
        val actualDistance = Utils.computeDistanceBetweenTwoPoints(pointA, pointB)

        assertEquals(expectedDistance, actualDistance, 0.01)
    }

    private fun calculateDistanceBetweenPoints(pointA: LatLng, pointB: LatLng): Double {
        val earthRadiusKm = 6371.0

        val lat1 = pointA.latitude
        val lon1 = pointA.longitude
        val lat2 = pointB.latitude
        val lon2 = pointB.longitude

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a =
            Math.sin(dLat / 2).pow(2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2).pow(2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadiusKm * c
    }

    private fun setStaticFieldViaReflection(field: Field, value: Any) {
        field.isAccessible = true
        getModifiersField().also {
            it.isAccessible = true
            it.set(field, field.modifiers and Modifier.FINAL.inv())
        }
        field.set(null, value)
    }

    private fun getModifiersField(): Field {
        return try {
            Field::class.java.getDeclaredField("modifiers")
        } catch (e: NoSuchFieldException) {
            try {
                val getDeclaredFields0: Method =
                    Class::class.java.getDeclaredMethod(
                        "getDeclaredFields0",
                        Boolean::class.javaPrimitiveType
                    )
                getDeclaredFields0.isAccessible = true
                val fields = getDeclaredFields0.invoke(Field::class.java, false) as Array<Field>
                for (field in fields) {
                    if ("modifiers" == field.name) {
                        return field
                    }
                }
            } catch (ex: ReflectiveOperationException) {
                e.addSuppressed(ex)
            }
            throw e
        }
    }
}//TODO : test api à set
