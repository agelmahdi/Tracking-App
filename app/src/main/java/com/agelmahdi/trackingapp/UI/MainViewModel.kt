package com.agelmahdi.trackingapp.UI

import androidx.lifecycle.ViewModel
import com.agelmahdi.trackingapp.Repositories.MainRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

}