package com.agelmahdi.trackingapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.agelmahdi.trackingapp.DB.Run
import com.agelmahdi.trackingapp.Others.TrackingUtil

import com.agelmahdi.trackingapp.databinding.ItemRunBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*


class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(private val itemRunBinding: ItemRunBinding) : RecyclerView.ViewHolder(itemRunBinding.root){
        fun bind(run: Run){

            Glide.with(itemRunBinding.root).load(run.image).into(itemRunBinding.ivRunImage)

            val aveSpeed= "${run.averageSpeedInKMH} km/h"
            itemRunBinding.tvAvgSpeed.text = aveSpeed

            val calender = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }

            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            itemRunBinding.tvDate.text = dateFormat.format(calender.time)

            val distanceInKm = "${run.distanceInMeters / 1000f} km"
            itemRunBinding.tvDistance.text = distanceInKm

            itemRunBinding.tvTime.text = TrackingUtil.formattedStopWatch(run.timeInMillis)

            val calroies = "${run.caloriesBurned} kcal"
            itemRunBinding.tvCalories.text = calroies

        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {

        val itemBinding = ItemRunBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RunViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.bind(run)
    }

    private var onItemClickListener: ((Run) -> Unit)? = null

    fun setOnItemClickListener(listener: (Run) -> Unit) {
        onItemClickListener = listener
    }
}