package io.trewartha.positional.ui.location

import androidx.compose.runtime.Immutable
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.units.Units

@Immutable
data class LocationState(
    val location: Location?,
    val coordinatesFormat: CoordinatesFormat,
    val units: Units,
    val showAccuracies: Boolean,
)
