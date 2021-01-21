package net.oddware.alcolator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tag_list_item.view.*

class TagListAdapter(
    private val tagList: List<String>,
    private val listener: TagListFragment.TagSelectionListener
) : RecyclerView.Adapter<TagListAdapter.TagViewHolder>() {
    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv: TextView = itemView.tvTagItem
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
        holder.tv.text = tagList[position]
        holder.tv.setOnClickListener {
            listener.onSelectTag(holder.tv.text as String)
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }
}