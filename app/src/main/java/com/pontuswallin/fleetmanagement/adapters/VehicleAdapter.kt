package com.pontuswallin.fleetmanagement.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pontuswallin.fleetmanagement.R
import com.pontuswallin.fleetmanagement.model.Vehicle
import com.pontuswallin.fleetmanagement.SingleVehicleActivity
import com.pontuswallin.fleetmanagement.utilities.DateUtilities
import kotlin.collections.ArrayList

class VehicleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Vehicle> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return VehicleViewHolder (
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.layout_vehicle_list_item, parent, false)
            , parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){ is VehicleViewHolder -> {
            holder.bind(items.get(position))
            holder.itemView.setOnClickListener {
                openSingleVehicleActivity(holder.context, items.get(position))
            }
        }}
    }

    private fun openSingleVehicleActivity(context: Context, currentVehicle: Vehicle) {
        val intent  = Intent(context, SingleVehicleActivity::class.java)
        intent.putExtra("currentVehicle", currentVehicle)
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(vehicles: List<Vehicle>) {
        items = vehicles
    }

    class VehicleViewHolder constructor(
        itemView: View,
        val context: Context,
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(vehicle: Vehicle) {

            val regNumberAndNameTv : TextView= itemView.findViewById(R.id.numberAndNameTextView)

            var driverName = vehicle.driverName
            if(driverName==null) {
                driverName = "Unknown"
            }

            regNumberAndNameTv.text = vehicle.plate + " / " + driverName

            val speedTv : TextView= itemView.findViewById(R.id.speedTextView)
            speedTv.text = vehicle.speed.toString() + " km/h"

            val addressTv : TextView= itemView.findViewById(R.id.addressTextView)
            addressTv.text = vehicle.address

            val timeAgo = DateUtilities.createTimeAgo(vehicle.timestamp!!)

            val dataAgeTv : TextView= itemView.findViewById(R.id.dataAgeTextView)
            dataAgeTv.text = timeAgo
        }
    }
}