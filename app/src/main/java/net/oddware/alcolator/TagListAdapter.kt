package net.oddware.alcolator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.oddware.alcolator.databinding.TagListItemBinding

class TagListAdapter(
    private val tagList: List<String>,
    private val listener: TagListFragment.TagSelectionListener
) : RecyclerView.Adapter<TagListAdapter.TagViewHolder>() {
    //private var _binding: TagListItemBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    //private val binding = _binding!!

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val tv: TextView = itemView.tvTagItem
        val binding = TagListItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        // set listener etc
        //vh.tv.setOnClickListener {
        //    Timber.d("Tag clicked: $it")
        //    listener.onSelectTag(it.toString())
        //}

        return TagViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.tag_list_item
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        //holder.tv.text = tagList[position]
        //holder.tv.setOnClickListener {
        //    listener.onSelectTag(holder.tv.text as String)
        //}
        with(holder) {
            binding.tvTagItem.text = tagList[position]
            binding.tvTagItem.setOnClickListener {
                listener.onSelectTag(binding.tvTagItem.text.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }
}