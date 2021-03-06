package com.innov8.memeit.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.innov8.memeit.R

class CoverBehaviour : CoordinatorLayout.Behavior<View> {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val height = R.dimen.profile_cover_expanded_height.dimen()
    private val expandedY = 0
    private val collapsedY = R.dimen.profile_toolbar_height.dimen() - height
    private val yDiff = expandedY - collapsedY

    private var appBarStartingHeight = 0
    private val appBarMinHeight = R.dimen.profile_toolbar_height.dimen() +
            R.dimen.profile_tab_height.dimen()
    private val appBarDiff get() = appBarStartingHeight - appBarMinHeight
    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            if (appBarStartingHeight == 0) appBarStartingHeight = dependency.height
            val appBarCurrentDiff = dependency.bottom - appBarMinHeight
            val appBarRatio = appBarCurrentDiff / appBarDiff
            child.y = collapsedY + (appBarRatio * yDiff)
        }

        return false
    }
}