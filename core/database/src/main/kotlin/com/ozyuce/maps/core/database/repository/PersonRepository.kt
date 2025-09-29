package com.ozyuce.maps.core.database.repository

import com.ozyuce.maps.core.database.model.Person
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun getAll(): Flow<List<Person>>
    suspend fun getById(id: String): Person?
    suspend fun insert(person: Person)
    suspend fun insertAll(persons: List<Person>)
    suspend fun delete(person: Person)
    suspend fun syncPerson(id: String)
    fun getPersonsByStopId(stopId: String): Flow<List<Person>>
}
