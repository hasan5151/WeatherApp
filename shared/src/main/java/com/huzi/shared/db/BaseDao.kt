package com.huzi.shared.db

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(type: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: List<T>)

    @Update
    suspend fun update(type: T)

    @Delete
    suspend fun delete(type: T)
}