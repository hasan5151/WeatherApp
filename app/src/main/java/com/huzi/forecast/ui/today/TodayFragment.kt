package com.huzi.forecast.ui.today

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.huzi.forecast.R
import com.huzi.forecast.databinding.FragmentTodayBinding
import com.huzi.forecast.ui.launch.LaunchViewModel
import com.huzi.shared.core.BaseFragment
import com.huzi.shared.extensions.setImageFromString

class TodayFragment: BaseFragment<FragmentTodayBinding>() {

    private val launchViewModel: LaunchViewModel by activityViewModels()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTodayBinding = FragmentTodayBinding.inflate(inflater, container, false)

    override fun setupObservers() = with(binding) {
        launchViewModel.current.onCollector {
            degree.text = "${it.temp.toDouble().toInt()} °"
            weatherIcon.setImageFromString(it.icon)
            feelsLike.text = getString(R.string.feels_like, it.feelsLike.toDouble().toInt())
        }

        launchViewModel.selectedCity.collector {
            it?.name?.let {
                mainViewModel.setToolbarTitle(it)
            }
        }
        super.setupObservers()
    }
}