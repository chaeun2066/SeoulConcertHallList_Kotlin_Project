package com.example.seoulconcerthalllist

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context, dbName: String, version: Int): SQLiteOpenHelper(context, dbName, null, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            create table concertTBL(
            number text primary key,
            name text,
            phone text, 
            state text, 
            address text, 
            star integer
            ) 
        """.trimIndent()
        db?.execSQL(query)
        Log.d("seoulconcerthalllist", "DBHelper.onCreate()")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = """
            drop table concertTBL
        """.trimIndent()
        db?.execSQL(query)
        this.onCreate(db)
    }

    fun selectConcertAll(): MutableList<Concert>? {
        var concertList: MutableList<Concert>? = mutableListOf<Concert>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            select * from concertTBL
        """.trimIndent()

        try{
            cursor = db.rawQuery(query, null)
            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val number = cursor.getString(0)
                    val name = cursor.getString(1)
                    val phone = cursor.getString(2)
                    val state = cursor.getString(3)
                    val address = cursor.getString(4)
                    val star = cursor.getInt(5)

                    val concert = Concert(number, name, phone, state, address, star)
                    concertList?.add(concert)
                    Log.d("seoulconcerthalllist", "selectConcertAll() success")
                }
            }else {
                concertList = null
            }
        }catch (e: Exception){
            Log.d("seoulconcerthalllist", "selectConcertAll() ${e.printStackTrace()}")
        }finally {
            cursor?.close()
            db.close()
        }
        return concertList
    }

    fun insertConcert(concert: Concert):Boolean {
        var flag = false
        val query = """
            insert into concertTBL (number, name, phone, state, address, star)
            values ('${concert.number}', '${concert.name}', '${concert.phone}', '${concert.state}', '${concert.address}', ${concert.star})
        """.trimIndent()
        val db = this.writableDatabase
        try{
            db.execSQL(query)
            flag = true
            Log.d("seoulconcerthalllist", "insertConcert() success")
        }catch (e: Exception){
            Log.d("seoulconcerthalllist", "insertConcert() ${e.printStackTrace()}")
            flag = false
        }finally{
            db.close()
        }
        return flag
    }

    fun updateStar(concert: Concert): Boolean {
        var flag = false
        val query = """
            update concertTBL set star = ${concert.star} where number = '${concert.number}'
        """.trimIndent()

        val db = this.writableDatabase

        try{
            db.execSQL(query)
            flag = true
            Log.d("seoulconcerthalllist", "DBHelper updateStar() concert ${concert.number}")
        }catch (e:Exception){
            Log.d("seoulconcerthalllist", "DBHelper updateStar() failed")
            flag = false
        }
        return flag
    }

    fun selectConcertStar(): MutableList<Concert>? {
        var concertList: MutableList<Concert>? = mutableListOf<Concert>()
        var cursor: Cursor? = null
        val query = """
            select * from concertTBL where star = 1
        """.trimIndent()
        val db = this.readableDatabase

        try{
            cursor = db.rawQuery(query, null)
            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val number = cursor.getString(0)
                    val name = cursor.getString(1)
                    val phone = cursor.getString(2)
                    val state = cursor.getString(3)
                    val address = cursor.getString(4)
                    val star = cursor.getInt(5)

                    val concert = Concert(number, name, phone, state, address, star)
                    concertList?.add(concert)
                }
            }else {
                concertList = null
            }
        }catch (e: Exception){
            Log.d("seoulconcerthalllist", "selectConcertAll() ${e.printStackTrace()}")
            concertList = null
        }finally {
            cursor?.close()
        }
        return concertList
    }

    fun searchConcert(query: String?): MutableList<Concert>? {
        var concertList: MutableList<Concert>? = mutableListOf<Concert>()
        var cursor: Cursor? = null
        val query = """
            select * from concertTBL where name like '${query}%'
        """.trimIndent()
        val db = this.readableDatabase

        try{
            cursor = db.rawQuery(query, null)
            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val number = cursor.getString(0)
                    val name = cursor.getString(1)
                    val phone = cursor.getString(2)
                    val state = cursor.getString(3)
                    val address = cursor.getString(4)
                    val star = cursor.getInt(5)

                    val concert = Concert(number, name, phone, state, address, star)
                    concertList?.add(concert)
                }
            }else {
                concertList = null
            }
        }catch (e: Exception){
            Log.d("seoulconcerthalllist", "selectConcertAll() ${e.printStackTrace()}")
            concertList = null
        }finally {
            cursor?.close()
        }
        return concertList
    }

    fun selectConcertRun(): MutableList<Concert>? {
        var concertList: MutableList<Concert>? = mutableListOf<Concert>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            select * from concertTBL
        """.trimIndent()

        try{
            cursor = db.rawQuery(query, null)
            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val number = cursor.getString(0)
                    val name = cursor.getString(1)
                    val phone = cursor.getString(2)
                    var state = cursor.getString(3)
                    val address = cursor.getString(4)
                    val star = cursor.getInt(5)

                    if(state.contains("영업") || state.contains("정상")) {
                        val concert = Concert(number, name, phone, state, address, star)
                        concertList?.add(concert)
                        Log.d("seoulconcerthalllist", "selectConcertRun() success")
                    }
                }
            }else {
                concertList = null
            }
        }catch (e: Exception){
            Log.d("seoulconcerthalllist", "selectConcertAll() ${e.printStackTrace()}")
        }finally {
            cursor?.close()
            db.close()
        }
        return concertList
    }

    fun selectConcertStop(): MutableList<Concert>? {
        var concertList: MutableList<Concert>? = mutableListOf<Concert>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            select * from concertTBL
        """.trimIndent()

        try{
            cursor = db.rawQuery(query, null)
            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val number = cursor.getString(0)
                    val name = cursor.getString(1)
                    val phone = cursor.getString(2)
                    var state = cursor.getString(3)
                    val address = cursor.getString(4)
                    val star = cursor.getInt(5)

                    if(!(state.contains("영업") || state.contains("정상"))) {
                        val concert = Concert(number, name, phone, state, address, star)
                        concertList?.add(concert)
                        Log.d("seoulconcerthalllist", "selectConcertStop() success")
                    }
                }
            }else {
                concertList = null
            }
        }catch (e: Exception){
            Log.d("seoulconcerthalllist", "selectConcertAll() ${e.printStackTrace()}")
        }finally {
            cursor?.close()
            db.close()
        }
        return concertList
    }
}