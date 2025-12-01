package pt.ipleiria.travelbook.Models

import java.util.UUID

enum class LocationStatus { PLANNED, VISITED }

data class Location(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val country: String = "",
    val notes: List<String> = emptyList(),
    val status: LocationStatus = LocationStatus.PLANNED,
    val startDate: Long? = null,
    val endDate: Long? = null
)
