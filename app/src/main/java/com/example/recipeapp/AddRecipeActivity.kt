package com.example.recipeapp

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blogspot.atifsoftwares.circularimageview.CircularImageView
import com.example.recipeapp.databinding.ActivityAddRecipeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.IOException

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecipeBinding
    private val CAMERA_REQUEST_CODE = 100;
    private val  STORAGE_REQUEST_CODE = 101;
    private val IMAGE_PICK_CAMERA_CODE = 102;
    private val IMAGE_PICK_GALLERY_CODE = 103;

    private lateinit var cameraPermissions:Array<String>
    private lateinit var storagePermissions:Array<String>

    private var imageUri: Uri? = null
    private var id:String? = ""
    private var title: String? = ""
    private var type: String? = ""
    private var ingredient: String? = ""
    private var step: String? = ""
    private var isEditMode = false
    private var addedTime: String? = ""
    private var updatedTime: String? = ""


    private var actionBar:ActionBar? = null;

    lateinit var dbHelper:DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        actionBar = supportActionBar
        actionBar!!.title = "Add Record"

        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        val intent = intent
        isEditMode = intent.getBooleanExtra("isEditMode", false)
        if(isEditMode){
            actionBar!!.title = "Update Record"
            id = intent.getStringExtra("ID")
            title = intent.getStringExtra("TITLE")
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"))
            type = intent.getStringExtra("TYPE")
            ingredient = intent.getStringExtra("INGREDIENT")
            step = intent.getStringExtra("STEP")
            addedTime = intent.getStringExtra("ADDED_TIMESTAMP")
            updatedTime = intent.getStringExtra("UPDATED_TIMESTAMP")

            if(imageUri.toString() == "null"){
                binding.ImageIv.setImageResource(R.drawable.ic_food)
            }else{
                binding.ImageIv.setImageURI(imageUri)
            }
            binding.titleEt.setText(title)
            binding.typeTv.setText(type)
            binding.ingredientEt.setText(ingredient)
            binding.stepEt.setText(step)

        }else{
            actionBar!!.title =  "Add Record"
        }

        setSpinner()

        dbHelper = DatabaseHelper(this)

        cameraPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        binding.ImageIv.setOnClickListener{
            imagePickDialog();
        }

        binding.saveBtn.setOnClickListener{
            inputData()
        }
    }

    private fun inputData() {
        title = "" + binding.titleEt.text.toString().trim()
        type = "" + binding.typeTv.text.toString().trim()
        ingredient = "" + binding.ingredientEt.text.toString().trim()
        step = "" + binding.stepEt.text.toString().trim()

        if(isEditMode){

            val timeStamp = "${System.currentTimeMillis()}"
            dbHelper?.updateRecord(
                "$id",
                "$title",
                "$imageUri",
                "$type",
                "$ingredient",
                "$step",
                "$addedTime",
                "$timeStamp"
            )

            Toast.makeText(this,"Updated...", Toast.LENGTH_SHORT).show()
        }else{
            val timeStamp = "${System.currentTimeMillis()}"
            val id = dbHelper.insertRecord(
                "$title",
                "$imageUri",
                "$type",
                "$ingredient",
                "$step",
                "$timeStamp",
                "$timeStamp"
            )

            Toast.makeText(this,"Record Added against ID $id", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun setSpinner() {
        lateinit var recipetypes: List<RecipeTypeModel>
        try {
            val parser = XmlPullParser()
            val istream = assets.open("recipetypes.xml")
            recipetypes = parser.parse(istream)

            recipetypes = recipetypes.drop(1)

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                recipetypes!!
            )
            binding.typeSpinner.adapter = adapter

            binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    parent!!.getItemAtPosition(position)
                    binding.typeTv.text = recipetypes[position].toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                        binding.typeTv.text = "Category"
                }

            }
        }catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun imagePickDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Image From")
        builder.setItems(options){dialog, which ->
            if(which==0){
                if(!checkCameraPermissions()){
                    requestCameraPermission()
                }else{
                    pickFromCamera()
                }
            }
            else{
                if(!checkStoragePermission()){
                    requestStoragePermission()
                }else{
                    pickFromGallery()
                }
            }
        }
        builder.show()
    }

    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(
            galleryIntent,
            IMAGE_PICK_GALLERY_CODE
        )
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(
            cameraIntent,
            IMAGE_PICK_CAMERA_CODE
        )

    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermissions(): Boolean {
        val results = ContextCompat.checkSelfPermission(this,
        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val resultsq = ContextCompat.checkSelfPermission(this,
        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        return results && resultsq
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE->{
                if(grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if(cameraAccepted && storageAccepted){
                        pickFromCamera()
                    }else{
                        Toast.makeText(this,"Camera and Storage permission required",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE -> {
               if(grantResults.isNotEmpty()){
                   val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                   if(storageAccepted){
                       pickFromGallery()
                   }else{
                       Toast.makeText(this,"Storage permission required",Toast.LENGTH_SHORT).show()
                   }
               }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode== IMAGE_PICK_GALLERY_CODE){
                CropImage.activity(data!!.data).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1).start(this)
            }else if(requestCode==IMAGE_PICK_CAMERA_CODE){
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1).start(this)
            }else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                val result = CropImage.getActivityResult(data)
                if(resultCode == Activity.RESULT_OK){
                    val resultUri = result.uri
                    imageUri = resultUri
                    binding.ImageIv.setImageURI(resultUri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    val error = result.error
                    Toast.makeText(this,""+error,Toast.LENGTH_SHORT).show()
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}