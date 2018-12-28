package com.innov8.memeit.Fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.innov8.memeit.CustomClasses.MyFragmentPagerAdapter
import com.innov8.memeit.Loaders.SavedTemplatesLoader
import com.innov8.memeit.Loaders.ServerTemplateLoader
import com.innov8.memeit.Loaders.Sorter
import com.innov8.memeit.Loaders.TemplateLoader
import com.innov8.memeit.R
import com.innov8.memeit.Utils.addOnTabSelected
import com.innov8.memeit.commons.addOnTextChanged
import kotlinx.android.synthetic.main.fragment_meme_template.*

class MemeTemplateHolderFragment : Fragment() {
    private val pager by lazy {
        TemplatePager(childFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_template, container, false)
    }

    private val currentFrag: MemeTemplateFragment
        get() = pager.getFragmentAt(template_pager.currentItem) as MemeTemplateFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        template_pager.adapter = pager
        template_tabs.setupWithViewPager(template_pager)
        search_toolbar.inflateMenu(R.menu.template_menu)

        search_toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_template_search -> {

                }
                R.id.menu_template_category -> {

                }
            }
            true
        }
        /*search_view.addOnTextChanged {
            currentFrag.search(it)
        }
        template_tabs.addOnTabSelected {
            currentFrag.search(search_view.text.toString())
        }*/

    }

    inner class TemplatePager(fragmentManager: FragmentManager) : MyFragmentPagerAdapter(fragmentManager) {
        private val titles = arrayOf("Saved", "Popular", "Recent", "My Templates")


        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> MemeTemplateFragment.newInstance(SavedTemplatesLoader())
                1 -> MemeTemplateFragment.newInstance(ServerTemplateLoader())
                2 -> MemeTemplateFragment.newInstance(ServerTemplateLoader().apply {
                    sortBy = Sorter.RECENT
                })
                3 -> MemeTemplateFragment.newInstance(ServerTemplateLoader().apply {
                    sortBy = Sorter.RECENT
                    mine = true
                })
                else -> throw IllegalStateException()
            }
        }

        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]

    }
}