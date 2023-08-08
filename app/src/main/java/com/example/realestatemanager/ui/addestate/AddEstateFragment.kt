package com.example.realestatemanager.ui.addestate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.FragmentAddEstateBinding
import com.example.realestatemanager.model.EstateType
import com.example.realestatemanager.ui.adapter.EstatePictureItemAdapter

class AddEstateFragment : Fragment() {
    private val binding: FragmentAddEstateBinding by lazy { FragmentAddEstateBinding.inflate(layoutInflater) }
    private val viewModel: AddEstateViewModel by viewModels()
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Array<String>>
    private val selectedImageUris = mutableListOf<Uri>()
    private val IMAGE_MIME_TYPE = "image/*"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        viewModel.initUi()
        viewModel.viewState.observe(requireActivity()) { state ->
            when (state) {
                is AddEstateState.LoadingState -> showLoadingState()
                is AddEstateState.InitialState -> showInitialState()
                is AddEstateState.WrongFormatAdress -> showAdressWarning()
                is AddEstateState.WrongInputPrice -> showPriceWarning()
                is AddEstateState.WrongInputSurface -> showSurfaceWarning()
                is AddEstateState.PictureDescriptionMissingState -> showPictureDescriptionWarning()
                is AddEstateState.EstateCreatedState -> showEstateCreatedState()
            }
        }
        return binding.root
    }

    private fun showEstateCreatedState() {
        Toast.makeText(
            requireContext(),
            getString(R.string.add_estate_creation_success),
            Toast.LENGTH_LONG
        ).show()
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().remove(this).commit()
        parentFragmentManager.setFragmentResult("yourFragmentDestroyedKey", Bundle())
    }

    private fun showPictureDescriptionWarning() {
        Toast.makeText(
            requireContext(),
            getString(R.string.add_estate_picture_description_warning),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun displayPictures() {
        binding.gallery.apply {
            isVisible = true
            adapter = EstatePictureItemAdapter(selectedImageUris) { description, position ->
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
                requireContext()
            )
        }
    }

    private fun openImagePicker() {
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = IMAGE_MIME_TYPE
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            imagePickerLauncher.launch(arrayOf(IMAGE_MIME_TYPE))
        }
    }
}