package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.oddware.alcolator.databinding.FragmentTablistBinding

class TabListFragment : Fragment() {
    private var _binding: FragmentTablistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTablistBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
}