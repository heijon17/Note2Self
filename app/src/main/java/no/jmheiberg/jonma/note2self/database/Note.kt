package no.jmheiberg.jonma.note2self.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDateTime

//this is just for making android studio not report error
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Entity(tableName = "note")
data class Note (
    var title: String?,
    var desc: String?,
    @ColumnInfo(name = "image")
    var imagePath: String?,
    var timestamp: String?,
    var lat: Double,
    var lng: Double,
    var weatherId: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()) //nullable mismatch, but will not run if fixed

    @Ignore
    constructor(
        title: String,
        desc: String
    ) : this(
        title,
        desc,
        "",
        LocalDateTime.now().toString(),
        0.0,
        0.0,
        ""
    )
    @Ignore
    constructor(
        title: String,
        desc: String,
        imagePath: String?
    ) : this(
        title,
        desc,
        imagePath,
        LocalDateTime.now().toString(),
        0.0,
        0.0,
        ""
    )


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(desc)
        parcel.writeString(imagePath)
        parcel.writeString(timestamp)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(weatherId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}

