package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.oddware.alcolator.databinding.FragmentTagListBinding

class TagListFragment(private val listener: TagSelectionListener) : DialogFragment() {
    interface TagSelectionListener {
        fun onSelectTag(tag: String)
        fun onCancelTag()
        fun onResetTag()
    }

    private lateinit var drinkViewModel: DrinkViewModel
    private var _binding: FragmentTagListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTagListBinding.inflate(inflater, container, false)
        val view = binding.root

        val lloMgr = LinearLayoutManager(view.context)
        with(binding.rvTagList) {
            // addItemDecoration
            setHasFixedSize(true)
            layoutManager = lloMgr
            //adapter = TagListAdapter()
        }

        binding.btnTagListReset.setOnClickListener {
            listener.onResetTag()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)

        drinkViewModel.tags.observe(viewLifecycleOwner, {
            if (null != it) {
                binding.rvTagList.adapter = TagListAdapter(it, listener)
            }
        })
    }
}