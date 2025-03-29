package com.example.stepforward.data.model

import android.os.Parcel
import android.os.Parcelable

data class LoggedInUser (
    val userId: String = "", // Значение по умолчанию
    val abonement: String = "", // Значение по умолчанию
    val pass: String = "", // Значение по умолчанию
    val displayName: String = "", // Значение по умолчанию
    val displaySecondName: String = "", // Значение по умолчанию
    val displaySurName: String = "", // Значение по умолчанию
    val dateBirthday: String = "", // Значение по умолчанию
    val imagePath: String = "" // Значение по умолчанию
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",  // userId
        parcel.readString() ?: "",  // abonement
        parcel.readString() ?: "",  // pass
        parcel.readString() ?: "",  // displayName
        parcel.readString() ?: "",  // displaySecondName
        parcel.readString() ?: "",  // displaySurName
        parcel.readString() ?: "",  // dateBirthday
        parcel.readString() ?: ""   // Img
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(abonement)
        parcel.writeString(pass)
        parcel.writeString(displayName)
        parcel.writeString(displaySecondName)
        parcel.writeString(displaySurName)
        parcel.writeString(dateBirthday)
        parcel.writeString(imagePath)
    }

    override fun describeContents(): Int {
        return 0
    }

    // В классе LoggedInUser
    companion object CREATOR : Parcelable.Creator<LoggedInUser> {
        override fun createFromParcel(parcel: Parcel): LoggedInUser {
            return LoggedInUser(parcel)
        }

        override fun newArray(size: Int): Array<LoggedInUser ?> {
            return arrayOfNulls(size)
        }
    }
}