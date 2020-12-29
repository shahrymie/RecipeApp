package com.example.recipeapp

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.recipeapp.databinding.ActivityMainBinding
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
            val intent = Intent(this,AddRecipeActivity::class.java)
            intent.putExtra("isEditMode",false)
            startActivity(intent)
        }

    }

    private fun loadRecords() {

        val adapterRecord = RecipeAdapter(this,dbHelper.getAllRecords(NEWEST_FIRST))

        binding.recordRv.adapter = adapterRecord

    }

    private fun setSpinner() {
        var recipetypes: List<RecipeTypeModel>
        try {
            val parser = XmlPullParser()
            val istream = assets.open("recipetypes.xml")
            recipetypes = parser.parse(istream)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, recipetypes!!)
            binding.filterSpinner.adapter = adapter

            binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    dbHelper.filterRecords(recipetypes[position].toString())
                    onResume()
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}