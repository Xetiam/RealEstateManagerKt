package com.example.realestatemanager.ui.settings

import android.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.realestatemanager.databinding.FragmentSettingsBinding
import com.example.realestatemanager.ui.CurrencyChangeListener

class SettingsFragment : Fragment() {
    private val binding: FragmentSettingsBinding by lazy {
        FragmentSettingsBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userSharedPrefs =
            requireContext().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        val currencySelected = userSharedPrefs.getString(USER_CURRENCY, "USD")
        val currencyList = listOf("USD", "EUR")
        val currentUser = userSharedPrefs.getString(USER_NAME, "")
        binding.userName.setText(currentUser)
        val typeAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_item, currencyList)
        typeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.currencySpinner.adapter = typeAdapter
        binding.currencySpinner.setSelection(typeAdapter.getPosition(currencySelected))
        binding.confirmPreferences.setOnClickListener {
            val editor = userSharedPrefs.edit()
            editor.putString(USER_NAME, binding.userName.text.toString())
            editor.putString("user_currency", binding.currencySpinner.selectedItem.toString())
            editor.apply()
            (requireActivity() as CurrencyChangeListener).onCurrencyChange()
        }
        return binding.root
    }

    companion object {
        const val USER_NAME = "user_name"
        const val USER_CURRENCY = "user_currency"
        const val USER_PREFS = "UserPrefs"
    }
}