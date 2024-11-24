package com.iambhargavnath.firebaseapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.iambhargavnath.firebaseapp.model.Quote

class DisplayQuoteActivity : AppCompatActivity() {

    private lateinit var myRef: DatabaseReference
    private val TAG = "FirebaseLogg"

    private var isEditable = false

    private lateinit var quoteFill: EditText
    private lateinit var authorFill: EditText
    private lateinit var languageFill: EditText
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_display_quote)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val quoteId: String = intent.getStringExtra("quoteId")?: "xyz"

        val database = Firebase.database
        myRef = database.getReference("quotes")

        quoteFill = findViewById(R.id.quoteFill)
        authorFill = findViewById(R.id.authorFill)
        languageFill = findViewById(R.id.languageFill)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)

        editButton.setOnClickListener {
            if(isEditable)
            {
                if(quoteFill.text.isNotEmpty() && authorFill.text.isNotEmpty() && languageFill.text.isNotEmpty()) {
                    val quote = Quote(
                        id = quoteId,
                        quote = quoteFill.text.toString(),
                        author = authorFill.text.toString(),
                        language = languageFill.text.toString()
                    )
                    updateQuote(quote)
                }
            } else {
                isEditable = true
                editButton.setText("Save")
                quoteFill.isEnabled = true
                authorFill.isEnabled = true
                languageFill.isEnabled = true
            }
        }

        deleteButton.setOnClickListener {
            deleteConfirmationDialog(quoteId)
        }

        getQuoteById(quoteId) { quote ->
            if (quote != null) {
                quoteFill.setText(quote.quote)
                authorFill.setText(quote.author)
                languageFill.setText(quote.language)
            } else {
                Log.d(TAG, "Quote not found")
            }
        }

    }

    fun getQuoteById(quoteId: String, onResult: (Quote?) -> Unit) {
        myRef.child(quoteId).get()
            .addOnSuccessListener { dataSnapshot ->
                val student = dataSnapshot.getValue(Quote::class.java)
                onResult(student)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching Quote by ID", exception)
                onResult(null)
            }
    }

    fun updateQuote(quote: Quote) {
        quote.id?.let { quoteId ->
            myRef.child(quoteId).setValue(quote)
                .addOnSuccessListener {
                    Log.d(TAG, "Quote updated successfully")
                    Toast.makeText(this, "Quote updated Successfully!", Toast.LENGTH_SHORT).show()
                    quoteFill.isEnabled = false
                    authorFill.isEnabled = false
                    languageFill.isEnabled = false
                    editButton.setText("Edit")
                    isEditable = false
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Quote to update Quote", e)
                }
        }
    }

    fun deleteQuote(quoteId: String) {
        myRef.child(quoteId).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "Quote deleted successfully")
                Toast.makeText(this, "Quote deleted Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to delete Quote", e)
            }
    }

    private fun deleteConfirmationDialog(quoteId: String)
    {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete Quote?")
            .setMessage("Are you sure you want to delete this quote?")
            .setPositiveButton("Yes") { dialogInterface, _ ->
                deleteQuote(quoteId)
                dialogInterface.dismiss()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

}