package com.example.recipeapp

object Constants {

    const val DB_NAME = "RECIPE_DB"
    const val DB_VERSION = 1

    const val TABLE_NAME = "RECORD_TABLE"

    const val C_ID = "ID"
    const val C_TITLE = "TITLE"
    const val C_IMAGE = "IMAGE"
    const val C_TYPE = "TYPE"
    const val C_INGREDIENT = "INGREDIENT"
    const val C_STEP =  "STEP"
    const val C_ADDED_TIMESTAMP = "ADDED_TIMESTAMP"
    const val C_UPDATED_TIMESTAMP = "UPDATED_TIMESTAMP"

    const val CREATE_TABLE = (
            "CREATE TABLE " + TABLE_NAME + "("
            + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + C_TITLE + " TEXT,"
            + C_IMAGE + " TEXT,"
            + C_TYPE + " TEXT,"
            + C_INGREDIENT+ " TEXT,"
            + C_STEP + " TEXT,"
            + C_ADDED_TIMESTAMP + " TEXT,"
            + C_UPDATED_TIMESTAMP + " TEXT)"
            )
}