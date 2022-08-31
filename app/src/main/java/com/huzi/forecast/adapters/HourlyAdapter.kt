package com.huzi.forecast.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huzi.forecast.databinding.AdapterHourlyBinding
import com.huzi.forecast.db.entity.HourlyWeather
import com.huzi.shared.extensions.setImageFromString

class HourlyAdapter : ListAdapter<HourlyWeather, HourlyAdapter.HourlyVH>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<HourlyWeather>() {
            override fun areItemsTheSame(
                oldItem: HourlyWeather,
                newItem: HourlyWeather
            ): Boolean {
                return oldItem.time == newItem.time
            }

            override fun areContentsTheSame(
                oldItem: HourlyWeather,
                newItem: HourlyWeather
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class HourlyVH(private val itemBinding: AdapterHourlyBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: HourlyWeather) = with(itemBinding) {
            degree.text = "${item.temp.toDouble().toInt()} °"
            img.setImageFromString(item.icon)
            time.text = item.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterHourlyBinding.inflate(layoutInflater, parent, false)
        return HourlyVH(binding)
    }

    override fun onBindViewHolder(holder: HourlyVH, position: Int)  = holder.bind(getItem(position))
}