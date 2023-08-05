package com.example.realestatemanager.data.contentprovider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import com.example.realestatemanager.data.EstateRepository
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.model.EstateType
import java.util.Date

class EstateContentProviderWrapper(private val context: Context) : EstateRepository {

    override fun insertEstate(estate: EstateModel) {
        val contentResolver: ContentResolver = context.contentResolver
        val contentValues = getContentValuesFromEstate(estate)
        contentResolver.insert(
            Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates"),
            contentValues
        )
    }

    override fun getAllEstates(): List<EstateModel> {
        val contentResolver: ContentResolver = context.contentResolver
        val cursor = contentResolver.query(
            Uri.withAppendedPath(EstateContentProvider.BASE_CONTENT_URI, "estates"),
            null,
            null,
            null,
            null)
        return parseCursorToEstateList(cursor)
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
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_PICTURES, estate.pictures.joinToString(","))
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_ADDRESS, estate.address)
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_INTEREST_POINTS, estate.interestPoints.joinToString(","))
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_STATUS, estate.status)
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_START_DATE, estate.startDate.time)
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_SELL_DATE, estate.sellDate?.time)
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_MODIFY_DATE, estate.modifyDate?.time)
        contentValues.put(EstateContentProvider.EstateEntry.COLUMN_AGENT_NAME, estate.agentName)
        return contentValues
    }

    private fun parseCursorToEstateList(cursor: Cursor?): List<EstateModel> {
        val estates = mutableListOf<EstateModel>()
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(BaseColumns._ID))
                val type = it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_TYPE))
                val dollarPrice = it.getInt(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_DOLLAR_PRICE))
                val surface = it.getInt(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_SURFACE))
                val rooms = it.getInt(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_ROOMS))
                val bathrooms = it.getInt(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_BATHROOMS))
                val bedrooms = it.getInt(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_BEDROOMS))
                val description = it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_DESCRIPTION))
                val picturesString = it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_PICTURES))
                val pictures = picturesString.split(",").map { uriString -> Uri.parse(uriString) }
                val address = it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_ADDRESS))
                val interestPointsString = it.getString(it.getColumnIndexOrThrow(
                    EstateContentProvider.EstateEntry.COLUMN_INTEREST_POINTS
                ))
                val interestPoints = interestPointsString.split(",")
                val status = it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_STATUS))
                val startDate = Date(it.getLong(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_START_DATE)))
                val sellDate = Date(it.getLong(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_SELL_DATE)))
                val modifyDate = Date(it.getLong(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_MODIFY_DATE)))
                val agentName = it.getString(it.getColumnIndexOrThrow(EstateContentProvider.EstateEntry.COLUMN_AGENT_NAME))

                val estate = EstateModel(
                    id,
                    EstateType.fromLabel(type),
                    dollarPrice,
                    surface,
                    Triple(rooms, bathrooms, bedrooms),
                    description,
                    pictures.toCollection(ArrayList()),
                    address,
                    ArrayList(interestPoints),
                    status,
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