package com.agelmahdi.trackingapp.UI

import androidx.lifecycle.ViewModel
import com.agelmahdi.trackingapp.Repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

}