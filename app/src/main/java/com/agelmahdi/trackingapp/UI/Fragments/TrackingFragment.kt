package com.agelmahdi.trackingapp.UI.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.agelmahdi.trackingapp.DB.Run
import com.agelmahdi.trackingapp.Others.Constants.ACTION_PAUSE_SERVICE
import com.agelmahdi.trackingapp.Others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.agelmahdi.trackingapp.Others.Constants.ACTION_STOP_SERVICE
import com.agelmahdi.trackingapp.Others.Constants.CAMERA_ZOOM
import com.agelmahdi.trackingapp.Others.Constants.POLYLINE_COLOR
import com.agelmahdi.trackingapp.Others.Constants.POLYLINE_WITH
import com.agelmahdi.trackingapp.Others.TrackingUtil
import com.agelmahdi.trackingapp.R
import com.agelmahdi.trackingapp.Sevices.BackgroundLocationService
import com.agelmahdi.trackingapp.Sevices.Polyline
import com.agelmahdi.trackingapp.Sevices.TrackingService
import com.agelmahdi.trackingapp.UI.ViewModels.MainViewModel
import com.agelmahdi.trackingapp.databinding.FragmentTrackingBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import timber.log.Timber.d
import java.util.*
import javax.inject.Inject
import kotlin.math.round

const val CANCEL_TRACKING_DIALOG_TAG = "cancel_tracking_dialog"
const val TAG = "TrackingFragment"

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentTrackingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var map: GoogleMap? = null

    private var isTracking = false
    private var pathPoint = mutableListOf<Polyline>()

    private var currentTimeInMillis = 0L

    private var menu: Menu? = null

    @set:Inject
    var weight = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true)

        _binding = FragmentTrackingBinding.inflate(inflater, container, false)

        val view = binding.root

        binding.mapView.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            val cancelTrackingDialog = parentFragmentManager
                .findFragmentByTag(CANCEL_TRACKING_DIALOG_TAG)
                    as CancelTrackingDialog?

            cancelTrackingDialog?.setConfirmListener {
                cancelRun()
            }
        }

        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }

        binding.btnFinishRun.setOnClickListener {
            zoomTheWholeTrack()
            endRunAndSaveToDb()
        }

        subscribeToObservers()

        return view
    }

    private fun toggleRun() {

        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)

        }
    }

    private fun subscribeToObservers() {
        BackgroundLocationService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        BackgroundLocationService.pathPoints.observe(viewLifecycleOwner, Observer {
            if (isTracking) {
                pathPoint = it
                addLatestPolyline()
                moveCameraToUser()
            }
        })

        BackgroundLocationService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            if (isTracking) {
                currentTimeInMillis = it
                val formatTime = TrackingUtil.formattedStopWatch(currentTimeInMillis, true)
                binding.tvTimer.text = formatTime
            }
        })

    }

    private fun cancelRun() {

        sendCommandToService(ACTION_STOP_SERVICE)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.trackingFragment, true)
            .build()
        findNavController().navigate(
            R.id.action_trackingFragment_to_runFragment,
            null,
            navOptions
        )

    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && currentTimeInMillis > 0L) {
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        } else if (isTracking) {
            binding.btnToggleRun.text = "Stop"
            binding.btnFinishRun.visibility = View.GONE
            menu?.getItem(0)?.isVisible = true
        }
    }

    private fun moveCameraToUser() {
        if (pathPoint.isNotEmpty() && pathPoint.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoint.last().last(),
                    CAMERA_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines() {
        for (polyline in pathPoint) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WITH)
                .addAll(polyline)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoint.isNotEmpty() && pathPoint.last().size > 1) {
            val preLastLatlng = pathPoint.last()[pathPoint.last().size - 2]
            val lastLatlng = pathPoint.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WITH)
                .add(preLastLatlng)
                .add(lastLatlng)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun endRunAndSaveToDb() {

        map?.snapshot {
            var distanceInMeters = 0

            for (polyline in pathPoint) {
                distanceInMeters += TrackingUtil.calcPolylineLength(polyline).toInt()
            }

            val avgSpeedKMH =
                round((distanceInMeters / 1000f) / (currentTimeInMillis / 1000f / 60 / 60) * 10) / 10f

            val timestamp = Calendar.getInstance().timeInMillis

            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()

            val run = Run(
                it,
                timestamp,
                avgSpeedKMH,
                distanceInMeters,
                currentTimeInMillis,
                caloriesBurned
            )

            viewModel.insertRun(run)

            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()

            cancelRun()
        }
    }

    private fun zoomTheWholeTrack() {
        val bounds = LatLngBounds.builder()
        for (polyline in pathPoint) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun sendCommandToService(action: String) {
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)

    }

    // to change the visibility of menu item
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.icCancelTracking -> {
                CancelTrackingDialog().apply {
                    setConfirmListener {
                        cancelRun()
                    }
                }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}