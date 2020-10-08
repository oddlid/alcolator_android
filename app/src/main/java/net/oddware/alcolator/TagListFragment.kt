package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_tag_list.*
import kotlinx.android.synthetic.main.fragment_tag_list.view.*

class TagListFragment(private val listener: TagSelectionListener) : DialogFragment() {
    interface TagSelectionListener {
        fun onSelectTag(tag: String)
        fun onCancelTag()
        fun onResetTag()
    }

    private lateinit var drinkViewModel: DrinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tag_list, container, false)

        val lloMgr = LinearLayoutManager(view.context)
        with(view.rvTagList) {
            // addItemDecoration
            setHasFixedSize(true)
            layoutManager = lloMgr
            //adapter = TagListAdapter()
        }

        view.btnTagListReset.setOnClickListener {
            listener.onResetTag()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)

        drinkViewModel.tags.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                rvTagList.adapter = TagListAdapter(it, listener)
            }
        })
    }
}