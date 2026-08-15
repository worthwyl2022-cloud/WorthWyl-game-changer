package com.example.core.image

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header

/**
 * Interface for the Imagen API to generate book cover art.
 */
interface ImagenService {
    @POST("v1/images:generate")
    suspend fun generateCover(
        @Header("Authorization") token: String,
        @Body request: ImagenRequest
    ): ImagenResponse
}

data class ImagenRequest(
    val prompt: String,
    val aspect_ratio: String = "2:3",
    val number_of_images: Int = 1
)

data class ImagenResponse(
    val images: List<GeneratedImage>
)

data class GeneratedImage(
    val url: String
)
