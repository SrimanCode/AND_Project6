package com.example.project5

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Query("SELECT * FROM Sleep_table")
    fun getAll(): Flow<List<SleepEntity>>

    @Insert
    fun insert(sleep: SleepEntity)

    @Query("DELETE FROM Sleep_table")
    fun deleteAll()

    @Update
    fun update(sleep: SleepEntity): Int

    @Query("DELETE FROM Sleep_table WHERE id = :id")
    fun deleteById(id: Long): Int

    @Query("SELECT * FROM Sleep_table WHERE id = :id")
    fun findById(id: Long): SleepEntity?
}