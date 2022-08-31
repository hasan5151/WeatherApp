package com.huzi.forecast.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huzi.forecast.databinding.AdapterCityListBinding
import com.huzi.forecast.db.entity.CityList

class CityListAdapter : ListAdapter<CityList, CityListAdapter.CityVH>(diffCallback) {

    var selectListener : ((item: CityList) ->Unit)?= null
    fun onSelectListener( listener : (item: CityList)->Unit){
        selectListener = listener
    }

    var deleteListener : ((item: CityList) ->Unit)?= null
    fun onDeleteListener( listener : (item: CityList)->Unit){
        deleteListener = listener
    }

    inner class CityVH(private val itemBinding: AdapterCityListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: CityList) = with(itemBinding) {
            card.strokeWidth = if (item.isSelected) 2 else 0
            card.setOnClickListener {
                selectListener?.invoke(item)
            }

            delete.setOnClickListener {
                deleteListener?.invoke(item)
            }

            cityName.text = item.name
            gpsIcon.isVisible = item.isGPS
            deleteGroup.isVisible = !item.isGPS
        }
    }

    companion object{
        val diffCallback = object : DiffUtil.ItemCallback<CityList>() {
            override fun areItemsTheSame(oldItem: CityList, newItem: CityList): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CityList, newItem: CityList): Boolean {
                 return  oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterCityListBinding.inflate(layoutInflater, parent, false)
        return CityVH(binding)
    }

    override fun onBindViewHolder(holder: CityVH, position: Int) = holder.bind(getItem(position))

}