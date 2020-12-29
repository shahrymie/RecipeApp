package com.example.recipeapp

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream

class XmlPullParser {
    private val recipetypes = ArrayList<RecipeTypeModel>()
    private var recipetype: RecipeTypeModel? = null
    private var text: String? = null

    fun parse(inputStream: InputStream): List<RecipeTypeModel> {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> if (tagname.equals("recipetype", ignoreCase = true)) {
                        // create a new instance of employee
                        recipetype = RecipeTypeModel()
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if (tagname.equals("recipetype", ignoreCase = true)) {
                        // add employee object to list
                        recipetype?.let { recipetypes.add(it) }
                    } else if (tagname.equals("type", ignoreCase = true)) {
                        recipetype!!.type = text
                    }
                    else -> {
                    }
                }
                eventType = parser.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return recipetypes
    }
}