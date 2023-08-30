package com.example.realestatemanager.data.contentprovider

import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import com.example.realestatemanager.data.EstateRepository
import com.example.realestatemanager.model.EstateInterestPoint
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.model.EstateStatus
import com.example.realestatemanager.model.EstateType
import java.util.Date

class EstateContentProviderWrapper(private val contentResolver: ContentResolver) :
    EstateRepository {

    override fun insertEstate(estate: EstateModel) {
        val contentValues = getContentValuesFromEstate(estate)
        contentResolver.insert(
            Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates"),
            contentValues
        )
    }

    override fun getAllEstates(): List<EstateModel> {
        val cursor = contentResolver.query(
            Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates"),
            null,
            null,
            null,
            null
        )
        return parseCursorToEstateList(cursor)
    }

    override fun getEstateById(estateId: Long): EstateModel {
        val cursor = contentResolver.query(
            Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates/$estateId"),
            null,
            null,
            null,
            null
        )
        return parseCursorToEstateList(cursor).first()
    }

    override fun updateEstate(estate: EstateModel) {
        val contentValues = getContentValuesFromEstate(estate)
        contentResolver.update(
            Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates/${estate.id}"),
            contentValues,
            null,
            null
        )
    }

    private fun getContentValuesFromEstate(estate: EstateModel): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_TYPE, estate.type.label)
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_DOLLAR_PRICE, estate.dollarPrice)
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_SURFACE, estate.surface)
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_ROOMS, estate.rooms.first)
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_BATHROOMS, estate.rooms.second)
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_BEDROOMS, estate.rooms.third)
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_DESCRIPTION, estate.description)
        contentValues.put(
            EstateContentProvider.EstateEntry.COLUMN_PICTURES,
            (estate.pictures.map { it.first }).joinToString(",")
        )
        contentValues.put(
            EstateContentProvider.EstateEntry.COLUMN_PICTURES_DESCRIPTION,
            (estate.pictures.map { it.second }).joinToString(",")
        )
        contentValues.put(
            EstateContentProvider.EstateEntry.COLUMN_PICTURES_DESCRIPTION,
            (estate.pictures.map { it.second }).joinToString(",")
        )

        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_ADDRESS, estate.address)
        contentValues.put(
            EstateContentProvider.EstateEntry.COLUMN_INTEREST_POINTS,
            estate.interestPoints.map { it.label }.joinToString(",")
        )
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_STATUS, estate.status.label)
        contentValues.put(
            EstateContentProvider.EstateEntry.COLUMN_START_DATE,
            estate.startDate.time
        )
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_SELL_DATE, estate.sellDate?.time)
        contentValues.put(
            EstateContentProvider.EstateEntry.COLUMN_MODIFY_DATE,
            estate.modifyDate?.time
        )
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_AGENT_NAME, estate.agentName)
        return contentValues
    }

    private fun parseCursorToEstateList(cursor: Cursor?): List<EstateModel> {
        val estates = mutableListOf<EstateModel>()
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(BaseColumns._ID))
                val type =
                    it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_TYPE))
                val dollarPrice =
                    it.getInt(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_DOLLAR_PRICE))
                val surface =
                    it.getInt(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_SURFACE))
                val rooms =
                    it.getInt(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_ROOMS))
                val bathrooms =
                    it.getInt(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_BATHROOMS))
                val bedrooms =
                    it.getInt(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_BEDROOMS))
                val description =
                    it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_DESCRIPTION))
                val picturesString =
                    it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_PICTURES))
                val pictures = picturesString.split(",").map { uriString -> Uri.parse(uriString) }
                val picturesDescriptionString =
                    it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_PICTURES_DESCRIPTION))
                val picturesDescription = picturesDescriptionString.split(",")
                val address =
                    it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_ADDRESS))
                val interestPointsString = it.getString(
                    it.getColumnIndexOrThrow(
                        EstateContentProvider.EstateEntry.COLUMN_INTEREST_POINTS
                    )
                )
                val interestPoints = interestPointsString.split(",").map { interestPoint ->
                    EstateInterestPoint.fromLabel(
                        interestPoint
                    )
                }
                val status =
                    it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_STATUS))
                val startDate =
                    Date(it.getLong(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_START_DATE)))
                var sellDate: Date? = null
                if (Date(it.getLong(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_SELL_DATE))) != Date(
                        0
                    )
                ) {
                    sellDate =
                        Date(it.getLong(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_SELL_DATE)))
                }
                var modifyDate: Date? = null
                if (Date(it.getLong(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_MODIFY_DATE))) != Date(
                        0
                    )
                ) {
                    modifyDate =
                        Date(it.getLong(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_MODIFY_DATE)))
                }
                val agentName =
                    it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_AGENT_NAME))

                val estate = EstateModel(
                    id,
                    EstateType.fromLabel(type),
                    dollarPrice,
                    surface,
                    Triple(rooms, bathrooms, bedrooms),
                    description,
                    ArrayList(pictures.zip(picturesDescription)),
                    address,
                    ArrayList(interestPoints),
                    EstateStatus.fromLabel(status),
                    startDate,
                    sellDate,
                    modifyDate,
                    agentName
                )
                estates.add(estate)
            }
        }
        return estates
    }
}
