package com.example.stepforward.data.model

import android.os.Parcel
import android.os.Parcelable
import java.util.Calendar
import java.util.Date

enum class Role {
    USER, ADMIN
}

data class LoggedInUser (
    val userId: String = "",
    val abonement: String = "",
    val pass: String = "",
    val displayName: String = "",
    val displaySecondName: String = "",
    val displaySurName: String = "",
    val dateBirthday: String = "",
    var imagePath: String = "",
    val daySession: List<Date> = emptyList(),
    val teacher: Teacher,
    val role: Role = Role.USER,
    val schedulePattern: SchedulePattern = SchedulePattern(),
    val trialLesson: TrialLesson? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        mutableListOf<Date>().apply {
            val size = parcel.readInt()
            for (i in 0 until size) {
                add(Date(parcel.readLong()))
            }
        }.sortedDescending(),
        parcel.readParcelable(Teacher::class.java.classLoader) ?: Teacher(0, 0, "", emptyList(), emptyList(), emptyList()), // Чтение teacher
        Role.values()[parcel.readInt()] // Чтение роли
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
        parcel.writeInt(daySession.size)
        daySession.forEach { date ->
            parcel.writeLong(date.time)
        }
        parcel.writeParcelable(teacher, flags) // Запись teacher
        parcel.writeInt(role.ordinal) // Сохранение роли
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoggedInUser > {
        override fun createFromParcel(parcel: Parcel): LoggedInUser  {
            return LoggedInUser (parcel)
        }

        override fun newArray(size: Int): Array<LoggedInUser ?> {
            return arrayOfNulls(size)
        }
    }
}

data class SchedulePattern(
    val daysOfWeek: List<Int> = listOf(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY),
    val timeOfDay: String = "18:00",
    val durationWeeks: Int = 4 // На сколько недель вперед генерировать расписание
) : Parcelable {
    // Реализация Parcelable
    constructor(parcel: Parcel) : this(
        parcel.createIntArray()?.toList() ?: emptyList(),
        parcel.readString() ?: "18:00",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeIntArray(daysOfWeek.toIntArray())
        parcel.writeString(timeOfDay)
        parcel.writeInt(durationWeeks)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SchedulePattern> {
        override fun createFromParcel(parcel: Parcel): SchedulePattern {
            return SchedulePattern(parcel)
        }

        override fun newArray(size: Int): Array<SchedulePattern?> {
            return arrayOfNulls(size)
        }
    }
}

data class TrialLesson(
    val direction: String,
    val studio: String,
    val date: Date,
    val completed: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Date(parcel.readLong()),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(direction)
        parcel.writeString(studio)
        parcel.writeLong(date.time)
        parcel.writeByte(if (completed) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TrialLesson> {
        override fun createFromParcel(parcel: Parcel): TrialLesson {
            return TrialLesson(parcel)
        }

        override fun newArray(size: Int): Array<TrialLesson?> {
            return arrayOfNulls(size)
        }
    }
}