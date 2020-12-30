package com.example.recipeapp

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.SyncStateContract
import com.example.recipeapp.Constants.DB_NAME
import com.example.recipeapp.Constants.DB_VERSION
import java.io.File
import java.io.FileOutputStream

class DatabaseHelper(val context: Context?) : SQLiteOpenHelper(
    context,
    Constants.DB_NAME,
    null,
    Constants.DB_VERSION
) {

    private val preferences: SharedPreferences = context!!.getSharedPreferences(
        "${context.packageName}.database_versions",
        Context.MODE_PRIVATE
    )

    private fun installedDatabaseIsOutdated(): Boolean {
        return preferences.getInt(DB_NAME, 0) < DB_VERSION
    }

    private fun writeDatabaseVersionInPreferences() {
        preferences.edit().apply {
            putInt(DB_NAME, DB_VERSION)
            apply()
        }
    }

    private fun installDatabaseFromAssets() {
        val inputStream = context!!.assets.open("$DB_NAME.db")

        try {
            val outputFile = File(context!!.getDatabasePath(DB_NAME).path)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()

            outputStream.flush()
            outputStream.close()
        } catch (exception: Throwable) {
            throw RuntimeException("The $DB_NAME database couldn't be installed.", exception)
        }
    }

    @Synchronized
    private fun installOrUpdateIfNecessary() {
        if (installedDatabaseIsOutdated()) {
            context!!.deleteDatabase(DB_NAME)
            installDatabaseFromAssets()
            writeDatabaseVersionInPreferences()
        }
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getWritableDatabase()
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getReadableDatabase()
    }

    override fun onCreate(db: SQLiteDatabase?) {
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insertRecord(
        title: String?,
        image: String?,
        type: String?,
        ingredient: String?,
        step: String?,
        addedTime: String?,
        updatedTime: String?
    ): Long {
        val values = ContentValues()
        values.put(Constants.C_TITLE, title)
        values.put(Constants.C_IMAGE, image)
        values.put(Constants.C_TYPE, type)
        values.put(Constants.C_INGREDIENT, ingredient)
        values.put(Constants.C_STEP, step)
        values.put(Constants.C_ADDED_TIMESTAMP, addedTime)
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTime)
        val db = this.writableDatabase
        val id = db.insert(Constants.TABLE_NAME, null, values)
        db.close()

        return id
    }

    fun updateRecord(
        id:String,
        title:String?,
        image: String?,
        type: String?,
        ingredient: String?,
        step: String?,
        addedTime: String?,
        updatedTime: String?
    ):Long
    {
        val values = ContentValues()
        val db = this.writableDatabase
        values.put(Constants.C_TITLE, title)
        values.put(Constants.C_IMAGE, image)
        values.put(Constants.C_TYPE, type)
        values.put(Constants.C_INGREDIENT, ingredient)
        values.put(Constants.C_STEP, step)
        values.put(Constants.C_ADDED_TIMESTAMP, addedTime)
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTime)
        return db.update(Constants.TABLE_NAME,
            values,
            "${Constants.C_ID} =?",
            arrayOf(id)).toLong()

    }

    fun getAllRecords(orderBy: String): ArrayList<RecipeModel> {
        val recordList = ArrayList<RecipeModel>()
        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} ORDER BY $orderBy"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val model = RecipeModel(
                    "" + cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_TITLE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_TYPE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_INGREDIENT)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_STEP)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP)),
                )
                recordList.add(model)
            } while (cursor.moveToNext())
        }
        db.close()
        return recordList
    }

    fun filterRecords(query: String): ArrayList<RecipeModel> {
        val recordList = ArrayList<RecipeModel>()
        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.C_TYPE} LIKE '%$query%'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val model = RecipeModel(
                    "" + cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_TITLE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_TYPE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_INGREDIENT)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_STEP)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP)),
                )
                recordList.add(model)
            } while (cursor.moveToNext())
        }
        db.close()
        return recordList
    }

    fun recordCount():Int{
        val countQuery = "SELECT * FROM ${Constants.TABLE_NAME}"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery,null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun deleteRecord(id: String){
        val db = this.writableDatabase
        db.delete(Constants.TABLE_NAME,"${Constants.C_ID}=?", arrayOf(id))
        db.close()
    }

    fun deleteAllRecords(){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM ${Constants.TABLE_NAME}")
        db.close()
    }
}