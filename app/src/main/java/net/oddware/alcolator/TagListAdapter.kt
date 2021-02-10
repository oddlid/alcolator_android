package net.oddware.alcolator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import net.oddware.alcolator.databinding.TagListItemBinding

class TagListAdapter(
    private val tagList: List<String>,
    private val navCtl: NavController
) : RecyclerView.Adapter<TagListAdapter.TagViewHolder>() {

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = TagListItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

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
                //listener.onSelectTag(binding.tvTagItem.text.toString())
                val action = TagListFragmentDirections.actionTagListFragmentToDrinkListFragment()

                // Set result before returning
                navCtl.previousBackStackEntry?.savedStateHandle?.set(
                    TagListFragment.FILTER_TAG_KEY,
                    binding.tvTagItem.text.toString()
                )

                navCtl.navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }
}