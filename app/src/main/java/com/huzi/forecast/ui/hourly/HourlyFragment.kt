package com.huzi.forecast.ui.hourly

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.huzi.forecast.adapters.HourlyAdapter
import com.huzi.forecast.databinding.FragmentHourlyBinding
import com.huzi.forecast.ui.launch.LaunchViewModel
import com.huzi.shared.core.BaseFragment

class HourlyFragment : BaseFragment<FragmentHourlyBinding>() {

    private val launchViewModel: LaunchViewModel by activityViewModels()
    private val adapter = HourlyAdapter()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHourlyBinding = FragmentHourlyBinding.inflate(inflater,container,false)

    override fun setupObservers() {
        launchViewModel.hourly.onCollector{
            adapter.submitList(it)
        }
        super.setupObservers()
    }

    override fun setupViews() = with(binding) {
        recyclerView.adapter = adapter
    }

}