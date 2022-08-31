package com.huzi.forecast.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huzi.forecast.databinding.AdapterSearchBinding
import com.huzi.forecast.models.SearchCityDTO

class SearchAdapter  :ListAdapter<SearchCityDTO, SearchAdapter.SearchVH>(diffCallback) {

    var selectListener : ((item: SearchCityDTO) ->Unit)?= null
    fun onSelectListener( listener : (item: SearchCityDTO)->Unit){
        selectListener = listener
    }

    inner class SearchVH(private val itemBinding: AdapterSearchBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: SearchCityDTO) = with(itemBinding) {
            cityName.text = "${item.name} | ${item.country}"
            itemBinding.root.setOnClickListener {
                selectListener?.invoke(item)
            }
        }
    }

    companion object{
        val diffCallback = object : DiffUtil.ItemCallback<SearchCityDTO>() {
            override fun areItemsTheSame(oldItem: SearchCityDTO, newItem: SearchCityDTO): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SearchCityDTO, newItem: SearchCityDTO): Boolean {
                return  oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: SearchVH, position: Int) = holder.bind(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterSearchBinding.inflate(layoutInflater, parent, false)
        return SearchVH(binding)    }

}