package com.huzi.forecast.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.huzi.forecast.ui.hourly.HourlyFragment
import com.huzi.forecast.ui.search.SearchFragment
import com.huzi.forecast.ui.today.TodayFragment

class WeatherPagerAdapter(fm : Fragment) : FragmentStateAdapter(fm) {
    private val fragments = listOf(SearchFragment(),TodayFragment(),HourlyFragment())
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}
