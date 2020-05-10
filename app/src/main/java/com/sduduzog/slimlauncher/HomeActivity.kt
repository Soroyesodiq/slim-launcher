package com.sduduzog.slimlauncher

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sduduzog.slimlauncher.adapters.HomeAdapter
import com.sduduzog.slimlauncher.models.HomeApp
import com.sduduzog.slimlauncher.utils.OnLaunchAppListener
import com.sduduzog.slimlauncher.viewmodels.HomeViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class HomeActivity : BaseActivity() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var receiver: BroadcastReceiver
    lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        receiver = ClockReceiver()
        registerReceiver(receiver, IntentFilter(Intent.ACTION_TIME_TICK))
        updateClock()

        val appListener = AppLauncher()
        val adapter1 = HomeAdapter(appListener)
        val adapter2 = HomeAdapter(appListener)

        home_list.adapter = adapter1
        home_list_extra.adapter = adapter2
        home_more.setOnClickListener {
            activity_home.transitionToState(R.id.active_scene)
        }

        home_close.setOnClickListener {
            activity_home.transitionToState(R.id.minimal_scene)
        }

        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        viewModel.apps.observe(this, Observer { list ->
            list?.let { apps ->
                adapter1.setItems(apps.filter {
                    it.sortingIndex < 5
                })
                adapter2.setItems(apps.filter {
                    it.sortingIndex >= 5
                })
            }
        })
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        val settings = getSharedPreferences(getString(R.string.prefs_settings), MODE_PRIVATE)
        val currentId = settings.getInt(getString(R.string.prefs_settings_key_theme), 0)

        val resolvedId = resolveTheme(currentId)
        val res = packageManager.getActivityInfo(componentName, 0).themeResource
        Log.d("foo", "resuming: $res and should be same as $resolvedId")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }


    override fun onBackPressed() {
        // super.onBackPressed()
    }


    fun onMore(v: View) {
        val i = Intent(this, OptionsActivity::class.java)
        startActivity(i)
    }

    fun updateClock() {
        val twenty4Hour = getSharedPreferences(getString(R.string.prefs_settings), Context.MODE_PRIVATE)
                ?.getBoolean(getString(R.string.prefs_settings_key_time_format), true)
        val date = Date()
        if (twenty4Hour as Boolean) {
            val fWatchTime = SimpleDateFormat("h:mm aa", Locale.ROOT)
            home_time.text = fWatchTime.format(date)
        } else {
            val fWatchTime = SimpleDateFormat("H:mm", Locale.ROOT)
            home_time.text = fWatchTime.format(date)
        }
        val fWatchDate = SimpleDateFormat("EEE, MMM dd", Locale.ROOT)
        home_date.text = fWatchDate.format(date)
    }

    inner class ClockReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateClock()
        }
    }

    inner class AppLauncher : OnLaunchAppListener {
        override fun onLaunch(app: HomeApp, view: View) {
            try {
                val intent = Intent()
                val name = ComponentName(app.packageName, app.activityName)
                intent.action = Intent.ACTION_MAIN
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                intent.component = name
                intent.resolveActivity(packageManager)?.let {
                    launchActivity(view, intent)
                }
            } catch (e: Exception) {
                // Do no shit yet
            }
        }
    }

}
