package com.example.stepforward.data.model

import android.os.Parcel
import android.os.Parcelable

data class Teacher(
    val idTeacher: Int,
    val imageRes: Int,
    val name: String,
    val direction: List<String>,
    val achievements: List<String>,
    val master_class: List<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idTeacher)
        parcel.writeInt(imageRes)
        parcel.writeString(name)
        parcel.writeStringList(direction)
        parcel.writeStringList(achievements)
        parcel.writeStringList(master_class)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Teacher> {
        override fun createFromParcel(parcel: Parcel): Teacher {
            return Teacher(parcel)
        }

        override fun newArray(size: Int): Array<Teacher?> {
            return arrayOfNulls(size)
        }
    }
}