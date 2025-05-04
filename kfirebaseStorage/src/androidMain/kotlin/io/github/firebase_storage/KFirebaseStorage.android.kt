package io.github.firebase_storage

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class KFirebaseStorage {
    private val storage = FirebaseStorage.getInstance()

    actual suspend fun uploadFile(
        filePath: String,
        fileData: ByteArray,
        // Returning file URL and file path
    ): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            val storageRef = storage.reference.child(filePath)
            storageRef.putBytes(fileData)
                .addOnSuccessListener {
                    cont.resume(Result.success(true))

                }
                .addOnFailureListener { exception ->
                    cont.resume(Result.failure(exception))
                }
        }
    }


    actual suspend fun deleteFile(
        filePath: String

    ): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            val storageRef = storage.reference.child(filePath)

            storageRef.delete().addOnSuccessListener {
                cont.resume(Result.success(true))
            }.addOnFailureListener { exception ->
                cont.resume(Result.failure(exception))
            }
        }
    }
//
//    actual suspend fun uploadFromUri(uri: String): Result<FileUploadResult> {
//        return try {
//            val storageReference: StorageReference =
//                storage.reference.child("uploads/${Uri.parse(uri).lastPathSegment}")
//
//            // Get the content resolver and open the InputStream
//            val inputStream: InputStream =
//                applicationContext.contentResolver.openInputStream(Uri.parse(uri))!!
//
//            // Read the data into a ByteArray
//            val byteArrayOutputStream = ByteArrayOutputStream()
//            inputStream.copyTo(byteArrayOutputStream)
//            val fileData = byteArrayOutputStream.toByteArray()
//
//            // Upload the file to Firebase Storage
//            val uploadTask = storageReference.putBytes(fileData).await()
//
//            // Get the download URL after upload
//            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
//
//            Result.success(FileUploadResult(downloadUrl, storageReference.path))
//
//
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    actual suspend fun uploadFileWithMetadata(
        filePath: String,
        fileData: ByteArray,
        metadata: Map<String, String>
    ): Result<Boolean> {
        return try {
            val storageRef = storage.reference.child(filePath)

            val firebaseMetadata = StorageMetadata.Builder().apply {
                metadata.forEach { (key, value) ->
                    setCustomMetadata(key, value)
                }
            }.build()

            storageRef.putBytes(fileData, firebaseMetadata).await()

            Result.success(true)


        } catch (e: Exception) {
            Result.failure(e)

        }
    }

    actual suspend fun getFileBytes(filePath: String): Result<ByteArray> {
        return suspendCancellableCoroutine { cont ->
            val storageRef = FirebaseStorage.getInstance().reference.child(filePath)


            storageRef.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                    // Successfully retrieved the file as bytes
                    cont.resume(Result.success(bytes))
                }
                .addOnFailureListener { exception ->
                    // Failed to download the file, return the exception
                    cont.resumeWithException(exception)
                }
        }
    }

}
