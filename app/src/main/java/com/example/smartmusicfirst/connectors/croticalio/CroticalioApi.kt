package com.example.smartmusicfirst.connectors.croticalio

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.smartmusicfirst.DEBUG_TAG
import com.example.smartmusicfirst.models.KeywordCroticalio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object CroticalioApi {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    suspend fun findKeyWords(
        accessToken: String,
        searchQuery: String
    ): List<KeywordCroticalio> = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            val url = "https://gw.cortical.io/nlp/keywords?limit=10"
            val requestBodyJson = JSONObject().put("text", searchQuery)

            val request = object : JsonObjectRequest(
                Method.POST,
                url,
                requestBodyJson,
                Response.Listener { response ->
                    try {
                        val keywordsJson = response.getJSONArray("keywords")
                        val keywords = mutableListOf<KeywordCroticalio>()
                        for (i in 0 until keywordsJson.length()) {
                            val keywordJson = keywordsJson.getJSONObject(i)
                            val posTagsJson = keywordJson.getJSONArray("pos_tags")
                            val posTags = mutableListOf<String>()
                            for (j in 0 until posTagsJson.length()) {
                                posTags.add(posTagsJson.getString(j))
                            }
                            val keyword = KeywordCroticalio(
                                word = keywordJson.getString("word"),
                                documentFrequency = keywordJson.getDouble("document_frequency"),
                                posTags = posTags,
                                score = keywordJson.getDouble("score")
                            )
                            keywords.add(keyword)
                        }
                        continuation.resume(keywords)
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                        Log.e(DEBUG_TAG, "Error parsing keywords: ${e.message}", e)
                    }
                },
                Response.ErrorListener { error ->
                    continuation.resumeWithException(error)
                    Log.e(DEBUG_TAG, "Error getting keywords: ${error.message}", error)
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $accessToken"
                    headers["Content-Type"] = "application/json"
                    headers["Accept"] = "application/json"
                    return headers
                }
            }

            Volley.newRequestQueue(applicationContext).add(request)

        }
    }
}
