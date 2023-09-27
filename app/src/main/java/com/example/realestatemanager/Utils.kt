package com.example.realestatemanager

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.realestatemanager.data.EstateRepository
import com.example.realestatemanager.data.contentprovider.EstateContentProviderWrapper
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.net.InetSocketAddress
import java.net.Socket
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToLong


/**
 * Created by Philippe on 21/02/2018.
 */
object Utils {
    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param dollars
     * @return
     */
    fun convertDollarToEuro(dollars: Int): Int {
        return (dollars * 0.901).roundToLong().toInt()
    }

    fun convertEuroToDollar(dollars: Int): Int {
        return (dollars * 1 / 0.901).roundToLong().toInt()
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @return
     */
    val todayDate: String
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            return dateFormat.format(Date())
        }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param context
     * @return
     */
    fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities: NetworkCapabilities? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm.getNetworkCapabilities(cm.activeNetwork)
            } else {
                try {
                    Socket().use { socket ->
                        socket.connect(InetSocketAddress("www.google.com", 80), 1500)
                        return true
                    }
                } catch (e: IOException) {
                    return false
                }
            }
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    fun getEstateRepository(context: Context): EstateRepository {
        return if (isInternetAvailable(context)) {
            //TODO : En attente du back
            EstateContentProviderWrapper(context.contentResolver)
        } else {
            EstateContentProviderWrapper(context.contentResolver)
        }
    }

    fun isAddressValid(address: String): Boolean {
        val pattern = Regex("(\\d+)?\\s*([\\w\\s]+),\\s*(\\d{5})\\s*([\\w\\s]+),\\s*([\\w\\s]+)")
        return pattern.matches(address)
    }

    fun extractCityFromAddress(address: String): String? {
        val pattern =
            Regex("(\\d+)?\\s*([\\w\\s]+),\\s*(\\d{5})\\s*([\\w\\s]+),\\s*([\\w\\s]+)")

        val matchResult = pattern.find(address)
        if (matchResult != null && matchResult.groups.size >= 5) {
            return matchResult.groups[4]?.value?.trim()
        }
        return null
    }

    fun formatPriceNumber(price: Int): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val dec = DecimalFormat("###,###,###,###,###", symbols)
        return dec.format(price)
    }

    fun getLocationFromAdress(address: String, context: Context, callBack: (LatLng?) -> Unit) {
        val geocoder = Geocoder(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(address, 1) {
                sendResultLatLng(it, callBack)

            }
        } else {
            val addresses: List<Address>? = geocoder.getFromLocationName(address, 1)
            sendResultLatLng(addresses.orEmpty(), callBack)
        }
    }

    private fun sendResultLatLng(addresses: List<Address>, callBack: (LatLng?) -> Unit) {
        val latLng: LatLng?
        latLng = if (addresses.isNotEmpty()) {
            LatLng(addresses[0].latitude, addresses[0].longitude)
        } else {
            null
        }
        callBack(latLng)
    }

    fun computeDistanceBetweenTwoPoints(
        userLatLng: LatLng,
        latLng: LatLng
    ): Double {
        val earthRadiusKm = 6371.0 // Rayon moyen de la Terre en kilomètres

        val lat1 = userLatLng.latitude
        val lon1 = userLatLng.longitude
        val lat2 = latLng.latitude
        val lon2 = latLng.longitude

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = kotlin.math.sin(dLat / 2).pow(2) + kotlin.math.cos(lat1) * kotlin.math.cos(lat2) * kotlin.math.sin(
            dLon / 2
        ).pow(2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

        return earthRadiusKm * c
    }
}