package com.innov8.memeit.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.innov8.memeit.BuildConfig
import com.innov8.memeit.R
import com.innov8.memeit.adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.commons.models.TypefaceManager
import com.innov8.memeit.customViews.DrawableBadge
import com.innov8.memeit.fragments.MemeListFragment
import com.innov8.memeit.fragments.ProfileFragment
import com.innov8.memeit.loaders.FavoriteMemeLoader
import com.innov8.memeit.loaders.HomeMemeLoader2
import com.innov8.memeit.loaders.TrendingMemeLoader
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.activity_main.*
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import io.fabric.sdk.android.services.settings.IconRequest.build


abstract class MainActivity : AppCompatActivity() {
    private val titles = arrayOf("MemeIt", "Trending", "Favorites")
    private var searchItem: MenuItem? = null
    private var notifItem: MenuItem? = null
    private val pagerAdapter by lazy {
        MyPagerAdapter(supportFragmentManager)
    }
    private lateinit var mDrawerLayout: DrawerLayout

    companion object {
        fun start(context: Context, apply: Intent.() -> Unit = {}) {
            val name = "com.innov8.memeit.${BuildConfig.FLAVOR}.MainActivity"
            context.startActivity(Intent(context, Class.forName(name)).apply {
                this.apply()
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MemeItClient.Auth.isSignedIn()) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        } else if (!MemeItClient.Auth.isUserDataSaved()) {
            startActivity(Intent(this, AuthActivity::class.java).apply {
                putExtra(AuthActivity.STARTING_MODE_PARAM, AuthActivity.MODE_PERSONALIZE)
            })
            finish()
        } else {
            initUI(savedInstanceState)
        }
    }

    private fun initUI(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        initToolbar()
        initBottomNav()
        main_viewpager.adapter = pagerAdapter


        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when(menuItem.itemId){
                R.id.menu_notif -> {
                    startActivity(Intent(this, NotificationActivity::class.java))
                    true
                }
                R.id.menu_search ->{
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                R.id.menu_settings -> {
                    startActivity(Intent(context,SettingsActivity::class.java))
                    true
                }
                R.id.menu_feedback -> {
                    onFeedbackMenu()
                    true
                }
                R.id.menu_invite -> {
                    startActivity(ShareCompat.IntentBuilder.from(this)
                            .setText("""Hey, let's make and share memes on MemeIt!|Download it here https://play.google.com/store/apps/details?id=com.innov8.memeit""".trimMargin())
                            .setType("text/plain")
                            .createChooserIntent())
                    true
                }

                R.id.menu_aboutus -> {
                    startActivity(Intent(context,AboutActivity::class.java))
                    true
                }
            }
            drawerLayout.closeDrawers()

            true
        }
    }

    abstract fun onFeedbackMenu()

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val selected = savedInstanceState.getInt("selected")
        bottom_nav.select(selected, true)
    }

    private fun initToolbar() {
        this.setSupportActionBar(toolbar2)
    }

    private fun initBottomNav() {
        bottom_nav.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            if (searchItem != null && searchItem!!.isActionViewExpanded)
                searchItem!!.collapseActionView()
            appbar.setExpanded(true, false)
            when (item.itemId) {
                R.id.menu_home -> {
                    setTitle(0)
                    main_viewpager.setCurrentItem(0, false)
                    supportActionBar!!.show()
                    changeStatColor()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_trending -> {
                    setTitle(1)
                    main_viewpager.setCurrentItem(1, false)
                    supportActionBar!!.show()
                    changeStatColor()

                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_create -> {
                    startActivity(Intent(this@MainActivity, MemeChooserActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_favorites -> {
                    setTitle(2)
                    changeStatColor()
                    supportActionBar!!.show()
                    main_viewpager.setCurrentItem(2, false)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_me -> {
                    changeStatColor(true)

                    supportActionBar!!.hide()
                    main_viewpager.setCurrentItem(3, false)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun changeStatColor(profile: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = if (profile) Color.RED else Color.parseColor("#eeeeee")

        }
    }

    override fun setTitle(index: Int) {
        supportActionBar!!.title = titles[index]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_top_menu, menu)

        notifItem = menu.findItem(R.id.menu_notif)
        loadNotifCount()

        return super.onCreateOptionsMenu(menu)
    }

//    private fun setDrawerFont(){
//        val navView : NavigationView =  findViewById(R.id.navView)
//        val m : Menu= navView.menu
//        for(m:MenuItem in )
//    }

    private fun loadNotifCount() {
        MemeItUsers.getNotifCount().call {
            notifItem?.icon = DrawableBadge.Builder(this)
                    .drawableResId(R.drawable.ic_notifications_black_24dp)
                    .maximumCounter(99)
                    .build()
                    .get(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

         return when (item.itemId) {
            R.id.menu_notif -> {
                startActivity(Intent(this, NotificationActivity::class.java))
                true
            }
            R.id.menu_search ->{
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(context,SettingsActivity::class.java))
                true
            }
            R.id.menu_feedback -> {
                onFeedbackMenu()
                true
            }
            R.id.menu_invite -> {
                startActivity(ShareCompat.IntentBuilder.from(this)
                        .setText("""Hey, let's make and share memes on MemeIt!|Download it here https://play.google.com/store/apps/details?id=com.innov8.memeit""".trimMargin())
                        .setType("text/plain")
                        .createChooserIntent())
                true
            }

            R.id.menu_aboutus -> {
                startActivity(Intent(context,AboutActivity::class.java))
                true
            }
            android.R.id.home ->{
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }

             else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotifCount()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selected", bottom_nav.selectedIndex)
    }

    private inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = when (position) {
            0 -> MemeListFragment.newInstance(MemeAdapter.HOME_ADAPTER, HomeMemeLoader2())
            1 -> MemeListFragment.newInstance(MemeAdapter.TRENDING_ADAPTER, TrendingMemeLoader())
            2 -> MemeListFragment.newInstance(MemeAdapter.LIST_FAVORITE_ADAPTER, FavoriteMemeLoader())
            3 -> ProfileFragment.newInstance()
            else -> throw IllegalArgumentException()
        }

        override fun getCount() = 4
    }
}