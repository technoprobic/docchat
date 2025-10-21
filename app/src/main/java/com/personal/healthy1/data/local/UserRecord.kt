package com.personal.healthy1.data.local

data class UserRecord(
    var id: String = java.util.UUID.randomUUID().toString(),
    var title: String,
    var summary: String,
    val sourceType: Int, // 0=Pasted, 1=File, 2=Image
    var filePath: String? = null,
    var creationStatus: Int, // 0=created, 1=Ingesting, 1=Completed
    var statusTime: Long = System.currentTimeMillis()
)
