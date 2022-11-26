package com.example.seoulconcerthalllist

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.seoulconcerthalllist.databinding.ItemMainBinding

class ConcertAdapter(val context: Context, val concertList: MutableList<Concert>?)
    :RecyclerView.Adapter<ConcertAdapter.ConcertViewHolder>() {

    override fun getItemCount(): Int {
        return concertList?.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConcertViewHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ConcertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConcertViewHolder, position: Int) {
        val binding = (holder as ConcertViewHolder).binding
        val concert = concertList?.get(position)

        binding.tvName.isSelected = true
        binding.tvState.isSelected = true
        binding.tvAddr.isSelected = true

        binding.ivConcertIcon.setImageResource(R.drawable.concert_icon)
        binding.tvName.text = concert?.name
        binding.tvState.text = concert?.state
        binding.tvTell.text = concert?.phone
        binding.tvAddr.text = concert?.address
        binding.ivSearch.setImageResource(R.drawable.ic_search_24)

        when(concert?.star){
            0 -> {binding.ivStar.setImageResource(R.drawable.ic_star_outline_24)}
            1 -> {binding.ivStar.setImageResource(R.drawable.ic_star_24)}
        }

        binding.ivSearch.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_WEB_SEARCH
            intent.putExtra(SearchManager.QUERY, binding.tvName.text.toString() + " 공연장")
            binding.root.context.startActivity(intent)
        }

        binding.ivStar.setOnClickListener {
            if(concert?.star == 0){
                binding.ivStar.setImageResource(R.drawable.ic_star_outline_24)
                concert?.star = 1
            }else {
                binding.ivStar.setImageResource(R.drawable.ic_star_24)
                concert?.star = 0
            }

            if(concert != null){
                val dbHelper = DBHelper(context, MainActivity.DB_NAME, MainActivity.VERSION)
                val flag = dbHelper.updateStar(concert)
                if(flag == false){
                    Log.d("seoulconcerthalllist", "ConcertAdapter.onBindViewHolder() : Update failed ${concert.toString()}")
                }else {
                    notifyDataSetChanged()
                }
            }
        }

        holder.itemView.setOnLongClickListener {
            if(binding.tvTell.text.toString().length != 0) {
                (context as MainActivity).callRestaurant(binding.tvTell.text.toString())
            }else {
                Toast.makeText(context, "번호가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
    class ConcertViewHolder(val binding: ItemMainBinding):RecyclerView.ViewHolder(binding.root)
}