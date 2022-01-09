package com.agelmahdi.trackingapp.UI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agelmahdi.trackingapp.DB.Run
import com.agelmahdi.trackingapp.Repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    val runSortedByDate = mainRepository.getByDate()
    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insert(run)
    }
}