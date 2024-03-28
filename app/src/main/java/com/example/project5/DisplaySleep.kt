package com.example.project5

data class DisplaySleep(
    val id: Long,
    val date: String?,
    val time: String?,
    val quality: String?,
    val notes: String?
) : java.io.Serializable

fun SleepEntity.toDisplaySleep(): DisplaySleep {
    return DisplaySleep(
        id = this.id,
        date = this.date,
        time = this.time,
        quality = this.quality,
        notes = this.notes
    )
}