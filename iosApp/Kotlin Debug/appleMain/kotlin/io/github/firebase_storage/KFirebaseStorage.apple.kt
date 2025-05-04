package io.github.firebase_storage

import io.github.native.kfirebase.storage.FIRStorage
import io.github.native.kfirebase.storage.FIRStorageReference
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSArray
import platform.Foundation.NSData
import platform.Foundation.NSDictionary
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSInputStream
import platform.Foundation.NSMutableArray
import platform.Foundation.NSSearchPathDirectory
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.dataTaskWithURL
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.dictionaryWithObjects
import platform.Foundation.lastPathComponent
import platform.Foundation.pathExtension
import platform.Foundation.stringByAppendingPathComponent
import platform.posix.memcpy
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
actual class KFirebaseStorage {

    private val storage = FIRStorage.storage()


    @OptIn(UnsafeNumber::class, BetaInteropApi::class)
    actual suspend fun uploadFile(
        filePath: String,
        fileData: ByteArray,
    ): Result<FileUploadResult> {
        return suspendCancellableCoroutine { cont ->

            val storageRef = storage.reference().child(filePath)

            fileData.usePinned { pinned ->
                val data =
                    NSData.create(bytes = pinned.addressOf(0), length = fileData.size.toULong())

                storageRef.putData(data, null) { _, error ->
                    if (error != null) {
                        cont.resumeWith(Result.failure(Exception(error.localizedDescription)))
                    } else {
                        storageRef.downloadURLWithCompletion { url, errorD ->
                            if (errorD != null) {
                                cont.resumeWith(Result.failure(Exception(errorD.localizedDescription)))
                            } else {
                                cont.resume(
                                    Result.success(
                                        FileUploadResult(
                                            url?.absoluteString,
                                            filePath
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    actual suspend fun deleteFile(
        filePath: String,
    ): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            val storageRef = storage.reference().child(filePath)

            storageRef.deleteWithCompletion { error ->
                if (error != null) {
                    cont.resume(Result.failure(Exception(error.localizedDescription)))
                } else {
                    cont.resume(Result.success(true))
                }
            }
        }
    }
//
//    actual suspend fun uploadFromUri(uri: String): Result<FileUploadResult> {
//        return suspendCancellableCoroutine { continuation ->
//            try {
//                // Get the file from the URL (assuming it's a local file path)
//                val fileURL = NSURL(string = uri)
//
//                // Convert the file to NSData
//                val fileData = fileURL.absoluteString?.let { NSData.dataWithContentsOfFile(it) } ?: run {
//                    continuation.resume(Result.failure(IllegalArgumentException("Failed to read file")))
//                    return@suspendCancellableCoroutine
//                }
//
//                // Upload the file to Firebase Storage
//                val storageRef: FIRStorageReference = storage.reference().child("uploads/${fileURL.lastPathComponent}")
//
//                storageRef.putData(fileData, null) { _, error ->
//                    if (error != null) {
//                        continuation.resumeWith(Result.failure(Exception(error.localizedDescription)))
//                    } else {
//                        storageRef.downloadURLWithCompletion { url, errorD ->
//                            if (errorD != null) {
//                                continuation.resumeWith(Result.failure(Exception(errorD.localizedDescription)))
//                            } else {
//                                continuation.resume(Result.success(FileUploadResult(url!!.absoluteString, fileURL.lastPathComponent!!)))
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                continuation.resume(Result.failure(e))
//            }
//        }
//    }

    @OptIn(BetaInteropApi::class)
    actual suspend fun uploadFileWithMetadata(
        filePath: String,
        fileData: ByteArray,
        metadata: Map<String, String>
    ): Result<FileUploadResult> {
        return suspendCancellableCoroutine { cont ->
            val storageRef = storage.reference().child(filePath)

            fileData.usePinned { pinned ->
                val data =
                    NSData.create(bytes = pinned.addressOf(0), length = fileData.size.toULong())

                val meta = io.github.native.kfirebase.storage.FIRStorageMetadata().apply {
                    setCustomMetadata(metadata.toNSDictionary())
                }

                storageRef.putData(data, meta) { _, error ->
                    if (error != null) {
                        cont.resume(Result.failure(Exception(error.localizedDescription)))
                    } else {
                        storageRef.downloadURLWithCompletion { url, errorD ->
                            if (errorD != null) {
                                cont.resume(Result.failure(Exception(errorD.localizedDescription)))
                            } else {
                                cont.resume(
                                    Result.success(
                                        FileUploadResult(
                                            url!!.absoluteString,
                                            filePath
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }

        }
    }

    actual suspend fun getFileBytes(filePath: String): Result<ByteArray> {
        return suspendCancellableCoroutine { cont ->
            val storageRef = storage.reference().child(filePath)

            // Get the download URL
            storageRef.downloadURLWithCompletion { url, error ->
                if (error != null) {
                    cont.resumeWithException(Exception(error.localizedDescription))
                } else if (url != null) {
                    // Now download the file using the URL
                    val task =
                        NSURLSession.sharedSession.dataTaskWithURL(url) { data, response, error ->
                            if (error != null) {
                                cont.resumeWithException(Exception(error.localizedDescription))
                            } else if (data != null) {
                                // Convert NSData to ByteArray
                                val byteArray = data.toByteArray()
                                cont.resume(Result.success(byteArray))
                            } else {
                                cont.resumeWithException(Exception("Unknown error occurred"))
                            }
                        }
                    task.resume()
                } else {
                    cont.resumeWithException(Exception("No URL available"))
                }
            }
        }
    }


}


// Helper extension function to convert NSData to ByteArray
@OptIn(UnsafeNumber::class, ExperimentalForeignApi::class)
private fun Map<String, String>.toNSDictionary():
        Map<Any?, *> {
    val nsKeys = this.keys.map { it as NSString } as List<Any>
    val nsValues = this.values.map { it as NSString } as List<Any>

    return NSDictionary.dictionaryWithObjects(
        objects = nsValues,
        forKeys = nsKeys
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()
    val byteArray = ByteArray(size)
    memcpy(byteArray.refTo(0), this.bytes, size.convert())
    return byteArray
}
