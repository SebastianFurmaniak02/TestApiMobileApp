package com.example.testapi
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)  {
    companion object {
        private const val DATABASE_NAME = "catfacts.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "facts"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FACT = "fact"
        private const val COLUMN_LENGTH = "length"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_FACT TEXT, $COLUMN_LENGTH INTEGER)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertData(dbRecord: DBRecord) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FACT, dbRecord.fact)
            put(COLUMN_LENGTH, dbRecord.length)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllFacts() : List<DBRecord> {
        val factsList = mutableListOf<DBRecord>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val fact = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FACT))
            val length = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LENGTH))

            val dbRecord = DBRecord(id,fact,length)
            factsList.add(dbRecord)
        }
        cursor.close()
        db.close()
        return factsList
    }

    fun clearDatabase() {
        val db = writableDatabase
        val clearQuery = "DELETE FROM $TABLE_NAME"
        db.execSQL(clearQuery)
    }
}