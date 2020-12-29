package com.example.recipeapp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter() : RecyclerView.Adapter<RecipeAdapter.HolderRecord>() {

    private var context: Context? = null
    private var recordList: ArrayList<RecipeModel>? = null

    private lateinit var dbHelper: DatabaseHelper

    constructor(context: Context?, recordList: ArrayList<RecipeModel>?) : this() {
        this.context = context
        this.recordList = recordList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecord {
        return HolderRecord(
            LayoutInflater.from(context).inflate(R.layout.row_record, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return recordList!!.size
    }

    override fun onBindViewHolder(holder: HolderRecord, position: Int) {
        val model = recordList!!.get(position)

        val id = model.id
        val title = model.title
        val image = model.image
        val type = model.type
        val ingredient = model.ingredient
        val step = model.step
        val addedTime = model.addedTime
        val updatedTime = model.updatedTime

        holder.titleIv.text = title
        holder.typeIv.text = type

        if (image == "null") {
            holder.imageIv.setImageResource(R.drawable.ic_food)
        } else {
            holder.imageIv.setImageURI(Uri.parse(image))
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailActivity::class.java)
            intent.putExtra("RECORD_ID", id)
            context!!.startActivity(intent)
        }

        holder.moreBtn.setOnClickListener {
            showMoreOption(
                position,
                id,
                title,
                image,
                type,
                ingredient,
                step,
                addedTime,
                updatedTime
            )
        }
    }

    private fun showMoreOption(
        position: Int,
        id: String,
        title: String,
        image: String,
        type: String,
        ingredient: String,
        step: String,
        addedTime: String,
        updatedTime: String
    ) {

        val intent = Intent(context, AddRecipeActivity::class.java)
        intent.putExtra("ID", id)
        intent.putExtra("TITLE", title)
        intent.putExtra("IMAGE", image)
        intent.putExtra("TYPE", type)
        intent.putExtra("INGREDIENT", ingredient)
        intent.putExtra("STEP", step)
        intent.putExtra("ADDED_TIMESTAMP", addedTime)
        intent.putExtra("UPDATED_TIMESTAMP", updatedTime)
        intent.putExtra("isEditMode", true)
        context!!.startActivity(intent)

    }

    inner class HolderRecord(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageIv: ImageView = itemView.findViewById(R.id.foodIv)
        var titleIv: TextView = itemView.findViewById(R.id.titleTv)
        var typeIv: TextView = itemView.findViewById(R.id.typeTv)
        var moreBtn: ImageButton = itemView.findViewById(R.id.moreBtn)
    }

}