package com.sduduzog.slimlauncher.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sduduzog.slimlauncher.data.BaseDao
import com.sduduzog.slimlauncher.models.HomeApp
import com.sduduzog.slimlauncher.models.Repository
import javax.inject.Inject

class HomeViewModel @Inject constructor(baseDao: BaseDao) : ViewModel() {
    private val _baseRepository = Repository(baseDao)
    private var _apps: LiveData<List<HomeApp>>

    init {
        _apps = _baseRepository.apps
    }

    val apps: LiveData<List<HomeApp>>
        get() = _apps
}