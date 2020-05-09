package com.sduduzog.slimlauncher.di.modules

import com.sduduzog.slimlauncher.HomeActivity
import com.sduduzog.slimlauncher.MainActivity
import com.sduduzog.slimlauncher.OptionsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector()
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun contributeHomeActivity(): HomeActivity

    @ContributesAndroidInjector()
    abstract fun contributeOptionsActivity(): OptionsActivity
}