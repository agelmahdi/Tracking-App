package com.agelmahdi.trackingapp.Repositories

import com.agelmahdi.trackingapp.DB.Run
import com.agelmahdi.trackingapp.DB.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runDAO: RunDAO
) {
    suspend fun insert(run: Run) = runDAO.insertRun(run)

    suspend fun delete(run: Run) = runDAO.deleteRun(run)

    fun getByDate() = runDAO.getAllRunsSortedByDate()

    fun getDistance() = runDAO.getAllRunsSortedByDistance()

    fun getByTimeInMillis() = runDAO.getAllRunsSortedByTimeInMillis()

    fun getByAveSpeed() = runDAO.getAllRunsSortedByAverageSpeed()

    fun getByCalories() = runDAO.getAllRunsSortedByCaloriesBurned()

    fun getTotalAvgSpeed() = runDAO.getTotalAvgSpeed()

    fun getTotalDistance() = runDAO.getTotalDistance()

    fun getTotalCalories() = runDAO.getTotalCaloriesBurned()

    fun getTotalTimeInMillis() = runDAO.getTotalTimeInMillis()
}