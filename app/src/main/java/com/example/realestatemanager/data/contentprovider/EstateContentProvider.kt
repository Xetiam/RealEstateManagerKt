package com.example.realestatemanager.data.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import androidx.annotation.RequiresApi

const val AUTHORITY = "com.example.realestatemanager"
const val PATH_ESTATES = "estates"

class EstateContentProvider : ContentProvider(){
    private lateinit var databaseHelper: SQLiteOpenHelper

    companion object {
        val BASE_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")
        private const val ESTATES = 1
        private const val ESTATE_ID = 2
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(AUTHORITY, PATH_ESTATES, ESTATES)
            uriMatcher.addURI(AUTHORITY, "$PATH_ESTATES/#", ESTATE_ID)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(): Boolean {
        databaseHelper = DatabaseHelper(requireContext())
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = databaseHelper.readableDatabase
        val cursor: Cursor?
        when (uriMatcher.match(uri)) {
            ESTATES -> cursor = db.query(
                EstateEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )

            ESTATE_ID -> {
                val id = ContentUris.parseId(uri)
                cursor = db.query(
                    EstateEntry.TABLE_NAME,
                    projection,
                    "${BaseColumns._ID} = ?",
                    arrayOf(id.toString()),
                    null,
                    null,
                    sortOrder
                )
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        cursor?.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val db = databaseHelper.writableDatabase
        val id: Long
        when (uriMatcher.match(uri)) {
            ESTATES -> {
                id = db.insert(EstateEntry.TABLE_NAME, null, values)
                if (id == -1L) {
                    throw RuntimeException("Failed to insert row into $uri")
                }
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return ContentUris.withAppendedId(uri, id)
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = databaseHelper.writableDatabase
        val rowsUpdated: Int
        when (uriMatcher.match(uri)) {
            ESTATES -> rowsUpdated =
                db.update(EstateEntry.TABLE_NAME, values, selection, selectionArgs)

            ESTATE_ID -> {
                val id = ContentUris.parseId(uri)
                rowsUpdated = db.update(
                    EstateEntry.TABLE_NAME,
                    values,
                    "${BaseColumns._ID} = ?",
                    arrayOf(id.toString())
                )
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (rowsUpdated != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = databaseHelper.writableDatabase
        val rowsDeleted: Int
        when (uriMatcher.match(uri)) {
            ESTATES -> rowsDeleted = db.delete(EstateEntry.TABLE_NAME, selection, selectionArgs)
            ESTATE_ID -> {
                val id = ContentUris.parseId(uri)
                rowsDeleted = db.delete(
                    EstateEntry.TABLE_NAME,
                    "${BaseColumns._ID} = ?",
                    arrayOf(id.toString())
                )
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (rowsDeleted != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    object EstateEntry : BaseColumns {
        const val TABLE_NAME = "estate_table"
        const val COLUMN_TYPE = "type"
        const val COLUMN_DOLLAR_PRICE = "dollar_price"
        const val COLUMN_SURFACE = "surface"
        const val COLUMN_ROOMS = "rooms"
        const val COLUMN_BATHROOMS = "bathrooms"
        const val COLUMN_BEDROOMS = "bedrooms"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_PICTURES = "pictures"
        const val COLUMN_PICTURES_DESCRIPTION = "pictures_description"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_INTEREST_POINTS = "interest_points"
        const val COLUMN_STATUS = "status"
        const val COLUMN_START_DATE = "start_date"
        const val COLUMN_SELL_DATE = "sell_date"
        const val COLUMN_MODIFY_DATE = "modify_date"
        const val COLUMN_AGENT_NAME = "agent_name"
    }

    private class DatabaseHelper(context: Context) : SQLiteOpenHelper(
        context,
        "estate_database",
        null,
        1
    ) {
        override fun onCreate(db: SQLiteDatabase) {
            val createTableQuery = "CREATE TABLE ${EstateEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${EstateEntry.COLUMN_TYPE} TEXT NOT NULL, " +
                    "${EstateEntry.COLUMN_DOLLAR_PRICE} INTEGER NOT NULL, " +
                    "${EstateEntry.COLUMN_SURFACE} INTEGER NOT NULL, " +
                    "${EstateEntry.COLUMN_ROOMS} INTEGER NOT NULL, " +
                    "${EstateEntry.COLUMN_BATHROOMS} INTEGER NOT NULL, " +
                    "${EstateEntry.COLUMN_BEDROOMS} INTEGER NOT NULL, " +
                    "${EstateEntry.COLUMN_DESCRIPTION} TEXT NOT NULL, " +
                    "${EstateEntry.COLUMN_PICTURES} TEXT NOT NULL, " +
                    "${EstateEntry.COLUMN_PICTURES_DESCRIPTION} TEXT NOT NULL, " +
                    "${EstateEntry.COLUMN_ADDRESS} TEXT NOT NULL, " +
                    "${EstateEntry.COLUMN_INTEREST_POINTS} TEXT NOT NULL, " +
                    "${EstateEntry.COLUMN_STATUS} TEXT NOT NULL, " +
                    "${EstateEntry.COLUMN_START_DATE} INTEGER NOT NULL, " +
                    "${EstateEntry.COLUMN_SELL_DATE} INTEGER, " +
                    "${EstateEntry.COLUMN_MODIFY_DATE} INTEGER, " +
                    "${EstateEntry.COLUMN_AGENT_NAME} TEXT NOT NULL" +
                    ");"
            db.execSQL(createTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // You can handle database upgrades here
        }
    }
/*
    @RequiresApi(Build.VERSION_CODES.R)
    override fun insertEstate(estate: EstateModel) {
        val contentValues = ContentValues().apply {
            put(EstateEntry.COLUMN_TYPE, estate.type.label)
            put(EstateEntry.COLUMN_DOLLAR_PRICE, estate.dollarPrice)
            put(EstateEntry.COLUMN_SURFACE, estate.surface)
            put(EstateEntry.COLUMN_ROOMS, estate.rooms.first)
            put(EstateEntry.COLUMN_BATHROOMS, estate.rooms.second)
            put(EstateEntry.COLUMN_BEDROOMS, estate.rooms.third)
            put(EstateEntry.COLUMN_DESCRIPTION, estate.description)
            put(EstateEntry.COLUMN_PICTURES, estate.pictures.joinToString(","))
            put(EstateEntry.COLUMN_ADDRESS, estate.address)
            put(EstateEntry.COLUMN_INTEREST_POINTS, estate.interestPoints.joinToString(","))
            put(EstateEntry.COLUMN_STATUS, estate.status)
            put(EstateEntry.COLUMN_START_DATE, estate.startDate.time)
            put(EstateEntry.COLUMN_SELL_DATE, estate.sellDate?.time)
            put(EstateEntry.COLUMN_MODIFY_DATE, estate.modifyDate?.time)
            put(EstateEntry.COLUMN_AGENT_NAME, estate.agentName)
        }

        requireContext().contentResolver.insert(BASE_CONTENT_URI.buildUpon().appendPath(PATH_ESTATES).build(), contentValues)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun getAllEstates(): List<EstateModel> {
        val estates = mutableListOf<EstateModel>()
        val cursor = requireContext().contentResolver.query(BASE_CONTENT_URI.buildUpon().appendPath(PATH_ESTATES).build(), null, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(BaseColumns._ID))
                val type = it.getString(it.getColumnIndexOrThrow(EstateEntry.COLUMN_TYPE))
                val dollarPrice = it.getInt(it.getColumnIndexOrThrow(EstateEntry.COLUMN_DOLLAR_PRICE))
                val surface = it.getInt(it.getColumnIndexOrThrow(EstateEntry.COLUMN_SURFACE))
                val rooms = it.getInt(it.getColumnIndexOrThrow(EstateEntry.COLUMN_ROOMS))
                val bathrooms = it.getInt(it.getColumnIndexOrThrow(EstateEntry.COLUMN_BATHROOMS))
                val bedrooms = it.getInt(it.getColumnIndexOrThrow(EstateEntry.COLUMN_BEDROOMS))
                val description = it.getString(it.getColumnIndexOrThrow(EstateEntry.COLUMN_DESCRIPTION))
                val picturesString = it.getString(it.getColumnIndexOrThrow(EstateEntry.COLUMN_PICTURES))
                val pictures = picturesString.split(",").map { uriString -> Uri.parse(uriString) }
                val address = it.getString(it.getColumnIndexOrThrow(EstateEntry.COLUMN_ADDRESS))
                val interestPointsString = it.getString(it.getColumnIndexOrThrow(EstateEntry.COLUMN_INTEREST_POINTS))
                val interestPoints = interestPointsString.split(",")
                val status = it.getString(it.getColumnIndexOrThrow(EstateEntry.COLUMN_STATUS))
                val startDate = Date(it.getLong(it.getColumnIndexOrThrow(EstateEntry.COLUMN_START_DATE)))
                val sellDate = Date(it.getLong(it.getColumnIndexOrThrow(EstateEntry.COLUMN_SELL_DATE)))
                val modifyDate = Date(it.getLong(it.getColumnIndexOrThrow(EstateEntry.COLUMN_MODIFY_DATE)))
                val agentName = it.getString(it.getColumnIndexOrThrow(EstateEntry.COLUMN_AGENT_NAME))

                val estate = EstateModel(
                    id,EstateType.fromLabel(type), dollarPrice, surface, Triple(rooms,bathrooms,bedrooms), description, pictures.toCollection(ArrayList()),
                    address, interestPoints as ArrayList<String>, status,
                    startDate, sellDate, modifyDate, agentName
                )

                estates.add(estate)
            }
        }

        return estates
    }*/
}
