package com.innov8.memeit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.CustomClasses.MyFragmentPagerAdapter
import com.innov8.memeit.Fragments.MemeListFragment
import com.innov8.memeit.Fragments.TagSearchFragment
import com.innov8.memeit.Fragments.UserSearchFragment
import com.innov8.memeit.Loaders.SearchMemeLoader
import com.innov8.memeit.R
import com.innov8.memeit.Utils.addOnTabSelected
import com.innov8.memeit.commons.addOnTextChanged
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private val pagerAdapter by lazy {
        SearchPagerAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        search_tabs.setupWithViewPager(search_pager)
        setSupportActionBar(search_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        search_view.addOnTextChanged {
            searchMemes(it)
            searchUsers(it)
            searchTags(it)
        }

        search_tabs.addOnTabSelected {
            when (it.position) {
                0 -> {
                    search_view.hint = "Search Memes, Use #tag to search by tag"
                }
                1 -> {
                    search_view.hint = "Search People, Use @user to search by username"
                }
                2 -> {
                    search_view.hint = "Search Tags"
                }
            }
        }
        search_pager.adapter=pagerAdapter
        search_pager.offscreenPageLimit=2
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    private fun searchMemes(search: String) {
        val f = pagerAdapter.getFragmentAt(0) as MemeListFragment
        (f.memeLoader as SearchMemeLoader).search = search
        f.loaderAdapterHandler.refresh(true)
    }

    private fun searchUsers(search: String) {
        val f = pagerAdapter.getFragmentAt(1) as UserSearchFragment
        f.setFilter(search)
    }

    private fun searchTags(search: String) {
        val f = pagerAdapter.getFragmentAt(2) as TagSearchFragment
        f.setFilter(search)
    }

    inner class SearchPagerAdapter(fm: FragmentManager) : MyFragmentPagerAdapter(fm) {

        private val titles = listOf("Memes", "People", "Tags")

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> MemeListFragment.newInstance(MemeAdapter.LIST_ADAPTER, SearchMemeLoader())
                1 -> UserSearchFragment().apply {
                    onItemClicked = { ProfileActivity.startWithUser(this@SearchActivity, it) }
                }
                2 -> TagSearchFragment().apply {
                    onItemClicked = { TagMemesActivity.startWithTag(this@SearchActivity, it.tag) }
                }
                else -> throw IllegalStateException()
            }
        }

        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }
}
