package com.bearings.data.stretch

interface RoutesRepository {
    suspend fun getTravelTime(
        origin: LatLng,
        destination: String,
        mode: TravelMode = TravelMode.TRANSIT,
    ): TravelTime?
}

data class LatLng(val latitude: Double, val longitude: Double)

enum class TravelMode { TRANSIT, WALK, DRIVE }

class MockRoutesRepository : RoutesRepository {
    override suspend fun getTravelTime(
        origin: LatLng,
        destination: String,
        mode: TravelMode,
    ): TravelTime = TravelTime(durationSeconds = 12 * 60, mode = mode.name)
}
