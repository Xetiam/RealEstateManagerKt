package com.example.realestatemanager.ui.addestate

import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.FragmentAddEstateBinding
import com.example.realestatemanager.model.EstateInterestPoint
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.model.EstateType
import com.example.realestatemanager.ui.MainActivity.Companion.ARG_ESTATE_ID
import com.example.realestatemanager.ui.SlidingPanelListener
import com.example.realestatemanager.ui.adapter.EstatePictureItemAdapter

class AddEstateFragment : Fragment() {
    private val binding: FragmentAddEstateBinding by lazy {
        FragmentAddEstateBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: AddEstateViewModel by viewModels()
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Array<String>>
    private val selectedImageUris = mutableListOf<Uri>()
    private val IMAGE_MIME_TYPE = "image/*"
    private var isModifying = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.initUi()
        viewModel.viewState.observe(requireActivity()) { state ->
            when (state) {
                is AddEstateState.LoadingState -> showLoadingState()
                is AddEstateState.InitialState -> showInitialState()
                is AddEstateState.WrongFormatAdress -> showAdressWarning()
                is AddEstateState.WrongInputPrice -> showPriceWarning()
                is AddEstateState.WrongInputSurface -> showSurfaceWarning()
                is AddEstateState.PictureDescriptionMissingState -> showPictureDescriptionWarning()
                is AddEstateState.ToastMessageState -> showEstateCreatedState(state.message)
                is AddEstateState.EstateDataState -> showEstateDataState(state.estate)
            }
        }
        return binding.root
    }

    private fun showEstateDataState(estate: EstateModel) {
        selectedImageUris.addAll(estate.pictures.map { it.first })
        binding.apply {
            type.setSelection(estate.type.ordinal)
            price.setText(estate.dollarPrice.toString())
            surface.setText(estate.surface.toString())
            rooms.setSelection(estate.rooms.first - 1)
            bathrooms.setSelection(estate.rooms.second - 1)
            bedrooms.setSelection(estate.rooms.third - 1)
            description.setText(estate.description)
            address.setText(estate.address)
            isSold.isChecked = estate.sellDate != null
            interestPoints.children.forEach {
                val checkBox = it as CheckBox
                checkBox.isChecked = estate.interestPoints.contains(
                    EstateInterestPoint.fromLabel(
                        checkBox.text.toString()
                    )
                )
            }
        }
        binding.isSold.isVisible = true
        binding.estateCreationButton.text = getString(R.string.add_estate_modification_button)
        displayPictures(estate.pictures.map { it.second })
    }

    private fun showEstateCreatedState(message: Int) {
        Toast.makeText(
            requireContext(),
            getString(message),
            Toast.LENGTH_LONG
        ).show()
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().remove(this).commit()
        (requireActivity() as SlidingPanelListener).closeSlidingPanel()
    }

    private fun showPictureDescriptionWarning() {
        Toast.makeText(
            requireContext(),
            getString(R.string.add_estate_picture_description_warning),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun displayPictures(descriptions: List<String>? = null) {
        binding.gallery.apply {
            isVisible = true
            adapter =
                EstatePictureItemAdapter(selectedImageUris, descriptions) { description, position ->
                    viewModel.addDescriptionOrModify(description, position)
                }
        }

    }

    private fun showSurfaceWarning() {
        binding.surface.error =
            getString(R.string.add_estate_picture_surface_warning)
    }

    private fun showPriceWarning() {
        binding.price.error =
            getString(R.string.add_estate_picture_price_warning)
    }

    private fun showAdressWarning() {
        binding.address.error = getString(R.string.add_estate_picture_address_warning)
    }

    private fun showInitialState() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
                uris.map {
                    requireContext().contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                uris?.let {
                    selectedImageUris.clear()
                    selectedImageUris.addAll(uris)
                    displayPictures()
                }
            }
        setListeners()
        setSpinnerAdapter(binding.type, EstateType.values().map { it.label })
        setSpinnerAdapter(binding.rooms, (1..20).toList().map { it.toString() })
        setSpinnerAdapter(binding.bathrooms, (1..20).toList().map { it.toString() })
        setSpinnerAdapter(binding.bedrooms, (1..20).toList().map { it.toString() })
        EstateInterestPoint.values().forEach { option ->
            val checkBox = CheckBox(requireContext())
            checkBox.text = option.label
            checkBox.isChecked = false
            binding.interestPoints.addView(checkBox)
        }
        viewModel.getEstateData(arguments?.getLong(ARG_ESTATE_ID), requireContext())
        isModifying = (arguments?.getLong(ARG_ESTATE_ID) != null
                && arguments?.getLong(ARG_ESTATE_ID) != 0L)

    }

    private fun showLoadingState() {
        //TODO("Not yet implemented")
    }

    private fun setSpinnerAdapter(spinner: Spinner, spinnerItems: List<String>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = typeAdapter
    }

    private fun setListeners() {
        binding.addImage.setOnClickListener {
            openImagePicker()
        }
        binding.estateCreationButton.setOnClickListener {
            viewModel.initiateCreation(
                EstateType.fromLabel(binding.type.selectedItem.toString()),
                binding.price.text.toString(),
                binding.surface.text.toString(),
                Triple(
                    binding.rooms.selectedItem.toString().toInt(),
                    binding.bathrooms.selectedItem.toString().toInt(),
                    binding.bedrooms.selectedItem.toString().toInt()
                ),
                binding.description.text.toString(),
                ArrayList(selectedImageUris),
                binding.address.text.toString(),
                ArrayList(
                    binding.interestPoints.children.filterIsInstance<CheckBox>()
                        .filter { it.isChecked }
                        .map { EstateInterestPoint.fromLabel(it.text.toString()) }.toList()
                ),
                requireContext(),
                isModifying,
                arguments?.getLong(ARG_ESTATE_ID),
                binding.isSold.isChecked
            )
        }
    }

    private fun openImagePicker() {
        Intent(ACTION_OPEN_DOCUMENT).apply {
            type = IMAGE_MIME_TYPE
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            imagePickerLauncher.launch(arrayOf(IMAGE_MIME_TYPE))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isModifying = false
        arguments?.clear()
    }
}