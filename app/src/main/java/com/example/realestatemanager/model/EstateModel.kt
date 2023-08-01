package com.example.realestatemanager.model

import android.graphics.Bitmap
import java.util.Date

data class EstateModel(
    private val type: String,
    private val dollarPrice: Int,
    private val surface: Int,
    private val rooms: Int,
    private val description: String,
    private val pictures: ArrayList<Bitmap>,    //Ou URI interne
    private val address: String,
    private val interestPoints: ArrayList<String>,
    private val status: String, private val startDate: Date,
    private val sellDate: Date, private val agentName: String
)