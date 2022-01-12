package com.agelmahdi.trackingapp.Others

import android.content.Context
import android.view.LayoutInflater
import com.agelmahdi.trackingapp.DB.Run
import com.agelmahdi.trackingapp.databinding.MarkerViewBinding
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView constructor(
    val runs: List<Run>,
    context: Context,
    layoutId: Int

) : MarkerView(context, layoutId) {

    private lateinit var binding: MarkerViewBinding

    init {
        binding = MarkerViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null) {
            return
        }
        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(calendar.time)

        val avgSpeed = "${run.averageSpeedInKMH}km/h"
        binding.tvAvgSpeed.text = avgSpeed

        val distanceInKm = "${run.distanceInMeters / 1000f}km"
        binding.tvDistance.text = distanceInKm

        binding.tvDuration.text = TrackingUtil.formattedStopWatch(run.timeInMillis)

        val caloriesBurned = "${run.caloriesBurned}kcal"
        binding.tvCaloriesBurned.text = caloriesBurned
    }
}