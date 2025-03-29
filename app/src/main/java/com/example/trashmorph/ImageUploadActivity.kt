package com.example.trashmorph

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ImageUploadActivity : AppCompatActivity() {

    private lateinit var itemName: EditText
    private lateinit var itemQuantity: EditText
    private lateinit var addButton: Button
    private lateinit var viewListButton: Button
    private lateinit var uploadButton: Button

    private var imageUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child("uploads")

    private var itemsList = mutableListOf<String>() // ✅ Stores only session items

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_upload)

        itemName = findViewById(R.id.itemName)
        itemQuantity = findViewById(R.id.itemQuantity)
        addButton = findViewById(R.id.addButton)
        viewListButton = findViewById(R.id.viewListButton)
        uploadButton = findViewById(R.id.uploadButton)

        addButton.setOnClickListener { addItem() }
        viewListButton.setOnClickListener { fetchItemsFromSessionOnly() } // ✅ Show only session items
        uploadButton.setOnClickListener { showImagePickerDialog() }

        itemsList.clear() // ✅ Clear session list on app start (Firebase data remains safe)
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select Image From")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                }
            }.show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, 102)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                101, 102 -> {
                    imageUri = data?.data
                    if (imageUri != null) {
                        uploadImageToStorage()
                    } else {
                        Toast.makeText(this, "Failed to get image!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun uploadImageToStorage() {
        if (imageUri != null) {
            val fileRef = storageRef.child("${System.currentTimeMillis()}.jpg")

            fileRef.putFile(imageUri!!)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    fileRef.downloadUrl
                }
                .addOnSuccessListener { downloadUri ->
                    Log.d("Storage", "Image uploaded: $downloadUri")
                    saveToFirestore(downloadUri.toString())
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Image upload failed!", Toast.LENGTH_SHORT).show()
                    Log.e("Storage", "Image upload error", e)
                }
        } else {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addItem() {
        val name = itemName.text.toString()
        val quantity = itemQuantity.text.toString().toIntOrNull()

        if (name.isNotEmpty() && quantity != null) {
            val itemEntry = "$name: $quantity"
            itemsList.add(itemEntry) // ✅ Add to session list
            saveToFirestore(null)  // ✅ Save to Firebase (data remains even after restart)
        } else {
            Toast.makeText(this, "Enter valid name and quantity!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToFirestore(imageUrl: String?) {
        val name = itemName.text.toString()
        val quantity = itemQuantity.text.toString().toIntOrNull()

        if (name.isNotEmpty() && quantity != null) {
            val itemData = hashMapOf(
                "name" to name,
                "quantity" to quantity,
                "imageUrl" to (imageUrl ?: "No Image")
            )

            db.collection("items")
                .add(itemData)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Item Added!", Toast.LENGTH_SHORT).show()
                    Log.d("Firestore", "Item saved: ${documentReference.id}")
                    clearFields()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add item: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Firestore", "Error saving document", e)
                }
        } else {
            Toast.makeText(this, "Invalid item details!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchItemsFromSessionOnly() {
        if (itemsList.isEmpty()) {
            showItemListDialog(listOf("No items found."))
        } else {
            showItemListDialog(itemsList)
        }
    }

    private fun showItemListDialog(itemsList: List<String>) {
        AlertDialog.Builder(this)
            .setTitle("Item List")
            .setMessage(itemsList.joinToString("\n"))
            .setPositiveButton("OK", null)
            .show()
    }

    private fun clearFields() {
        itemName.text.clear()
        itemQuantity.text.clear()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}
