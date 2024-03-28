package com.example.project5

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Sleep_table")
data class SleepEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "Date") val date : String?,
    @ColumnInfo(name = "time") val time : String?,
    @ColumnInfo(name = "Quality") val quality: String?,
    @ColumnInfo(name = "notes") val notes: String?
)