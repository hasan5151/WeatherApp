package com.huzi.forecast.ui.launch

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.huzi.forecast.adapters.WeatherPagerAdapter
import com.huzi.forecast.databinding.FragmentLaunchBinding
import com.huzi.forecast.utils.SharedPrefs
import com.huzi.shared.core.BaseFragment
import com.huzi.shared.extensions.dismissKeyboard
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LaunchFragment : BaseFragment<FragmentLaunchBinding>() {
    private val launchViewModel: LaunchViewModel by activityViewModels()
    private val weatherAdapter: WeatherPagerAdapter by lazy { WeatherPagerAdapter(this) }

    @Inject
    lateinit var prefs: SharedPrefs

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLaunchBinding = FragmentLaunchBinding.inflate(inflater, container, false)

    override fun setupViews() = with(binding) {
        setupViewpager()
        activateGps()
        fetchData()
    }

    private fun fetchData() {
        if (!prefs.isGpsSelected){
            launchViewModel.setSelectedCityId(prefs.selectedCityId)
        }
    }

    override fun fetchLocation(location: Location) {
        if (prefs.isFirstOpen) {
            launchViewModel.setLocation(location)
            prefs.isFirstOpen = false
        }else if (prefs.isGpsSelected){
            launchViewModel.setLocation(location)
        }
        launchViewModel.recordLocation(location)
    }

    private fun setupViewpager() = with(binding) {
        viewPager.adapter = weatherAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position -> }.attach()
        viewPager.currentItem = 1
        viewPager.offscreenPageLimit = 2
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dismissKeyboard(binding.root)
            }
        })
    }
}