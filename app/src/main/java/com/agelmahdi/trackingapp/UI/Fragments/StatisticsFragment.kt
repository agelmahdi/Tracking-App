package com.agelmahdi.trackingapp.UI.Fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.agelmahdi.trackingapp.R
import com.agelmahdi.trackingapp.UI.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.fragment_statistics) {
    private val viewModel: MainViewModel by viewModels()

}