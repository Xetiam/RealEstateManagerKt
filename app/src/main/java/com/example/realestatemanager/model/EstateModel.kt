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
    val pictures: ArrayList<Pair<Uri,String>>,//done
    val address: String,//done
    val interestPoints: ArrayList<EstateInterestPoint>,//TODO
    val status: EstateStatus,//done
    val startDate: Date,//auto
    val sellDate: Date? = null,//done
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
enum class EstateStatus(val label: String) {
    TO_SALE("To sale"),
    SOLD("Sold");
    companion object {
        private val map = values().associateBy(EstateStatus::label)

        fun fromLabel(label: String): EstateStatus {
            return map[label] ?: throw IllegalArgumentException("Unknown EstateType with label: $label")
        }
    }
}
enum class EstateInterestPoint(val label: String) {
    SCHOOL("school"),
    TRANSPORT("public transport"),
    PARK("park"),
    SHOPS("shops"),
    HOSPITAL("hospital"),
    RESTAURANT("restaurant");
    companion object {
        private val map = values().associateBy(EstateInterestPoint::label)

        fun fromLabel(label: String): EstateInterestPoint {
            return map[label] ?: throw IllegalArgumentException("Unknown EstateType with label: $label")
        }
    }
}