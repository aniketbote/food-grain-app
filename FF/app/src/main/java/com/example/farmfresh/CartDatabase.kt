package com.example.farmfresh

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


val DATABASE_NAME = "FarmFreshDB"

class CartDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,3){
    private val token: SharedPreferences = context.getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
    private val emailHash = token.getString("EMAILHASH", "")

    private val TABLE_NAME = "Cart_$emailHash"
    private val COL_NAME = "NAME"
    private val COL_IMAGE = "IMAGE"
    private val COL_PRICE = "PRICE"
    private val COL_SIZE = "SIZE"
    private val COL_COUNT = "COUNT"

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        val tableExistQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='$TABLE_NAME'"
        val result = db?.rawQuery(tableExistQuery, null)
        if(result?.count!! <= 0){
            onCreate(db)
        }
    }


    override fun onCreate(p0: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_NAME + " VARCHAR(256)," +
                COL_PRICE + " VARCHAR(64)," +
                COL_SIZE + " VARCHAR(64)," +
                COL_IMAGE + " VARCHAR(1024)," +
                COL_COUNT + " VARCHAR(64))"

        p0?.execSQL(createTable)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        Log.d("CartDataBase", "Version Changed")
        p0?.execSQL("DROP TABLE IF EXISTS CartItem")
        onCreate(p0)
    }

    fun insertData(cartItem: CartItem): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME,cartItem.name)
        cv.put(COL_SIZE,cartItem.size)
        cv.put(COL_PRICE, cartItem.price)
        cv.put(COL_IMAGE, cartItem.imageUrl)
        cv.put(COL_COUNT, cartItem.count)
        val result = db.insert(TABLE_NAME,null, cv)
        db.close()
        return result
    }

    fun readData(): MutableList<CartItem> {
        val cartList:MutableList<CartItem> = ArrayList()
        val db = this.readableDatabase
        val readQuery = "SELECT * FROM $TABLE_NAME"
        val result = db.rawQuery(readQuery,null)
        if(result.moveToFirst()){
            do {
                val cartItem = CartItem(
                    result.getString(result.getColumnIndex(COL_NAME)),
                    result.getString(result.getColumnIndex(COL_IMAGE)),
                    result.getString(result.getColumnIndex(COL_SIZE)),
                    result.getString(result.getColumnIndex(COL_PRICE)),
                    result.getString(result.getColumnIndex(COL_COUNT)))
                cartList.add(cartItem)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return cartList
    }

    fun deleteData(name:String){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_NAME=?", arrayOf(name))
        db.close()
    }

    fun updateData(name:String, count:String){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_COUNT, count)
        db.update(TABLE_NAME, cv, "$COL_NAME=?", arrayOf(name))
    }
    fun checkTableExist(): Boolean {
        val tableExistQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='$TABLE_NAME'"
        val db = this.readableDatabase
        val result = db.rawQuery(tableExistQuery, null)
        return result.count > 0
    }


}