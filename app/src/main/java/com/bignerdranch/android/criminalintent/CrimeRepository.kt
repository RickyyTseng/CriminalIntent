package com.bignerdranch.android.criminalintent

import android.content.Context
import android.content.LocusId
import androidx.room.Room
import database.CrimeDatabase
import database.migration_1_2
import database.migration_2_3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.UUID

private const val DATABASE_NAME = "crime-database"
class CrimeRepository private constructor(context: Context, private val coroutineScope: CoroutineScope = GlobalScope){
    private val database: CrimeDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            CrimeDatabase::class.java,
            DATABASE_NAME
        )
        .addMigrations(migration_1_2, migration_2_3)
        .build()
    fun getCrimes(): Flow<List<Crime>> = database.crimeDAO().getCrimes()
    suspend fun getCrime(id: UUID): Crime = database.crimeDAO().getCrime(id)
    fun updateCrime(crime: Crime){
        coroutineScope.launch {
            database.crimeDAO().updateCrime(crime)
        }
    }
    suspend fun addCrime(crime: Crime){
        database.crimeDAO().addCrime(crime)
    }

    companion object{
        private var INSTANCE: CrimeRepository? = null
        fun intialize(context: Context){
            if (INSTANCE == null){
                INSTANCE = CrimeRepository(context)
            }
        }
        fun get(): CrimeRepository{
            return  INSTANCE ?:
            throw   IllegalStateException("CrimeRepository must be initialized")
        }
    }
}