package me.electfreak.kotlin

import java.io.File
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.system.exitProcess

typealias Category = List<String>
typealias Categories = Map<String, Category>

/**
 * Gives categories of words following like it represents in json.
 *
 * @return [Categories] of words (see typealias).
 */
fun readCategories(): Categories {
    val gson = Gson()
    val sType = object : TypeToken<Categories>() {}.type
    return gson.fromJson(File("data/categories.json").readText(), sType)
}

/**
 * Gives category by its name.
 * Exits if trying was not successful.
 *
 * @return [Category] of words (see typealias).
 */
fun getCategory(categoryName: String): Category {
    val categories = readCategories()
    val categoryList = categories[categoryName]
    if (categoryList == null) {
        println("Failed to get category, try again")
        exitProcess(0)
    } else {
        return categoryList
    }
}