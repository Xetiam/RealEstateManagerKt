package com.example.realestatemanager.model

import android.net.Uri
import java.util.Date

data class EstateModel(
    val id: Long? = null,
    val type: EstateType,//done
    val dollarPrice: Int,//done
    val surface: Int,//done
    val rooms: Triple<Int,Int,Int>,//done
    val description: String,//done
    val pictures: ArrayList<Uri>,//done
    val address: String,//done
    val interestPoints: ArrayList<String>,//TODO
    val status: String,//TODO
    val startDate: Date,//auto
    val sellDate: Date? = null,//TODO
    val modifyDate: Date? = null,//auto
    val agentName: String//auto
)
enum class EstateType(val label: String) {
    FLAT("Flat"),
    HOUSE("House"),
    DUPLEX("Duplex"),
    PENTHOUSE("Penthouse");
    companion object {
        private val map = values().associateBy(EstateType::label)

        fun fromLabel(label: String): EstateType {
            return map[label] ?: throw IllegalArgumentException("Unknown EstateType with label: $label")
        }
    }
}