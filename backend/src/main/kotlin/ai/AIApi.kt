package com.example.ai

interface AIApi {
    suspend fun getImageAltText(url: String): String
    suspend fun getUrlDescription(url: String, text: String): String

}