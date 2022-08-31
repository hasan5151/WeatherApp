package com.huzi.shared.core

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.huzi.shared.R
import com.huzi.shared.common.simpleDialog
import com.huzi.shared.extensions.fetchRationale
import com.huzi.shared.extensions.hasPermissions
import com.huzi.shared.extensions.shouldRationale
import com.huzi.shared.main.MainViewModel
import com.huzi.shared.resource.ioDispatcher
import com.huzi.shared.result.Resource
import com.huzi.shared.result.ResultStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var bindingHolder: VB? = null
    protected val binding: VB get() = bindingHolder!!

    protected open val isResizable: Boolean = false

    /**Override it to 'true' for handle back press in fragment*/
    protected open val isBackHandle: Boolean = false

    /**Override it to create toolbar menu in fragment
     * Use menu resource id*/
    @MenuRes
    protected open val toolbarMenu: Int = -1

    protected val isAlive: Boolean
        get() = isVisible && !isRemoving && !isDetached

    companion object {
        private val permsLocation = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }
    private lateinit var locationCallback: LocationCallback

    protected val mainViewModel: MainViewModel by activityViewModels()

    private val backPressCallback: OnBackPressedCallback by lazy { createBackPressCallback() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bindingHolder = createBinding(inflater, container)
        setupInputMode()
        if (isBackHandle) {
            activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressCallback)
        }
        return binding.root
    }

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.setLoaderOwner(this)
        setupViews()
        setupObservers()
        setupResultObservers()
        setupResultListener()
        setupToolbarMenu()
    }

    private fun setupResultListener() {
        mainViewModel.isKeyboardOpen.observer{
            onKeyboardToggled(it)
        }
        mainViewModel.isGpsOn.observer {
            onGpsActive()
        }
    }

    @Suppress("DEPRECATION")
    private fun setupInputMode() {
        val stateHidden = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        val adjustResize = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        val adjustPan = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val currentMode = activity?.window?.attributes?.softInputMode
        val resizeMode = adjustResize or stateHidden
        val panMode = adjustPan or stateHidden
        if (isResizable) {
            if (currentMode != resizeMode) {
                activity?.window?.apply {
                    setSoftInputMode(resizeMode)
                }
            }
        } else {
            if (currentMode != panMode) {
                activity?.window?.apply {
                    setSoftInputMode(panMode)
                }
            }
        }
    }

    /**Override methods*/
    open fun setupViews() {}

    /**Setup toolbar menu for extra menu items and handle back press*/
    private fun setupToolbarMenu() {
        if (toolbarMenu == -1 && !isBackHandle) return
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (toolbarMenu != -1) {
                    menuInflater.inflate(toolbarMenu, menu)
                }
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return if (isBackHandle) {
                    if (menuItem.itemId == android.R.id.home) {
                        val isPrevent = onBackPress()
                        /**Not Prevent means default action
                         * go back and remove callback*/
                        if (!isPrevent) {
                            backPressCallback.isEnabled = false
                            backPressCallback.remove()
                        }
                        isPrevent
                    } else {
                        onToolbarItemSelected(menuItem)
                    }
                } else {
                    onToolbarItemSelected(menuItem)
                }
            }

            override fun onPrepareMenu(menu: Menu) {
                onToolbarMenuPrepare(menu)
            }
        }, viewLifecycleOwner)
    }

    open fun setupObservers() {}

    open fun setupResultObservers() {}

    open fun onToolbarItemSelected(item: MenuItem): Boolean {
        return false
    }

    open fun onToolbarMenuPrepare(menu: Menu) {

    }

    /**Helper methods*/
    protected fun processResult(res: Resource<Any>, showError: Boolean = true) {
        val showLoading = res.status == ResultStatus.LOADING
        mainViewModel.showLoadingAnim(showLoading)
        if (showError) {
            res.message?.let { showErrorMessage(it) }
        }
    }

    protected fun showLoading(show: Boolean) {
        mainViewModel.showLoadingAnim(show)
    }

    protected fun showMessage(@StringRes resId: Int?) {
        resId?.let {
            showMessage(getString(resId))
        }
    }

    protected fun showMessage(message: String?) {
        mainViewModel.showMessage(message)
    }

    protected fun showErrorMessage(@StringRes resId: Int?) {
        resId?.let {
            showErrorMessage(getString(it))
        }
    }

    protected fun showErrorMessage(message: String?) {
        mainViewModel.showErrorMessage(message)
    }

    protected fun setToolbarTitle(title: String?) {
        mainViewModel.setToolbarTitle(title)
    }

    override fun onDestroyView() {
        bindingHolder = null
        if (isBackHandle) {
            backPressCallback.isEnabled = false
            backPressCallback.remove()
        }
        super.onDestroyView()
    }

    /**Keyboard open close callback*/
    private fun createBackPressCallback(): OnBackPressedCallback {
        return object : OnBackPressedCallback(isBackHandle) {
            override fun handleOnBackPressed() {
                val isPrevent = onBackPress()
                /**Not Prevent means default action
                 * go back and remove callback*/
                if (!isPrevent) {
                    this.isEnabled = false
                    this.remove()
                    findNavController().popBackStack()
                }
            }
        }
    }

    /**Call back methods*/
    open fun onKeyboardToggled(isOpen: Boolean) {}

    fun onGpsActive() {
        lifecycleScope.launch {
            locationUpdate()
        }
    }

    open fun fetchLocation(location: Location){}

    @SuppressLint("MissingPermission")
    suspend fun locationUpdate() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                val location: Location = p0.lastLocation ?: return
                fetchLocation(location)
                fusedLocationClient.removeLocationUpdates(locationCallback) //get only 1 time location :)
            }
        }
      //  withContext(ioDispatcher) {
            val lastLocation = fusedLocationClient.lastLocation.await()
            lastLocation?.let {
                fetchLocation(it)
            } ?: apply {
                fusedLocationClient.requestLocationUpdates(
                    getLocationRequest(),
                    locationCallback,
                    Looper.myLooper()
                )
            }
        //}
    }

    private fun getLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            this.interval = 5000
            this.fastestInterval = 2000
            this.priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }

    /** Back press related stuff
     * Return false for default action
     * */
    open fun onBackPress(): Boolean = false

    /**Call this method to check GPS permissions and settings*/
    protected fun activateGps() {
        when {
            hasPermissions(*permsLocation) -> {
                setupGpsSettings()
            }
            shouldRationale(*permsLocation) -> {
                val dialog = simpleDialog(context) {
                    messageRes = R.string.perms_gps_required
                    onConfirm = {
                        val perms = fetchRationale(*permsLocation)
                        requestLocationPermissions.launch(perms)
                    }
                }
                dialog?.show()
            }
            else -> requestLocationPermissions.launch(permsLocation)
        }
    }

    private fun setupGpsSettings() = lifecycleScope.launch {
        val locationRequest = getLocationRequest()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
        val settingsResponse = try {
            withContext(ioDispatcher) {
                client.checkLocationSettings(builder.build()).await()
            }
        } catch (ex: Exception) {
            if (ex is ResolvableApiException) {
                val sender = ex.resolution.intentSender
                val senderRequest = IntentSenderRequest.Builder(sender).build()
                requestGpsSettings.launch(senderRequest)
            } else {
                showErrorMessage(R.string.error_gps)
            }
            null
        }
        if (settingsResponse != null) {
            onGpsActive()
        }
    }

    private val requestLocationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var isPermissionsGraded = true
        for (entry in permissions.entries) {
            if (!entry.value) {
                isPermissionsGraded = false
                break
            }
        }
        if (isPermissionsGraded) {
            setupGpsSettings()
        } else {
            showErrorMessage(R.string.error_gps)
        }
    }

    private val requestGpsSettings = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            onGpsActive()
        } else {
            showErrorMessage(R.string.error_gps)
        }
    }

    fun <T> LiveData<T>.observer(observer: Observer<T>) {
        observe(viewLifecycleOwner) {
            when (it) {
                is Resource<*> -> {
                    onLoad(it.status == ResultStatus.LOADING)
                    when (it.status) {
                        ResultStatus.SUCCESS -> {
                            observer.onChanged(it)
                        }

                        ResultStatus.ERROR -> {
                            it.message?.let {
                                onError(it)
                            }
                        }
                        else->{}

                    }
                }

                else -> observer.onChanged(it)
            }
        }
    }

    fun <T> LiveData<Resource<T>>.onObserver(observer: Observer<T>) {
        observe(viewLifecycleOwner) {
            onLoad(it.status == ResultStatus.LOADING)
            when (it.status) {
                ResultStatus.SUCCESS -> {
                    it.data.let {
                        observer.onChanged(it)
                    }
                }

                ResultStatus.ERROR -> {
                    it.message?.let {
                        onError(it)
                    }
                    it.data?.let{
                        observer.onChanged(it)
                    }

                }

                else->{}

            }
        }
    }

    fun <T> Flow<T>.collector(collector: FlowCollector<T>) =
        lifecycleScope.launch {
            collect {
                when (it) {
                    is Resource<*> -> {
                        onLoad(it.status == ResultStatus.LOADING)
                        when (it.status) {
                            ResultStatus.SUCCESS -> {
                                collector.emit(it)
                            }

                            ResultStatus.ERROR -> {
                                it.message?.let {
                                    onError(it)
                                }
                            }
                            else->{}

                        }
                    }
                    else -> {
                        collector.emit(it)
                    }
                }
            }
        }

    fun <T> Flow<Resource<T>?>.onCollector(isLoadingAvailable: Boolean =true,collector: FlowCollector<T>) =
        lifecycleScope.launch {
            collect {
                if (isLoadingAvailable)
                onLoad(it?.status == ResultStatus.LOADING)
                when (it?.status) {
                    ResultStatus.SUCCESS -> {
                        it.data?.let {
                            collector.emit(it)
                        }
                    }

                    ResultStatus.ERROR -> {
                        it.message?.let {test->
                            onError(test)
                        }

                        it.data?.let{
                            collector.emit(it)
                        }
                    }

                    else->{}
                }
            }
        }

    open fun onError(it: String){
        mainViewModel.showErrorMessage(it)
    }

    open fun onLoad(isLoading: Boolean) {
        mainViewModel.showLoadingAnim(isLoading)
    }
}