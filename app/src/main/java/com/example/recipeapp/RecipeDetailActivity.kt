package com.example.recipeapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.recipeapp.databinding.ActivityRecipeDetailBinding
import java.util.*

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding

    private var actionBar: ActionBar? = null

    private var dbHelper: DatabaseHelper? = null

    private var recordId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar
        actionBar!!.title = "Record Details"
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        dbHelper = DatabaseHelper(this)

        val intent = intent
        recordId = intent.getStringExtra("RECORD_ID")

        var id = showRecordDetails()

        binding.deletebtn.setOnClickListener{
            dbHelper!!.deleteRecord(id)

            finish()
        }
    }

    private fun showRecordDetails(): String {

        var id: String = ""
        val selectQuery =
            "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.C_ID + " =\"" + recordId + "\""
        val db = dbHelper!!.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                id = "" + cursor.getString(cursor.getColumnIndex(Constants.C_ID))
                val title = "" + cursor.getString(cursor.getColumnIndex(Constants.C_TITLE))
                val image = "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE))
                val type = "" + cursor.getString(cursor.getColumnIndex(Constants.C_TYPE))
                val ingredient = "" + cursor.getString(cursor.getColumnIndex(Constants.C_INGREDIENT))
                val step = "" + cursor.getString(cursor.getColumnIndex(Constants.C_STEP))
                val addedTimestamp = "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP))
                val updatedTimestamp = "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))

                val calendar1 = Calendar.getInstance(Locale.getDefault())
                calendar1.timeInMillis = addedTimestamp.toLong()
                val timeAdded = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:aa", calendar1)

                val calendar2 = Calendar.getInstance(Locale.getDefault())
                calendar1.timeInMillis = updatedTimestamp.toLong()
                val timeUpdated = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:aa", calendar2)

                binding.titleTv.text = title
                binding.typeTv.text = type
                binding.ingredientTv.text = ingredient
                binding.stepTv.text = step
                binding.addedTimeTv.text = timeAdded
                binding.updatedTimeTv.text = timeUpdated

                if(image=="null"){
                    binding.imageIv.setImageResource(R.drawable.ic_food)
                }
                else{
                    binding.imageIv.setImageURI(Uri.parse(image))
                }

            } while (cursor.moveToNext())
        }
        db.close()

        return id
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}