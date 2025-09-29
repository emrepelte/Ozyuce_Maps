package com.ozyuce.maps.core.database.dao

import androidx.room.*
import com.ozyuce.maps.core.database.entity.PersonEntity
import com.ozyuce.maps.core.database.entity.PersonStopCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM persons")
    fun getAll(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getById(id: String): PersonEntity?

    @Query("SELECT * FROM persons WHERE needsSync = 1")
    suspend fun getUnsynced(): List<PersonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: PersonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(persons: List<PersonEntity>)

    @Delete
    suspend fun delete(person: PersonEntity)

    @Query("UPDATE persons SET lastSyncedAt = :timestamp, needsSync = 0 WHERE id = :id")
    suspend fun markSynced(id: String, timestamp: Long)

    // Stop ili?kileri
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonStopCrossRef(crossRef: PersonStopCrossRef)

    @Query("""
        SELECT * FROM persons
        INNER JOIN person_stop_cross_ref ON persons.id = person_stop_cross_ref.personId
        WHERE person_stop_cross_ref.stopId = :stopId
    """)
    fun getPersonsByStopId(stopId: String): Flow<List<PersonEntity>>
}
