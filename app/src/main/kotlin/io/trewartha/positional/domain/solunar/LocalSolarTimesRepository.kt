package io.trewartha.positional.domain.solunar

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toLocalTime
import javax.inject.Inject

/**
 * Local implementation of a [SolarTimesRepository] capable of calculating solar times without using
 * an internet connection to an API
 */
class LocalSolarTimesRepository @Inject constructor() : SolarTimesRepository {

    override fun getAstronomicalDawn(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getAstronomicalSunriseForDate(date.toCalendar())
        .asLocalTime()

    override fun getAstronomicalDusk(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getAstronomicalSunsetForDate(date.toCalendar())
        .asLocalTime()

    override fun getCivilDawn(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getCivilSunriseForDate(date.toCalendar())
        .asLocalTime()

    override fun getCivilDusk(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getCivilSunsetForDate(date.toCalendar())
        .asLocalTime()

    override fun getNauticalDawn(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getNauticalSunriseForDate(date.toCalendar())
        .asLocalTime()

    override fun getNauticalDusk(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getNauticalSunsetForDate(date.toCalendar())
        .asLocalTime()

    override fun getSunrise(date: LocalDate, latitude: Double, longitude: Double): LocalTime? =
        createLibraryCalculator(latitude, longitude)
            .getOfficialSunriseForDate(date.toCalendar())
            .asLocalTime()

    override fun getSunset(date: LocalDate, latitude: Double, longitude: Double): LocalTime? =
        createLibraryCalculator(latitude, longitude)
            .getOfficialSunsetForDate(date.toCalendar())
            .asLocalTime()

    private fun createLibraryCalculator(latitude: Double, longitude: Double) =
        SunriseSunsetCalculator(Location(latitude, longitude), java.util.TimeZone.getDefault())

    private fun String.asLocalTime(): LocalTime? = takeIf { it != NO_TIME }?.toLocalTime()

    private companion object {
        private const val NO_TIME = "00:00"
    }
}
