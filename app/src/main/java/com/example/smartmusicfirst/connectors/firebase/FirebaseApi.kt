package com.example.smartmusicfirst.connectors.firebase

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class FirebaseApi {
    private val database = Firebase.firestore
    private val storage = Firebase.storage

    fun getDocs(collectionName: String): List<Map<String, Any?>> {
        val items = mutableListOf<Map<String, Any>>()
        database.collection(collectionName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    items.add(document.data)
                }
            }
            .addOnFailureListener { exception ->
                throw exception
            }
        return items
    }

    fun deleteDoc(collectionName: String, documentId: String): Boolean {
        var isDeleted = false
        database.collection(collectionName)
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                isDeleted = true
            }
            .addOnFailureListener { exception ->
                throw exception
            }
        return isDeleted
    }

    fun updateDoc(collectionName: String, documentId: String, doc: Map<String, Any?>): Boolean {
        var isUpdated = false
        database.collection(collectionName)
            .document(documentId)
            .set(doc)
            .addOnSuccessListener {
                isUpdated = true
            }
            .addOnFailureListener { exception ->
                throw exception
            }
        return isUpdated
    }

    fun isExist(collectionName: String, documentId: String): Boolean {
        var isExist = false
        database.collection(collectionName)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    isExist = true
                }
            }
            .addOnFailureListener { exception ->
                throw exception
            }
        return isExist
    }

    suspend fun createDoc(
        collectionName: String,
        documentId: String,
        item: Map<String, Any?>
    ): Boolean {
        return try {
            database.collection(collectionName).document(documentId).set(item).await()
            true
        } catch (exception: Exception) {
            throw exception
        }
    }

    suspend fun addDoc(collectionName: String, item: Map<String, Any?>): DocumentReference? {
        return try {
            val docRef = database.collection(collectionName).add(item).await()
            docRef
        } catch (exception: Exception) {
            throw exception
        }
    }

    suspend fun uploadImage(imageUri: Uri): String {
        return try {
            val storageRef = storage.reference.child("images/${imageUri.lastPathSegment} ")
            storageRef.putFile(imageUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (exception: Exception) {
            throw exception
        }
    }
}
