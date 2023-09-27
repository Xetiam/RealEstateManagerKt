package com.example.realestatemanager.data.contentprovider

import android.content.ContentResolver
import android.database.MatrixCursor
import android.net.Uri
import com.example.realestatemanager.model.EstateInterestPoint
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.model.EstateStatus
import com.example.realestatemanager.model.EstateType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Date

@RunWith(RobolectricTestRunner::class)
class EstateContentProviderWrapperTest {

    private lateinit var contentResolver: ContentResolver
    private lateinit var wrapper: EstateContentProviderWrapper

    @Before
    fun setup() {
        contentResolver = mockk(relaxed = true)
        wrapper = EstateContentProviderWrapper(contentResolver)

        every {
            contentResolver.insert(
                Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates"),
                any()
            )
        } returns Uri.parse("content://com.example.realestatemanager/estates/1")

    }

    @Test
    fun `insertEstate inserts data correctly`() {
        val estate = getMockEstateList().first()

        wrapper.insertEstate(estate)

        verify {
            contentResolver.insert(
                Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates"),
                any()
            )
        }
    }

    @Test
    fun `getAllEstates retrieves all estates correctly`() {
        val columns = arrayOf(
            "_id",
            "type",
            "dollar_price",
            "surface",
            "rooms",
            "bathrooms",
            "bedrooms",
            "description",
            "pictures",
            "pictures_description",
            "address",
            "interest_points",
            "status",
            "start_date",
            "sell_date",
            "modify_date",
            "agent_name")
        val matrixCursor = MatrixCursor(columns)

        matrixCursor.addRow(arrayOf<Any>(1L, "Flat", 100000, 120, 3,2,1, "Beautiful flat in city center","https://example.com/image1.jpg","description1", "123 Street Name, City, Country", "school,shops", "To sale", Date().time, Date().time, Date().time,"John Doe"))
        matrixCursor.addRow(arrayOf<Any>(2L, "House", 300000, 300, 5,3,2, "Spacious house with garden","https://example.com/image2.jpg","description2", "456 Another Street, City, Country", "park,hospital", "Sold", Date().time,Date().time, Date().time, "Jane Smith"))

        every {
            contentResolver.query(
                Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates"),
                null,
                null,
                null,
                null
            )
        } returns matrixCursor

        val result = wrapper.getAllEstates()

        verify {
            contentResolver.query(
                Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates"),
                null,
                null,
                null,
                null
            )
        }
        assert(result.size == 2)
    }

    @Test
    fun `getAllEstateById retrieves estate correctly`() {
        val columns = arrayOf(
            "_id",
            "type",
            "dollar_price",
            "surface",
            "rooms",
            "bathrooms",
            "bedrooms",
            "description",
            "pictures",
            "pictures_description",
            "address",
            "interest_points",
            "status",
            "start_date",
            "sell_date",
            "modify_date",
            "agent_name")
        val matrixCursor = MatrixCursor(columns)

        matrixCursor.addRow(arrayOf<Any>(2L, "House", 300000, 300, 5,3,2, "Spacious house with garden","https://example.com/image2.jpg","description2", "456 Another Street, City, Country", "park,hospital", "Sold", Date().time,Date().time, Date().time, "Jane Smith"))

        every {
            contentResolver.query(
                Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates/2"),
                null,
                null,
                null,
                null
            )
        } returns matrixCursor

        val result = wrapper.getEstateById(2L)

        verify {
            contentResolver.query(
                Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates/2"),
                null,
                null,
                null,
                null
            )
        }
        assert(result.id == 2L)
    }

    @Test
    fun `updateEstate updates estate correctly`() {
        val estate = getMockEstateList().first()

        wrapper.updateEstate(estate)

        verify {
            contentResolver.update(
                Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates/1"),
                any(),
                null,
                null
            )
        }
    }

    private fun getMockEstateList(): List<EstateModel> {
        val mockUri1 = mockk<Uri>()
        every { mockUri1.toString() } returns "https://example.com/image1.jpg"
        val mockUri2 = mockk<Uri>()
        every { mockUri2.toString() } returns "https://example.com/image2.jpg"
        val mockUri3 = mockk<Uri>()
        every { mockUri3.toString() } returns "https://example.com/image3.jpg"
        val mockUri4 = mockk<Uri>()
        every { mockUri4.toString() } returns "https://example.com/image4.jpg"

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
        return fakeEstates
    }
}
