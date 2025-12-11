package com.burhanudinal.firebase8

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.burhanudinal.firebase8.databinding.ItemBookBinding

class BookAdapter(
    private val books: MutableList<Book>,
    private val onDelete: (Book, Int) -> Unit,
    private val onEdit: (Book, Int) -> Unit,
    private val onToggleDone: (Book, Int, Boolean) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    fun updateList(newBooks: List<Book>) {
        books.clear()
        books.addAll(newBooks)
        notifyDataSetChanged()
    }

    inner class BookViewHolder(val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book, position: Int) {

            binding.tvTitle.text = book.title
            binding.tvDesc.text = book.description ?: ""
            binding.tvRelease.text = book.release ?: ""

            val isDone = book.done

            binding.root.alpha = if (isDone) 0.5f else 1f

            binding.tvTitle.paint.isStrikeThruText = isDone

            val gray = Color.parseColor("#9E9E9E")
            val normalTitle = Color.parseColor("#1A1A1A")
            val normalDesc = Color.parseColor("#666666")
            val normalDate = Color.parseColor("#4169E1")

            if (isDone) {
                binding.tvTitle.setTextColor(gray)
                binding.tvDesc.setTextColor(gray)
                binding.tvRelease.setTextColor(gray)

                binding.tvRelease.setBackgroundResource(R.drawable.release_bg)
                binding.tvRelease.alpha = 0.5f
            } else {
                binding.tvTitle.setTextColor(normalTitle)
                binding.tvDesc.setTextColor(normalDesc)
                binding.tvRelease.setTextColor(normalDate)

                binding.tvRelease.setBackgroundResource(R.drawable.release_bg)
                binding.tvRelease.alpha = 1f
            }

            // Checkbox
            binding.checkDone.setOnCheckedChangeListener(null)
            binding.checkDone.isChecked = isDone

            binding.checkDone.setOnCheckedChangeListener { _, isChecked ->
                book.done = isChecked
                onToggleDone(book, position, isChecked)

                notifyItemChanged(position) // refresh efek
            }

            // Delete
            binding.btnDelete.setOnClickListener { onDelete(book, position) }

            // Edit
            binding.root.setOnClickListener { onEdit(book, position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position], position)
    }

    override fun getItemCount() = books.size
}