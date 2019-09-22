package no.jmheiberg.jonma.note2self.util

import no.jmheiberg.jonma.note2self.database.Note
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DateUtil {

    fun fromStringToDate(note: Note): LocalDateTime {
        return LocalDateTime.parse(note.timestamp)
    }

    fun formatDate(dateTime: LocalDateTime, big: Boolean): String {
        val formatter: DateTimeFormatter = if (big) {
            DateTimeFormatter.ofPattern("dd/MM-yyyy\nHH:mm")
        } else {
            DateTimeFormatter.ofPattern("dd/MM\n yyyy\nHH:mm")
        }
        return formatter.format(dateTime)
    }
}