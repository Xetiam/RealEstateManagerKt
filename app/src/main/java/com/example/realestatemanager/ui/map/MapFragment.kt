package com.example.realestatemanager.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.realestatemanager.databinding.FragmentMapBinding
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.ui.OnEstateClickListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {
    private val binding: FragmentMapBinding by lazy { FragmentMapBinding.inflate(layoutInflater) }
    private val viewModel: MapViewModel by viewModels()
    private var locationPermissionRequest =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { _: Map<String, Boolean>? ->
            val mapView = binding.mapView
            val supportMapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction()
                .replace(mapView.id, supportMapFragment)
                .commit()
            supportMapFragment.getMapAsync(this)
        }
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.initUi()
        viewModel.viewState.observe(requireActivity()) { state ->
            when (state) {
                is MapState.InitialState -> showInitialState()
                is MapState.LoadingState -> showLoadingState()
                is MapState.WithEstatesState -> showEstatesState(state.estates)
                is MapState.WithoutEstateState -> showWithoutEstateState(state.message)
            }
        }
        return binding.root
    }

    private fun showWithoutEstateState(message: Int) {
        Toast.makeText(
            requireContext(),
            getString(message),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showEstatesState(estates: List<Pair<EstateModel, LatLng?>>) {
        estates.forEach { estateAndPos ->
            estateAndPos.second?.let {
                MarkerOptions().position(it).title(estateAndPos.first.type.label)
            }
                ?.let { map.addMarker(it) }
        }
        map.setOnMarkerClickListener { marker: Marker ->
            val estate =
                estates.find { it.second?.latitude == marker.position.latitude && it.second?.longitude == marker.position.longitude }
            estate?.let {
                it.first.id?.let { id ->
                    (requireActivity() as OnEstateClickListener).onEstateClick(
                        id
                    )
                }
            }
            true
        }
    }

    private fun showLoadingState() {
        //TODO("Not yet implemented")
    }

    private fun showInitialState() {
        val mapView = binding.mapView
        val supportMapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(mapView.id, supportMapFragment)
            .commit()
        supportMapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
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
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                viewModel.loadNearEstates(userLatLng, requireContext())
            }
        }
    }
}