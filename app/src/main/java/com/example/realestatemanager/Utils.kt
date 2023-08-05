package com.openclassrooms.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.realestatemanager.data.contentprovider.EstateContentProviderWrapper
import com.example.realestatemanager.data.EstateRepository
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


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
        return Math.round(dollars * 0.901).toInt()
    }

    fun convertEuroToDollar(dollars: Int): Int {
        return Math.round(dollars * 1.11).toInt()
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @return
     */
    val todayDate: String
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
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
        val capabilities: NetworkCapabilities? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    fun getEstateRepository(context: Context) : EstateRepository =
        if (isInternetAvailable(context)) {
            //TODO : En attente du back
            EstateContentProviderWrapper(context)
        } else {
            EstateContentProviderWrapper(context)
        }

    fun isAddressValid(address: String): Boolean {
        val pattern = Regex("(\\d+)?\\s*([\\w\\s]+),\\s*(\\d{5})\\s*([\\w\\s]+),\\s*([\\w\\s]+)")
        return pattern.matches(address)
    }

    fun extractCityFromAddress(address: String): String? {
        val pattern = Regex("(\\d+)?\\s*([\\w\\s]+),\\s*(\\d{5})\\s*([\\w\\s]+),\\s*([\\w\\s]+)") // Expression régulière pour un format européen générique

        val matchResult = pattern.find(address)
        if (matchResult != null && matchResult.groups.size >= 5) {
            return matchResult.groups[4]?.value?.trim() // Récupère le groupe correspondant à la ville et retire les espaces éventuels autour
        }
        return null // Adresse invalide ou incomplète
    }
}