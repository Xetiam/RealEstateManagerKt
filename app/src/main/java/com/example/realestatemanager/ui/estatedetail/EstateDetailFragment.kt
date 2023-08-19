package com.example.realestatemanager.ui.estatedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestatemanager.databinding.FragmentEstateDetailBinding
import com.example.realestatemanager.model.EstateInterestPoint
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.ui.MainActivity.Companion.ARG_ESTATE_ID
import com.example.realestatemanager.ui.adapter.EstatePictureDetailItemAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.Utils


class EstateDetailFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: EstateDetailViewModel by viewModels()
    private val binding: FragmentEstateDetailBinding by lazy {
        FragmentEstateDetailBinding.inflate(layoutInflater)
    }
    private var address: String? = null
    private lateinit var map: GoogleMap
    /*var locationPermissionRequest =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { _: Map<String, Boolean>? -> }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        viewModel.initUi()

        val mapView = binding.mapView
        val supportMapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(mapView.id, supportMapFragment)
            .commit()
        supportMapFragment.getMapAsync(this)

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EstateDetailState.InitialState -> showInitialState()
                is EstateDetailState.LoadingState -> showLoadingState()
                is EstateDetailState.WithEstateState -> showWithEstateState(state.estate)
            }
        }
        arguments?.getLong(ARG_ESTATE_ID)?.let { viewModel.getEstateDetail(it, requireContext()) }
        return binding.root
    }

    private fun showWithEstateState(estate: EstateModel) {
        address = estate.address
        binding.gallery.apply {
            val pictureAdapter = EstatePictureDetailItemAdapter()
            pictureAdapter.submitList(estate.pictures)
            adapter = pictureAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        EstateInterestPoint.values().forEach { option ->
            val checkBox = CheckBox(requireContext())
            checkBox.text = option.label.replace(" ", "\n")
            checkBox.isChecked = false
            binding.interestPoints.addView(checkBox)
        }
        binding.apply {
            description.text = estate.description
            surface.text = estate.surface.toString()
            nbRooms.text = estate.rooms.first.toString()
            nbBathrooms.text = estate.rooms.second.toString()
            nbBedrooms.text = estate.rooms.third.toString()
            address.text = estate.address.replace(", ", "\n")
            interestPoints.children.forEach {
                val checkBox = it as CheckBox
                checkBox.isChecked = estate.interestPoints.contains(
                    EstateInterestPoint.fromLabel(
                        checkBox.text.toString().replace("\n", " ")
                    )
                )
                checkBox.isFocusable = false
                checkBox.isClickable = false
            }
        }
    }

    private fun showLoadingState() {
        //TODO("Not yet implemented")
    }

    private fun showInitialState() {

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        address?.let {

            Utils.getLocationFromAdress(it, requireContext()) { location ->
                if (location != null) {
                    requireActivity().runOnUiThread {
                        map.addMarker(MarkerOptions().position(location))
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
                        map.uiSettings.isScrollGesturesEnabled = false
                        map.uiSettings.isZoomGesturesEnabled = false
                    }
                }
            }
        }
    }
    //TODO : code avec launcher de permission pour l'écran de map
    /*map = googleMap
    val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

    val permissionsGranted =
        ActivityCompat.checkSelfPermission(
            requireActivity(),
            fineLocationPermission
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    coarseLocationPermission
                ) == PackageManager.PERMISSION_GRANTED

    if (!permissionsGranted) {
        locationPermissionRequest.launch(
            arrayOf(
                fineLocationPermission,
                coarseLocationPermission
            )
        )
    } else {
        map.isMyLocationEnabled = true
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val lastKnownLocation =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        lastKnownLocation?.let { location ->
            val userLatLng = LatLng(location.latitude, location.longitude)
            map.addMarker(MarkerOptions().position(userLatLng).title("Votre position"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
        }
    }*/


}