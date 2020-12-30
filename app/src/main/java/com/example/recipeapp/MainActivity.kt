package com.example.recipeapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.databinding.ActivityMainBinding
import org.w3c.dom.Text
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var dbHelper: DatabaseHelper

    private val NEWEST_FIRST = "${Constants.C_ADDED_TIMESTAMP} DESC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        loadRecords()
        setSpinner()

        binding.addRecordBtn.setOnClickListener{
            val intent = Intent(this, AddRecipeActivity::class.java)
            intent.putExtra("isEditMode", false)
            startActivity(intent)
        }

    }

    private fun loadRecords() {

        val adapterRecord = RecipeAdapter(this, dbHelper.getAllRecords(NEWEST_FIRST))

        binding.recordRv.adapter = adapterRecord

    }

    private fun setSpinner() {
        lateinit var recipetypes: List<RecipeTypeModel>
        try {
            val parser = XmlPullParser()
            val istream = assets.open("recipetypes.xml")
            recipetypes = parser.parse(istream)

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                recipetypes!!
            )
            binding.filterSpinner.adapter = adapter

            binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    parent!!.getItemAtPosition(position)
                    if(position == 0){
                        binding.spinnerTv.text = recipetypes[position].toString()
                        loadRecords()
                    }else{
                        val adapterRecord = RecipeAdapter(this@MainActivity,dbHelper.filterRecords(recipetypes[position].toString()))
                        binding.recordRv.adapter = adapterRecord
                        binding.spinnerTv.text = recipetypes[position].toString()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        }catch (e: IOException) {
            e.printStackTrace()
        }
    }

    public override fun onResume() {
        super.onResume()
        loadRecords()
        setSpinner()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}