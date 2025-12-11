package com.burhanudinal.firebase8

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.burhanudinal.firebase8.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var booksRef: DatabaseReference
    private lateinit var adapter: BookAdapter
    private val keys = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        booksRef = FirebaseDatabase.getInstance().getReference("books")

        adapter = BookAdapter(mutableListOf(),
            onDelete = { book, pos ->
                val key = keys.getOrNull(pos)
                if (key != null) {
                    booksRef.child(key).removeValue().addOnSuccessListener {
                        Toast.makeText(this, "Tugas dihapus", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onEdit = { book, pos ->
                val key = keys.getOrNull(pos)
                AddBookDialog(this, booksRef) .show(existing = book, nodeKey = key)
            },
            onToggleDone = { book, pos, isDone ->
                val key = keys.getOrNull(pos)
                if (key != null) {
                    booksRef.child(key)
                        .child("done")
                        .setValue(isDone)
                        .addOnSuccessListener {
                        }
                }
            }
        )

        binding.rvBooks.layoutManager = LinearLayoutManager(this)
        binding.rvBooks.adapter = adapter

        binding.fabAddBooks.setOnClickListener {
            AddBookDialog(this, booksRef) {
            }.show()
        }

        fetchData()
    }

    private fun fetchData() {
        booksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Book>()
                keys.clear()
                for (child in snapshot.children) {
                    val b = child.getValue(Book::class.java)
                    if (b != null) {
                        list.add(b)
                        keys.add(child.key ?: "")
                    }
                }
                adapter.updateList(list)
                binding.emptyState.visibility = if (list.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}