package com.ozyuce.maps.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlin.io.use

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `attendance` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `personId` TEXT NOT NULL,
                    `serviceId` TEXT NOT NULL,
                    `date` INTEGER NOT NULL,
                    `status` TEXT NOT NULL DEFAULT 'PRESENT'
                )
            """.trimIndent()
        )

        database.query("PRAGMA table_info(`attendance`)").use { cursor ->
            var hasStatusColumn = false
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                if (name == "status") {
                    hasStatusColumn = true
                    break
                }
            }
            if (!hasStatusColumn) {
                database.execSQL("ALTER TABLE attendance ADD COLUMN status TEXT NOT NULL DEFAULT 'PRESENT'")
            }
        }
    }
}




