package com.example.time4medapp

import androidx.room.*

import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long

    @Query("SELECT * FROM history_table ORDER BY scheduledDateTime DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("DELETE FROM history_table")
    suspend fun deleteAllHistory()

    @Query("DELETE FROM history_table WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("""
        UPDATE history_table 
        SET status = :newStatus, 
            actualDateTime = :actualDateTime 
        WHERE id = :id
    """)
    suspend fun updateStatusAndTime(
        id: Int,
        newStatus: String,
        actualDateTime: String?
    )

    @Update
    suspend fun updateHistory(history: HistoryEntity)
}