package com.speakerboxlite.router

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

interface BaseHostView
{
    var routerManager: RouterManager
    var router: Router
}

class ActivityLifeCycle(val routerManager: RouterManager,
                        val hostActivityFactory: HostActivityFactory): Application.ActivityLifecycleCallbacks
{
    override fun onActivityCreated(p0: Activity, p1: Bundle?)
    {
        if (p0 is BaseHostView)
        {
            p0.routerManager = routerManager
            p0.router = routerManager[p0.hostActivityKey]
        }

        if (p0 is AppCompatActivity)
        {
            p0.supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentLifeCycle(routerManager, hostActivityFactory), true)
        }
    }

    override fun onActivityStarted(p0: Activity)
    {

    }

    override fun onActivityResumed(p0: Activity)
    {
        if (p0 is BaseHostView)
        {
            routerManager.top = p0.router
            if (p0 is AppCompatActivity)
                p0.router.bindExecutor(CommandExecutorAndroid(p0, R.id.root, p0.supportFragmentManager, hostActivityFactory))
        }
    }

    override fun onActivityPaused(p0: Activity)
    {
        if (p0 is BaseHostView)
        {
            p0.router.unbindExecutor()
        }
    }

    override fun onActivityStopped(p0: Activity)
    {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle)
    {

    }

    override fun onActivityDestroyed(p0: Activity)
    {

    }
}