package com.example.recipeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.SyncStateContract

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(
    context,
    Constants.DB_NAME,
    null,
    Constants.DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(Constants.CREATE_TABLE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db != null) {
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME)
        }
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
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(Constants.C_TITLE, title)
        values.put(Constants.C_IMAGE, image)
        values.put(Constants.C_TYPE, type)
        values.put(Constants.C_INGREDIENT, ingredient)
        values.put(Constants.C_STEP, step)
        values.put(Constants.C_ADDED_TIMESTAMP, addedTime)
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTime)

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
        val db = this.writableDatabase
        val values = ContentValues()

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
        val db = writableDatabase
        db.delete(Constants.TABLE_NAME,"${Constants.C_ID}=?", arrayOf(id))
        db.close()
    }

    fun deleteAllRecords(){
        val db = writableDatabase
        db.execSQL("DELETE FROM ${Constants.TABLE_NAME}")
        db.close()
    }
}