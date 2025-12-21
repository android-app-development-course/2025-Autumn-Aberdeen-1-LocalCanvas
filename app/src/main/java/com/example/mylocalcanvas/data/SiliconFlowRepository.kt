package com.example.mylocalcanvas.data

import com.example.mylocalcanvas.BuildConfig

class SiliconFlowRepository(
    private val api: SiliconFlowApiService
) {
    suspend fun generateImage(prompt: String, imageBase64: String, maskBase64: String?): String {
        val apiKey ="sk-rypgiqdjsrvalfpgibfotxyaiyhvzqaszgjwcegngogvmvbp"
        require(apiKey.isNotBlank()) { "SILICONFLOW_API_KEY is missing." }

        val request = ImageGenerationRequest(
            model = "Qwen/Qwen-Image-Edit-2509",
            prompt = prompt,
            image = imageBase64,
            image2 = maskBase64
        )

        val response = api.createImage(
            authorization = "Bearer $apiKey",
            request = request
        )

        return response.images.firstOrNull()?.url
            ?: error("No image URL returned from SiliconFlow.")
    }

    companion object {
        val instance: SiliconFlowRepository by lazy {
            SiliconFlowRepository(SiliconFlowApi.createService())
        }
    }
}
