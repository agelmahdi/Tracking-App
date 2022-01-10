package com.agelmahdi.trackingapp.UI

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agelmahdi.trackingapp.DB.Run
import com.agelmahdi.trackingapp.Others.SortType
import com.agelmahdi.trackingapp.Repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    private val runSortedByDate = mainRepository.getByDate()
    private val runSortedByDistance = mainRepository.getDistance()
    private val runSortedByTime = mainRepository.getByTimeInMillis()
    private val runSortedByAvgSpeed = mainRepository.getByAveSpeed()
    private val runSortedByCalories = mainRepository.getByCalories()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE

    init {
        mediatorLiveData(sortType)
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insert(run)
    }

    // observe sort type changes to be recognised by the MediatorLiveData
    fun sortRun(sortType: SortType) = when (sortType) {
        SortType.DATE -> runSortedByDate.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runSortedByTime.value?.let { runs.value = it }
        SortType.DISTANCE -> runSortedByDistance.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runSortedByAvgSpeed.value?.let { runs.value = it }
        SortType.CALORIES -> runSortedByCalories.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }

    // add all live to a single mediator
    private fun mediatorLiveData(sortType: SortType) {

        runs.addSource(runSortedByDate) { result ->
            if (sortType == SortType.DATE) {
                result?.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runSortedByDistance) { result ->
            if (sortType == SortType.DISTANCE) {
                result?.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runSortedByTime) { result ->
            if (sortType == SortType.RUNNING_TIME) {
                result?.let {
                    runs.value = it
                }
            }
        }


        runs.addSource(runSortedByAvgSpeed) { result ->
            if (sortType == SortType.AVG_SPEED) {
                result?.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runSortedByCalories) { result ->
            if (sortType == SortType.CALORIES) {
                result?.let {
                    runs.value = it
                }
            }
        }
    }
}