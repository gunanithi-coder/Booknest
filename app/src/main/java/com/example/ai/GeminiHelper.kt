package com.example.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiHelper {
    private const val TAG = "GeminiHelper"
    private const val MODEL_NAME = "gemini-3.5-flash"

    // OkHttpClient with generous timeouts as recommended by skill
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Call the Gemini API server-side using standard REST
     */
    suspend fun getBookAdvice(prompt: String, systemInstruction: String = ""): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is not configured. Falling back to offline response engine.")
            return@withContext getOfflineResponse(prompt)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
            
            // Build the JSON request body
            val requestBodyJson = JSONObject()
            
            // Add contents
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject().apply {
                put("text", prompt)
            }
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            requestBodyJson.put("contents", contentsArray)

            // Add system instruction if provided
            if (systemInstruction.isNotEmpty()) {
                val sysInstObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPartObj = JSONObject().apply {
                    put("text", systemInstruction)
                }
                sysPartsArray.put(sysPartObj)
                sysInstObj.put("parts", sysPartsArray)
                requestBodyJson.put("systemInstruction", sysInstObj)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
            
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errMsg = response.body?.string() ?: ""
                    Log.e(TAG, "Unsuccessful response from Gemini: Code ${response.code}, Msg: $errMsg")
                    return@withContext "I'm sorry, I encountered an issue speaking to our literary AI index: Code ${response.code}. Here's some offline guidance instead:\n\n${getOfflineResponse(prompt)}"
                }

                val responseBody = response.body?.string() ?: ""
                val responseJson = JSONObject(responseBody)
                
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val content = candidates.getJSONObject(0).optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text", "No response content found.")
                        }
                    }
                }
                return@withContext "Apologies, the electronic library indexes didn't report any conclusions."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini helper call", e)
            return@withContext "Connectivity interruption in our literary network index: ${e.localizedMessage}. Seeding offline suggestions:\n\n${getOfflineResponse(prompt)}"
        }
    }

    /**
     * Fallback mock engine loaded with authentic smart catalog/search responses
     */
    private fun getOfflineResponse(prompt: String): String {
        val lowerPrompt = prompt.lowercase()
        return when {
            lowerPrompt.contains("recommend") || lowerPrompt.contains("suggest") || lowerPrompt.contains("find") -> {
                """
                📚 **BookNest AI Curated Matches** (Offline Engine)
                
                Here are high-demand listings matching your inquiry:
                
                1. **Kotlin in Action** by Dmitry Jemerov ($38.50)
                   *Category:* Programming Books
                   *Why it fits:* The ultimate, developer-vouched roadmap to master modern native Android programming.
                   
                2. **Atomic Habits** by James Clear ($15.00)
                   *Category:* Self-Help / Business Books
                   *Why it fits:* Incredible strategic layout on building sustainable daily performance routines.
                   
                3. **The Hobbit (Collector's Leatherbound)** ($115.00)
                   *Category:* Rare and Collectible Books
                   *Why it fits:* Premium heirloom quality with fully colored illustrations. Excellent valuation holding.
                   
                *Would you like to browse these categories directly or perform a keyword search?*
                """.trimIndent()
            }
            lowerPrompt.contains("sell") || lowerPrompt.contains("price") -> {
                """
                ⚖️ **BookNest AI Listing Valuation Check**
                
                To optimize your book's sale velocity:
                - **Programming / Technical Books:** Mark 60-70% of retail price if in "New/Like New" state. Demands are highest in September and January.
                - **Academic Textbooks:** Always list the exact **ISBN-13** so college students verify registration codes.
                - **Novels / Fiction:** Price competitive ($5-$12) as volume is high.
                
                *Tip: Select one of our beautiful gradient presets to make your book's graphic card catch a reader's eyes instantly!*
                """.trimIndent()
            }
            else -> {
                """
                📖 **Welcome to the BookNest Curation Services**
                
                As your BookNest Senior Assistant, I can help you:
                - Formulate optimized book descriptions for listing sales.
                - Recommend matching volumes based on your personal reading history.
                - Provide current second-hand valuation benchmarks for rare editions.
                
                *Configure your GEMINI_API_KEY in the Secrets panel to activate live cloud-synced generation!*
                """.trimIndent()
            }
        }
    }
}
