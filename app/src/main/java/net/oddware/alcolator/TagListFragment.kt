package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import net.oddware.alcolator.databinding.FragmentTagListBinding
import timber.log.Timber

class TagListFragment() : DialogFragment() {
    //interface TagSelectionListener {
    //    fun onSelectTag(tag: String)
    //    fun onCancelTag()
    //    fun onResetTag()
    //}

    companion object {
        const val FILTER_TAG_KEY = "net.oddware.alcolator.TagListFragment.FILTER_TAG_KEY"
    }

    private val drinkViewModel: DrinkViewModel by activityViewModels()
    private var _binding: FragmentTagListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //override fun onCreate(savedInstanceState: Bundle?) {
    //    super.onCreate(savedInstanceState)
    //    setStyle(STYLE_NORMAL, R.style.AppTheme)
    //}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTagListBinding.inflate(inflater, container, false)
        val view = binding.root

        val lloMgr = LinearLayoutManager(view.context)
        with(binding.rvTagList) {
            addItemDecoration(DividerItemDecoration(view.context, lloMgr.orientation))
            setHasFixedSize(true)
            layoutManager = lloMgr
            //adapter = TagListAdapter()
        }

        binding.btnTagListReset.setOnClickListener {
            //listener.onResetTag()
            val navCtl = findNavController()
            navCtl.previousBackStackEntry?.savedStateHandle?.set(
                FILTER_TAG_KEY,
                ""
            ) ?: kotlin.run {
                Timber.d("Did not find navController.previousBackStackEntry")
            }
            val action = TagListFragmentDirections.actionTagListFragmentToDrinkListFragment()
            navCtl.navigate(action)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return super.onCreateDialog(savedInstanceState)
        _binding = FragmentTagListBinding.inflate(LayoutInflater.from(context), null, false)
        val view = binding.root
        val bld = AlertDialog.Builder(requireActivity())

        val lloMgr = LinearLayoutManager(view.context)
        with (binding.rvTagList) {
            addItemDecoration(DividerItemDecoration(view.context, lloMgr.orientation))
            setHasFixedSize(true)
            layoutManager = lloMgr
        }

        bld.setView(view)

        return bld.create()
    }

     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drinkViewModel.tags.observe(viewLifecycleOwner, {
            if (null != it) {
                binding.rvTagList.adapter = TagListAdapter(it, findNavController())
            }
        })
    }

    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)

        drinkViewModel.tags.observe(viewLifecycleOwner, {
            if (null != it) {
                binding.rvTagList.adapter = TagListAdapter(it, listener)
            }
        })
    }

     */
}