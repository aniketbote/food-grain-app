package com.example.farmfresh

import android.util.Log
import android.util.Patterns
import com.google.firebase.database.DataSnapshot
import java.io.Serializable
import java.security.MessageDigest

object HelperUtils {

    fun getList(p0: DataSnapshot): MutableList<HashMap<String, String>> {
        val finalList = mutableListOf<HashMap<String, String>>()
        for (itemName in p0.children) {
            val tempHashMap = HashMap<String, String>()
            tempHashMap.put("Name", itemName.key.toString())
            for (subItem in itemName.children) {
                tempHashMap.put(subItem.key.toString(), subItem.value.toString())
            }
            Log.d("LoadingActivity", "${tempHashMap}")
            finalList.add(tempHashMap)
        }
        return finalList
    }

    fun generatehash(stringToBeHashed:String): String {
        val bytes = stringToBeHashed.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    fun EmailValidate(email: String): Boolean {
        Log.d("RegisterActivity","Funtion : email validate")
        return Patterns.EMAIL_ADDRESS.toRegex().matches(email)
    }
    fun getCatObj(itemList:List<HashMap<String, String>>, totalCount:String): subCategoryData {
        val catDataObj = subCategoryData(itemList, totalCount.toInt())
        return catDataObj
    }

}

class subCategoryData(val itemList:List<HashMap<String, String>>, val totalCount: Int): Serializable