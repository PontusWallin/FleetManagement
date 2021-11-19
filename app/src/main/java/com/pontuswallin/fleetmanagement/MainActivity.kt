package com.pontuswallin.fleetmanagement

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.pontuswallin.fleetmanagement.adapters.VehicleAdapter
import com.pontuswallin.fleetmanagement.databinding.ActivityMainBinding
import com.pontuswallin.fleetmanagement.model.APIError
import com.pontuswallin.fleetmanagement.model.VehicleAPIResponse
import com.pontuswallin.fleetmanagement.networking.RetrofitClient
import com.pontuswallin.fleetmanagement.utilities.DateUtilities.Companion.parseDateString
import com.pontuswallin.fleetmanagement.utilities.ErrorUtils
import com.pontuswallin.fleetmanagement.utilities.GoogleMapsUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var vehicleAdapter : VehicleAdapter
    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("FleetManagement", Context.MODE_PRIVATE)

        GoogleMapsUtils.isGoogleMapsServiceAvailable(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initRecyclerView()

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

    private fun initRecyclerView() {
        binding.vehicleRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            vehicleAdapter = VehicleAdapter()
            adapter = vehicleAdapter
        }
        setVehicleRecyclerViewVisibility()
    }

    private fun setVehicleRecyclerViewVisibility() {
        if (vehicleAdapter.itemCount == 0) {
            binding.vehicleRecyclerView.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.vehicleRecyclerView.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
        }
    }

    private fun fetchLastVehicleDataFromAPI() {

        RetrofitClient.apiKey  = sharedPreferences.getString("API_Key", "")!!

        val dataCall: Call<VehicleAPIResponse> = RetrofitClient.getClient.getLastData(
            RetrofitClient.apiKey,
            true
        )
        
        dataCall.enqueue(object: Callback<VehicleAPIResponse> {
            override fun onResponse(
                call: Call<VehicleAPIResponse>,
                response: Response<VehicleAPIResponse>) {
                if(!response.isSuccessful) {
                    // If we receive a bad response, we display an error.
                    val error = APIError(response.code(), response.message())
                    ErrorUtils.displayErrorDialog(this@MainActivity, error)
                } else {
                    // If we receive a good response, we display a list of vehicles.
                    var vehicles = response.body()!!.response
                    vehicles = vehicles.sortedByDescending { vehicle ->
                        parseDateString(vehicle.timestamp!!)
                    }
                    vehicleAdapter.submitList(vehicles)
                    vehicleAdapter.notifyDataSetChanged()
                    setVehicleRecyclerViewVisibility()
                }
            }

            override fun onFailure(call: Call<VehicleAPIResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Network failure! Please try again later.", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_enter_key -> showEnterKeyDialog()
            R.id.action_refresh -> refresh()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showEnterKeyDialog(): Boolean {

        val builder : AlertDialog.Builder  = AlertDialog.Builder(this)
        builder.setTitle("Enter API key")

        val keyInput = EditText(this)
        keyInput.inputType = InputType.TYPE_CLASS_TEXT
        keyInput.setText(sharedPreferences.getString("API_Key", "")!!)
        builder.setView(keyInput)

        builder.setPositiveButton("OK") {
                _, _ ->
            setNewAPIKey(keyInput)
            fetchLastVehicleDataFromAPI()
        }

        builder.setNegativeButton("Cancel") {
                dialog, _ -> dialog.cancel()
        }

        builder.show()
        return true
    }

    private fun setNewAPIKey(keyInput: EditText) {
        val apiKey = keyInput.text.toString()
        RetrofitClient.apiKey = apiKey

        sharedPreferences.edit()
            .putString("API_Key", apiKey)
            .apply()
    }

    private fun refresh(): Boolean {
        fetchLastVehicleDataFromAPI()
        return true
    }
}