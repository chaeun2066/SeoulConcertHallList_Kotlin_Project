package com.example.seoulconcerthalllist

import com.example.seoulconcerthalllist.data.ConcertHall
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

//http://openapi.seoul.go.kr:8088/6d6d4a6a75636f643939626862426e/json/LOCALDATA_030601/1/5/

class SeoulConcertHallOpenApi {

    companion object{
        const val DOMAIN = "http://openapi.seoul.go.kr:8088/"
        const val API_KEY = "6d6d4a6a75636f643939626862426e"
        const val LIMIT = 100
    }
}

interface SeoulConcertOpenService{
    @GET("{api_key}/json/LOCALDATA_030601/1/{end}")
    fun getConcertHall(@Path("api_key") key: String, @Path("end") limit : Int): Call<ConcertHall>
}