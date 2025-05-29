package com.example.stepforward.data

import com.example.stepforward.data.model.SchedulePattern
import java.util.Calendar
import java.util.Date

object ScheduleGenerator {
    fun generateSchedule(pattern: SchedulePattern): List<Date> {
        val sessions = mutableListOf<Date>()
        val (hour, minute) = pattern.timeOfDay.split(":").map { it.toInt() }

        val now = Calendar.getInstance()
        val currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)

        // Базовая дата с установленным временем
        val baseDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Генерируем расписание на указанное количество недель
        for (week in 0 until pattern.durationWeeks) {
            for (dayOfWeek in pattern.daysOfWeek) {
                val sessionDate = baseDate.clone() as Calendar

                // Устанавливаем день недели
                sessionDate.set(Calendar.DAY_OF_WEEK, dayOfWeek)

                // Добавляем недели
                sessionDate.add(Calendar.WEEK_OF_YEAR, week)

                // Проверяем, не прошла ли уже эта дата
                if (sessionDate.time.after(now.time) ||
                    (sessionDate.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
                            sessionDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            (hour > currentHour || (hour == currentHour && minute >= currentMinute)))) {
                    sessions.add(sessionDate.time)
                }
            }
        }

        return sessions.sorted()
    }
}