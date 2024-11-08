package org.wit.imbored.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.wit.imbored.databinding.CardImboredBinding
import org.wit.imbored.models.ImBoredModel

interface ImBoredListener {
    fun onActivityClick(activityItem: ImBoredModel)
}

class ImBoredAdapter constructor(
    private var activities: List<ImBoredModel>,
    private val listener: ImBoredListener
) : RecyclerView.Adapter<ImBoredAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardImboredBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val activityItem = activities[holder.adapterPosition]
        holder.bind(activityItem, listener)
    }

    override fun getItemCount(): Int = activities.size

    class MainHolder(private val binding: CardImboredBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(activityItem: ImBoredModel, listener: ImBoredListener) {
            binding.activityTitle.text = activityItem.title
            binding.description.text = activityItem.description
            binding.category.text = activityItem.category
            binding.dateTime.text = activityItem.dateTime
            binding.recurrence.text = activityItem.recurrence

            Picasso.get().load(activityItem.image).resize(150,150).into(binding.imageIcon)

            binding.root.setOnClickListener { listener.onActivityClick(activityItem) }
        }
    }
}
