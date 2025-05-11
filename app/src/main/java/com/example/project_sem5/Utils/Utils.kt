package com.example.project_sem5.Utils

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.util.UUID

fun uploadImage(uri: Uri, folderName: String, callback: (String?)->Unit) {
    var imageURL: String ?= null
    Firebase.storage.getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                imageURL = it.toString()
                callback(imageURL)
            }
        }
}