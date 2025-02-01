package com.example.ai

interface AIApi {
    suspend fun getImageAltText(url: String): String
}