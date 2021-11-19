package com.pontuswallin.fleetmanagement

import android.Manifest
import android.R
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.pontuswallin.fleetmanagement.databinding.ActivitySingleVehicleBinding
import com.pontuswallin.fleetmanagement.model.APIError
import com.pontuswallin.fleetmanagement.model.RawDataAPIResponse
import com.pontuswallin.fleetmanagement.model.Vehicle
import com.pontuswallin.fleetmanagement.model.VehicleLocation
import com.pontuswallin.fleetmanagement.networking.RetrofitClient
import com.pontuswallin.fleetmanagement.utilities.DateUtilities
import com.pontuswallin.fleetmanagement.utilities.ErrorUtils
import com.pontuswallin.fleetmanagement.utilities.GoogleMapsUtils.Companion.COARSE_LOCATION
import com.pontuswallin.fleetmanagement.utilities.GoogleMapsUtils.Companion.FINE_LOCATION
import com.pontuswallin.fleetmanagement.utilities.GoogleMapsUtils.Companion.LOCATION_PERMISSION_REQUEST_CODE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class SingleVehicleActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivitySingleVehicleBinding

    private lateinit var map: GoogleMap
    private var vehicle: Vehicle? = null

    private var locationPermissionsGranted = false
    private lateinit var selectedDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getVehicleFromIntent()
        binding = ActivitySingleVehicleBinding.inflate(layoutInflater)
        initializeToolbar()
        setContentView(binding.root)

        setSelectedDate()
        initializeDateSelectionUI()
        initMap()
        getLocationPermission()
    }

    private fun setSelectedDate() {
        if (vehicle!!.timestamp != null) {
            selectedDate = DateUtilities.dateStringToDateObj(vehicle!!.timestamp!!)
        } else {
            selectedDate = Date()
        }
    }

    private fun initializeDateSelectionUI() {
        binding.dateTextView.text = DateUtilities.getSimpleDateStringWithSlashes(selectedDate)

        binding.calendarButton.setOnClickListener {
            val datePicker = DatePickerFragment()
            datePicker.show(supportFragmentManager, "datePicker")
        }
    }

    private fun getVehicleFromIntent() {
        if (intent.hasExtra("currentVehicle")) {
            vehicle = intent.getParcelableExtra("currentVehicle")
        }
    }

    private fun initializeToolbar() {
        val toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Location history: " + vehicle!!.plate
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(com.pontuswallin.fleetmanagement.R.id.historyMapFragment) as SupportMapFragment
        mapFragment.getMapAsync { map = it }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val selectedDateString = DateUtilities.getSimpleDateStringWithSlashes(c.time)
        binding.dateTextView.text = selectedDateString

        if (locationPermissionsGranted) {
            fetchVehicleHistoryFromAPI()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        locationPermissionsGranted = false

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE ->
                if (grantResults.size > 0) {
                    for (granResult in grantResults) {
                        if (granResult != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionsGranted = false
                            return
                        }
                    }
                    locationPermissionsGranted = true
                    if (!currentlyFetchingFromAPI) {
                        fetchVehicleHistoryFromAPI()
                    }
                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private var currentlyFetchingFromAPI = false

    private fun getLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        if (ContextCompat.checkSelfPermission(applicationContext, FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionsGranted = true
            if (!currentlyFetchingFromAPI) {
                fetchVehicleHistoryFromAPI()
            }

            if (ContextCompat.checkSelfPermission(applicationContext, COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionsGranted = true
                if (!currentlyFetchingFromAPI) {
                    fetchVehicleHistoryFromAPI()
                }

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun fetchVehicleHistoryFromAPI() {

        var dateAsLocalDate = DateUtilities.convertToLocalDate(selectedDate)
        dateAsLocalDate = dateAsLocalDate?.plusDays(1)
        val tomorrowDate = DateUtilities.convertToDate(dateAsLocalDate!!)

        val selectedDateAsString = DateUtilities.getSimpleDateStringWithDashes(selectedDate)
        val tomorrowDateAsString = DateUtilities.getSimpleDateStringWithDashes(tomorrowDate!!)
        val dataCall: Call<RawDataAPIResponse> = RetrofitClient.getClient.getLastDataByTimeRange(
            selectedDateAsString,
            tomorrowDateAsString,
            vehicle?.objectId!!,
            RetrofitClient.apiKey,
            true
        )

        currentlyFetchingFromAPI = true

        dataCall.enqueue(object : Callback<RawDataAPIResponse> {
            override fun onResponse(
                call: Call<RawDataAPIResponse>,
                response: Response<RawDataAPIResponse>
            ) {

                // If the response is not successful, we display an error message
                if (!response.isSuccessful) {
                    val error = APIError(response.code(), response.message())
                    ErrorUtils.displayErrorDialog(this@SingleVehicleActivity, error)
                } else {
                    val body = response.body()
                    // If the response is successful, but we receive a bad status code from the API
                    // We will also show an error message.
                    if (body?.status != 0) { // I'm assuming that status 0 means successful call
                        val error = APIError(body?.status!!, body.errormessage)
                        ErrorUtils.displayErrorDialog(this@SingleVehicleActivity, error)
                    } else {
                        // If the response is successful, and we received a cod status from API..
                        val locationList = response.body()!!.response
                        // .. We check to see if we received any locations or not..
                        if (locationList.isEmpty()) {
                            Toast.makeText(
                                applicationContext,
                                "No data for current date and vehicle!",
                                Toast.LENGTH_SHORT
                            ).show()
                            // .. if we have no location data, we simply zoom the camera in on the last known location.
                            createMarkerForLastKnownPosition()
                        } else {
                            // If we do receive any locations from the API, we first perform a
                            // sanity check so we don't accidentally use response data which is actually
                            // missing longitude and latitude values
                            if (locationList.get(0).latitude == 0.0 || locationList.get(0).longitude == 0.0) {
                                ErrorUtils.displayErrorDialog(
                                    this@SingleVehicleActivity,
                                    APIError(
                                        0,
                                        "Latitude or Longitude data is missing for this Vehicle!"
                                    )
                                )
                            } else {
                                // If the location data makes sense (contains longitude and latitude values that are not 0)
                                // we create a route. With a marker at the start and end..
                                buildFullVehicleRoute(locationList)
                                // .. and calculate the Trip Distance for the route.
                                calculateTripDistance(locationList)
                            }
                        }
                    }
                }
                currentlyFetchingFromAPI = false
            }

            override fun onFailure(call: Call<RawDataAPIResponse>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Network failure! Please try again later.",
                    Toast.LENGTH_LONG
                ).show()
                currentlyFetchingFromAPI = false
            }
        })
    }

    private fun buildFullVehicleRoute(locationList: List<VehicleLocation>) {
        // create markers for first and last location
        val firstLocation = locationList.get(0)
        createVehicleLocationMarker(firstLocation, "Start")
        createVehicleLocationMarker(locationList.last(), "End")

        // fill the rest of the route with polylines
        val locations = ArrayList<LatLng>()
        for (location in locationList) {
            val currentLocation = LatLng(location.latitude, location.longitude)
            locations.add(currentLocation)

        }
        map.addPolyline(PolylineOptions().addAll(locations))

        // Zoom in on the first location.
        zoomOnLocation(LatLng(firstLocation.latitude, firstLocation.longitude))
    }

    private fun calculateTripDistance(locationList: List<VehicleLocation>) {
        var totalDistanceInKM = 0f

        var previousLocation = locationList.get(0)
        for (i in 1..locationList.size - 1) {
            val currentLocation = locationList.get(i)
            val results = FloatArray(1)
            Location.distanceBetween(
                previousLocation.latitude, previousLocation.longitude,
                currentLocation.latitude, currentLocation.longitude,
                results
            )

            // result is returned in Meters, so we need to convert the result into kilometers before
            // we add it to the distance total
            val currentDistanceInKM = (results[0] / 1000)
            totalDistanceInKM += currentDistanceInKM
        }

        // We need to round the distance to the nearest two decimals, and convert the distance
        // into a string..
        val formattedDistance = createFormattedDistanceString(totalDistanceInKM)
        // .. before we can finally display the distance to the user.
        binding.tripDistanceTv.text = "Trip Distance: " + formattedDistance + "Km"
    }

    private fun createFormattedDistanceString(totalDistanceInKM: Float): String? {
        val decFormat = DecimalFormat(".##")
        decFormat.roundingMode = RoundingMode.CEILING
        val formattedDistance = decFormat.format(totalDistanceInKM)
        return formattedDistance
    }

    private fun createVehicleLocationMarker(location: VehicleLocation, title: String) {
        val latlngOfStartLocation = LatLng(location.latitude, location.longitude)
        map.addMarker(MarkerOptions().position(latlngOfStartLocation).title(title))
    }

    private fun createMarkerForLastKnownPosition() {
        val location = LatLng(vehicle!!.latitude!!, vehicle!!.longitude!!)
        map.addMarker(MarkerOptions().position(location))

        zoomOnLocation(location)
    }

    private fun zoomOnLocation(location: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .target(location)
            .zoom(12F)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}