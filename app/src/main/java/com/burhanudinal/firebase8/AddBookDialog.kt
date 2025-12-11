package com.burhanudinal.firebase8

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.burhanudinal.firebase8.databinding.UploadDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddBookDialog(
    private val context: Context,
    private val booksRef: DatabaseReference,
    private val onSaved: (() -> Unit)? = null
) {
    fun show(existing: Book? = null, nodeKey: String? = null) {
        val dialogBinding = UploadDialogBinding.inflate(LayoutInflater.from(context))

        existing?.let {
            dialogBinding.editTextTitleBook.setText(it.title)
            dialogBinding.editDeskBook.setText(it.description)
            dialogBinding.editTextRelease.setText(it.release)
        }

        dialogBinding.editTextRelease.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dp = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    dialogBinding.editTextRelease.setText(dateFormat.format(selectedCalendar.time))
                },
                year, month, day
            )

            dp.datePicker.minDate = System.currentTimeMillis()

            dp.show()
        }


        MaterialAlertDialogBuilder(context)
            .setTitle(if (existing == null) "Tambah Tugas Baru" else "Edit Tugas")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { dlg, _ ->
                val title = dialogBinding.editTextTitleBook.text.toString().trim()
                val desc = dialogBinding.editDeskBook.text.toString().trim()
                val release = dialogBinding.editTextRelease.text.toString().trim()

                if (title.isEmpty() || release.isEmpty()) {
                    Toast.makeText(context, "Isi semua data!", Toast.LENGTH_SHORT).show()
                } else {
                    if (nodeKey == null) {
                        val node = booksRef.push()
                        val newBook = Book(title = title, release = release, description = desc)
                        node.setValue(newBook).addOnSuccessListener {
                            Toast.makeText(context, "Tugas ditambah", Toast.LENGTH_SHORT).show()
                            onSaved?.invoke()
                        }.addOnFailureListener { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val updated = Book(title = title, release = release, description = desc)
                        booksRef.child(nodeKey).setValue(updated).addOnSuccessListener {
                            Toast.makeText(context, "Tugas diperbarui", Toast.LENGTH_SHORT).show()
                            onSaved?.invoke()
                        }.addOnFailureListener { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}


