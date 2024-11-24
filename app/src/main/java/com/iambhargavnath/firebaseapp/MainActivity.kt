package com.iambhargavnath.firebaseapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.iambhargavnath.firebaseapp.adapter.QuoteAdapter
import com.iambhargavnath.firebaseapp.model.Quote

class MainActivity : AppCompatActivity() {

    private val TAG = "FirebaseLogg"
    private lateinit var myRef: DatabaseReference

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    private lateinit var quoteAdapter: QuoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabAdd = findViewById(R.id.fabAdd)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val database = Firebase.database
        myRef = database.getReference("quotes")

        getQuotes()

        fabAdd.setOnClickListener {
            showAddDialog()
        }

    }

    fun addQuote(quotes: Quote) {
        val taskId = myRef.push().key
        quotes.id = taskId
        myRef.child(taskId!!).setValue(quotes)
            .addOnSuccessListener {
                Log.d(TAG, "Quote added successfully")
                Toast.makeText(this, "Quote Added Successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to add Quote", e)
            }
    }

    fun getQuotes() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val quotes = mutableListOf<Quote>()
                for (taskSnapshot in snapshot.children) {
                    val quote = taskSnapshot.getValue(Quote::class.java)
                    if (quote != null) {
                        quotes.add(quote)
                    }
                }
                Log.d(TAG, "Quote retrieved: ${quotes.size}")

                quoteAdapter = QuoteAdapter(
                    quotes,
                    onClick = { selectedQuote ->
                        val intent = Intent(this@MainActivity, DisplayQuoteActivity::class.java)
                        intent.putExtra("quoteId", selectedQuote!!.id)
                        startActivity(intent)
                    }
                )

                recyclerView.adapter = quoteAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve tasks", error.toException())
            }
        })
    }

    private fun showAddDialog() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val quoteFill: EditText = dialogView.findViewById(R.id.quoteFill)
        val authorFill: EditText = dialogView.findViewById(R.id.authorFill)
        val languageFill: EditText = dialogView.findViewById(R.id.languageFill)
        val saveButton: Button = dialogView.findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            if(quoteFill.text.isNotEmpty() && authorFill.text.isNotEmpty() && languageFill.text.isNotEmpty()) {
                val quote = Quote(
                    quote = quoteFill.text.toString(),
                    author = authorFill.text.toString(),
                    language = languageFill.text.toString()
                )
                addQuote(quote)
                dialog.dismiss()
            }
            else {
                Toast.makeText(this, "Fields shouldn't be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

}