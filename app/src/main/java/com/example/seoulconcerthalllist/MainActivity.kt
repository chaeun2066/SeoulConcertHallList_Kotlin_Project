package com.example.seoulconcerthalllist

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seoulconcerthalllist.data.ConcertHall
import com.example.seoulconcerthalllist.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    companion object{
        const val DB_NAME = "concertDB"
        const val VERSION = 1
    }

    lateinit var binding: ActivityMainBinding
    lateinit var name: String
    lateinit var state: String
    lateinit var phone : String
    lateinit var address : String
    lateinit var number : String
    var star : Int = 0
    lateinit var concertAdapter: ConcertAdapter
    private var concertList: MutableList<Concert>? = mutableListOf<Concert>()
    private var getConcert = mutableListOf<Concert>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = DBHelper(this@MainActivity, DB_NAME, VERSION)
        concertList = dbHelper.selectConcertAll()

        if(concertList == null || concertList?.size == 0){
            val retrofit = Retrofit.Builder()
                .baseUrl(SeoulConcertHallOpenApi.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(SeoulConcertOpenService::class.java)

            service.getConcertHall(SeoulConcertHallOpenApi.API_KEY, SeoulConcertHallOpenApi.LIMIT)
                .enqueue(object : Callback<ConcertHall>{
                    override fun onResponse(call: Call<ConcertHall>, response: Response<ConcertHall>) {
                        val data = response.body()
                        data?.let{
                            for (concert in it.LOCALDATA_030601.row) {
                                number = concert.MGTNO
                                name = concert.BPLCNM
                                state = concert.TRDSTATENM
                                phone = concert.SITETEL
                                address = concert.RDNWHLADDR
                                Log.d("seoulconcerthalllist", "${name} ${state}")
                                val getConcertData = Concert(number, name, phone, state, address, star)
                                getConcert.add(getConcertData)
                            }
                            if(getConcert.size != 0){
                                for(i in 0 until getConcert.size - 1){
                                    val concert = getConcert.get(i)
                                    dbHelper.insertConcert(concert)
                                }
                                concertList = getConcert
                            }else {
                                Log.d("seoulconcerthalllist", "MainActivity getConcert Data is Empty")
                            }
                            Log.d("seoulconcerthalllist", "concertList ${concertList?.size}")
                            concertAdapter = ConcertAdapter(this@MainActivity, concertList)
                            binding.recylerview.adapter = concertAdapter
                            binding.recylerview.setHasFixedSize(true)
                            binding.recylerview.layoutManager = LinearLayoutManager(this@MainActivity)
                        }?: let{
                            Log.d("seoulconcerthalllist", "Concert Hall List is Empty")
                            Toast.makeText(this@MainActivity,"InformationCenter Data is empty",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ConcertHall>, t: Throwable) {
                        Log.d("seoulconcerthalllist", "Concert Hall List Load Error ${t.toString()}")
                        Toast.makeText(this@MainActivity,"InformationCenter Load Error",Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            concertAdapter = ConcertAdapter(this@MainActivity, concertList)
            binding.recylerview.adapter = concertAdapter
            binding.recylerview.setHasFixedSize(true)
            binding.recylerview.layoutManager = LinearLayoutManager(this@MainActivity)
        }

        setSupportActionBar(binding.toolbar)

        val spinner_List = listOf("전체","정상영업", "영업중지")
        val spinner_adapter = ArrayAdapter(this, R.layout.spinner_item_layout, spinner_List)
        binding.spState.adapter = spinner_adapter

        binding.spState.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 1){
                    concertList?.clear()
                    dbHelper.selectConcertRun()?.let{concertList?.addAll(it)}
                    concertAdapter.notifyDataSetChanged()
                }else if(position == 2){
                    concertList?.clear()
                    dbHelper.selectConcertStop()?.let{concertList?.addAll(it)}
                    concertAdapter.notifyDataSetChanged()
                }else {
                    concertList?.clear()
                    dbHelper.selectConcertAll()?.let{concertList?.addAll(it)}
                    concertAdapter.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val dbHelper = DBHelper(this@MainActivity, DB_NAME, VERSION)
        when(item.itemId){
            R.id.menu_star -> {
                concertList?.clear()
                dbHelper.selectConcertStar()?.let{concertList?.addAll(it)}
                concertAdapter.notifyDataSetChanged()
                binding.spState.visibility = View.GONE
            }
            R.id.menu_list -> {
                concertList?.clear()
                dbHelper.selectConcertAll()?.let{concertList?.addAll(it)}
                concertAdapter.notifyDataSetChanged()
                binding.spState.visibility = View.VISIBLE
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchMenu = menu?.findItem(R.id.menu_search)
        val searchView = searchMenu?.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                val dbHelper = DBHelper(this@MainActivity, DB_NAME, VERSION)
                if(query.isNullOrBlank()){
                    concertList?.clear()
                    dbHelper.selectConcertAll()?.let{concertList?.addAll(it)}
                    concertAdapter.notifyDataSetChanged()
                }else{
                    concertList?.clear()
                    dbHelper.searchConcert(query)?.let{concertList?.addAll(it)}
                    concertAdapter.notifyDataSetChanged()
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    fun callRestaurant(phone: String) {
        val myUri = Uri.parse("tel:${phone}")
        val intent = Intent(Intent.ACTION_DIAL, myUri)
        startActivity(intent)
    }
}