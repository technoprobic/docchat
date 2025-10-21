package com.personal.healthy1.data

import android.util.Log
import com.personal.healthy1.data.local.UserRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object RecordRepository {
    private val TAG = this.javaClass.simpleName

    private val records = mutableListOf<UserRecord>()

    fun getAllRecords(): Flow<List<UserRecord>> {
        return flow { emit(records) }
    }

    fun getRecordsByIds(ids: List<String>): Flow<List<UserRecord>> {
        return flow { emit(records.filter { it.id in ids }) }
    }

    fun getRecordById(id: String): UserRecord? {
        return records.filter { it.id == id }.firstOrNull()
    }

    fun insertRecord(record: UserRecord) {
        records.add(record)
    }

    fun updateRecord(record: UserRecord) {
        val index = records.indexOfFirst { it.id == record.id }
        if (index != -1) {
            records[index] = record
        } else {
            records.add(record)
        }
    }
}