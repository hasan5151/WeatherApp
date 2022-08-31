package com.huzi.forecast.ui.main

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.huzi.forecast.R
import com.huzi.forecast.databinding.ActivityMainBinding
import com.huzi.shared.extensions.fetchColor
import com.huzi.shared.extensions.getMeasurements
import com.huzi.shared.main.MainViewModel
import com.huzi.shared.models.SnackbarMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val keyboardListener: ViewTreeObserver.OnGlobalLayoutListener by lazy { createKeyboardListener() }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private val viewModel: MainViewModel by viewModels()
    companion object{
        const val REQUEST_GPS_SETTINGS = 7235
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupNavigation()
        setupObservers()
    }

    private fun setupNavigation() = with(binding) {
        val hostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.main_host_fragment) as NavHostFragment? ?: return
        navController = hostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setSupportActionBar(mainToolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, destination, _ ->
           // changeToolbarState(true)
        }
    }

    private fun changeToolbarState(show: Boolean) = with(binding) {
        if (mainToolbar.isVisible == show) return@with
        val measuredHeight = mainToolbar.getMeasurements(root).second
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener {
            if (show && !mainToolbar.isVisible && it.animatedFraction > 0.05) {
                mainToolbar.isVisible = true
            }
            if (!show && mainToolbar.isVisible && it.animatedFraction < 0.05) {
                mainToolbar.isVisible = false
            }
            val height = (it.animatedFraction * measuredHeight).toInt()
            if (mainToolbar.layoutParams.height != height) {
                mainToolbar.layoutParams.height = height
                mainToolbar.requestLayout()
            }
        }
        animator.duration = 200
        animator.interpolator = DecelerateInterpolator()
        root.post {
            if (show) {
                animator.start()
            } else {
                animator.reverse()
            }
        }
    }

    private fun createKeyboardListener(): ViewTreeObserver.OnGlobalLayoutListener {
        val initialBounds = Rect()
        binding.root.getWindowVisibleDisplayFrame(initialBounds)
        val initialHeight = initialBounds.height()
        return ViewTreeObserver.OnGlobalLayoutListener {
            val visibleBounds = Rect()
            binding.root.getWindowVisibleDisplayFrame(visibleBounds)
            val visibleHeight = visibleBounds.height()
            val heightDiff = initialHeight - visibleHeight
            val minDiff = initialHeight * 0.15f
            if (heightDiff > minDiff) {
                viewModel.setKeyboardOpen(true)
            } else {
                viewModel.setKeyboardOpen(false)
            }
        }
    }

    private fun setupObservers() {
        viewModel.loadingAnim.observe(this) { show: Boolean ->
            showLoading(show)
        }
        viewModel.message.observe(this) {
            showSnackbar(it)
        }
        viewModel.toolbarTitle.observe(this) {
            supportActionBar?.title = it
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showLoading(show: Boolean) = with(binding) {
        if (loadingOverlay.isVisible == show) return@with

        val values: FloatArray = if (show) {
            floatArrayOf(0f, 1f)
        } else {
            floatArrayOf(1f, 0f)
        }
        val overlayAnimator = ObjectAnimator.ofFloat(loadingOverlay, "alpha", *values)
        overlayAnimator.addListener(
            onStart = {
                if (show) {
                    loadingOverlay.isVisible = true
                }
            },
            onEnd = {
                loadingOverlay.isVisible = show
            }
        )
        overlayAnimator.start()
        loadingIndicator.isVisible = show
    }

    private fun showSnackbar(snackbarMessage: SnackbarMessage) {
        val msg = snackbarMessage.message ?: return

        val snackbar = Snackbar.make(binding.mainRootView, msg, snackbarMessage.length)
        snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE

        if (snackbarMessage.isError) {
            val view = snackbar.view
            view.setBackgroundResource(R.drawable.bg_snackbar)
            snackbar.setTextColor(fetchColor(com.huzi.shared.R.color.textColorPrimary))
        }

        if (snackbarMessage.length == Snackbar.LENGTH_INDEFINITE) {
            /*try {
                val action = snackbar.view.findViewById<Button>(com.google.android.material.R.id.snackbar_action)
                val params = action.layoutParams as LinearLayout.LayoutParams
                params.gravity = Gravity.BOTTOM or Gravity.END
                params.bottomMargin = 16
            } catch (ex: Exception) {
                Log.e("MainActivity", "showSnackbar Exception:", ex)
            }*/
            snackbar.setAction(com.huzi.shared.R.string.close) {
                snackbar.dismiss()
            }
        }
        snackbar.show()
    }

    override fun onResume() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
        super.onResume()
    }

    override fun onPause() {
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(keyboardListener)
        super.onPause()
    }

    @Suppress("Deprecation")
    @Deprecated("Anyway")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_GPS_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.isGpsOn.postValue(true)
            } else {
                viewModel.showErrorMessage(getString(R.string.perms_gps_required))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}