package com.huzi.forecast.utils

import android.content.SharedPreferences
import com.huzi.forecast.constants.ISFIRSTOPEN
import com.huzi.forecast.constants.ISGPSSELECTED
import com.huzi.forecast.constants.SELECTEDCITYID
import com.huzi.shared.util.BooleanPrefs
import com.huzi.shared.util.PrefsImpl
import com.huzi.shared.util.StringPrefs

class SharedPrefs(override val sharedPreferences: SharedPreferences): PrefsImpl {
    var isFirstOpen by BooleanPrefs(ISFIRSTOPEN,true)
    var isGpsSelected by BooleanPrefs(ISGPSSELECTED,true)
    var selectedCityId by StringPrefs(SELECTEDCITYID,null)
}