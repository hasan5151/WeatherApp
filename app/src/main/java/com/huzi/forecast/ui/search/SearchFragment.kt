package com.huzi.forecast.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.huzi.forecast.R
import com.huzi.forecast.adapters.CityListAdapter
import com.huzi.forecast.adapters.SearchAdapter
import com.huzi.forecast.databinding.FragmentSearchBinding
import com.huzi.forecast.models.SearchCityDTO
import com.huzi.forecast.ui.launch.LaunchViewModel
import com.huzi.forecast.utils.SharedPrefs
import com.huzi.shared.common.simpleDialog
import com.huzi.shared.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private val launchViewModel: LaunchViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val cityListAdapter = CityListAdapter()
    private val searchAdapter = SearchAdapter()

    @Inject
    lateinit var prefs: SharedPrefs

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false)

    override fun setupObservers()  = with(binding) {
        launchViewModel.cityList.collector {
            cityListAdapter.submitList(it)
        }

        searchViewModel.search.onCollector(false) {
            searchAdapter.submitList(it)
        }

        searchViewModel.isSearchRvVisible.collector{
            searchRv.isVisible = it
        }
        super.setupObservers()
    }

    override fun setupViews() = with(binding) {
        setupCityListRecyclerView()
        setupSearchRecyclerView()
        searchInput.doAfterTextChanged {
            searchViewModel.onSearch(it)
        }
        super.setupViews()
    }

    private fun setupSearchRecyclerView() = with(binding) {
        searchRv.adapter = searchAdapter
        searchAdapter.onSelectListener {
            showDialog(it)
        }
    }

    private fun showDialog(it: SearchCityDTO) = with(binding){
        simpleDialog(requireContext()) {
            this.title = getString(R.string.do_you_want_to_add)
            this.onConfirm = {
                searchViewModel.insertCity(it)
                searchViewModel.setSearchRvVisibility(false)
                launchViewModel.setSelectedCityId(it.id.toString())
                searchInput.setText("")
                prefs.selectedCityId = it.id.toString()
                prefs.isGpsSelected = false
            }
        }?.show()
    }

    private fun setupCityListRecyclerView() = with(binding) {
        cityRv.adapter = cityListAdapter
        cityListAdapter.onSelectListener {
            searchViewModel.selectCity(it)
            launchViewModel.setSelectedCityId(it.id.toString())
            prefs.isGpsSelected = it.isGPS
            prefs.selectedCityId = it.id.toString()
        }

        cityListAdapter.onDeleteListener {
            searchViewModel.deleteCity(it)
            if (it.isSelected){
                launchViewModel.updateLocation()
            }
        }
    }
}