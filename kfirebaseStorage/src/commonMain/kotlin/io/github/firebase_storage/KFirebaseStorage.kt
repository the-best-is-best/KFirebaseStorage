package io.github.firebase_storage

expect class KFirebaseStorage() {
    suspend fun uploadFile(
        filePath: String,
        fileData: ByteArray,

        ): Result<Boolean>

    //    suspend fun uploadFromUri(uri: String): Result<FileUploadResult>
    suspend fun uploadFileWithMetadata(
        filePath: String,
        fileData: ByteArray,
        metadata: Map<String, String>
    ): Result<Boolean>


    suspend fun deleteFile(filePath: String): Result<Boolean>

    suspend fun getFileBytes(filePath: String): Result<ByteArray>
}
