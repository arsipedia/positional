package io.trewartha.positional.ui.sun

import androidx.compose.runtime.Immutable
import io.trewartha.positional.ui.State
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Immutable
data class SunState(
    val todaysDate: LocalDate,
    val selectedDate: LocalDate,
    val astronomicalDawn: State<LocalTime?, Nothing>,
    val nauticalDawn: State<LocalTime?, Nothing>,
    val civilDawn: State<LocalTime?, Nothing>,
    val sunrise: State<LocalTime?, Nothing>,
    val sunset: State<LocalTime?, Nothing>,
    val civilDusk: State<LocalTime?, Nothing>,
    val nauticalDusk: State<LocalTime?, Nothing>,
    val astronomicalDusk: State<LocalTime?, Nothing>
)
