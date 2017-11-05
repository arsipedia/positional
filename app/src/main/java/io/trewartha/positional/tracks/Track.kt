package io.trewartha.positional.tracks

import io.trewartha.positional.time.Duration
import org.threeten.bp.Instant

class Track {

    /**
     * The distance (in meters) that this [Track] covers. This distance is the sum of all the
     * [TrackSegment]s' distances, which are in turn the sums of distances between the
     * [TrackSegment]s' [TrackPoint]s. As such, this distance does not represent an "as the crow
     * flies" distance, but rather a travelled distance.
     */
    var distance: Float = 0.0f
        private set
        get() = segments.map { it.distance }.sum()

    /**
     * The [Duration] of time that this [Track] spans
     */
    var duration: Duration = Duration(0, 0, 0)
        private set
        get() {
            val startInstant = start ?: throw IllegalStateException("Track has not been started")
            val endInstant = end ?: Instant.now()
            return Duration.between(startInstant, endInstant)
        }

    var end: Instant? = null
        private set

    var start: Instant? = null
        private set

    private val segments = mutableListOf<TrackSegment>()

    /**
     * Adds a new [TrackPoint] to the last [TrackSegment] of this [Track]
     */
    fun addPoint(point: TrackPoint?) {
        if (point == null) {
            // Start a new track segment if we had some points in a segment before and this new
            // point is null (which means some location data just didn't come in for some reason).
            if (!segments.isEmpty() && segments.last().getPoints().isNotEmpty()) {
                segments.add(TrackSegment())
            }
        } else if (segments.isEmpty()) {
            val segment = TrackSegment().apply { addPoint(point) }
            segments.add(segment)
        } else {
            segments.last().addPoint(point)
        }
    }

    /**
     * Stops this [Track], settings its [end] to the current [Instant]
     */
    fun stop() {
        end = Instant.now()
    }

    /**
     * Gets an immutable copy of the [TrackSegment]s for this [Track]
     */
    fun getSegments(): List<TrackSegment> {
        return segments.toList()
    }

    /**
     * Starts this [Track], settings its [start] to the current [Instant]
     */
    fun start() {
        start = Instant.now()
    }
}