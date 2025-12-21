package com.example.mylocalcanvas.data

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

data class ImageGenerationRequest(
    val model: String,
    val prompt: String,
    @Json(name = "negative_prompt") val negativePrompt: String? = null,
    @Json(name = "num_inference_steps") val numInferenceSteps: Int = 20,
    val image: String,
    val image2: String? = null
)

data class ImageGenerationResponse(
    val images: List<ImageItem> = emptyList()
)

data class ImageItem(
    val url: String
)

interface SiliconFlowApiService {
    @POST("images/generations")
    suspend fun createImage(
        @Header("Authorization") authorization: String,
        @Body request: ImageGenerationRequest
    ): ImageGenerationResponse
}

object SiliconFlowApi {
    private const val BASE_URL = "https://api.siliconflow.cn/v1/"

    fun createService(): SiliconFlowApiService {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(SiliconFlowApiService::class.java)
    }
}
