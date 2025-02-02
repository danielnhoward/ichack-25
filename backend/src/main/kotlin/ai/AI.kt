package com.example.ai

interface AI {
    fun getImageAltText(url: String): String

    fun getUrlDescription(
        url: String,
        text: String,
    ): String

    fun getLanguage(string: String): String
}
