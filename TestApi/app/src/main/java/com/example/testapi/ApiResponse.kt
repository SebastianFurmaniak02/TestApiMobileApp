package com.example.testapi

import android.os.AsyncTask
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL


class ApiResponse {

    interface OnApiResultListener {
        fun onResult(result: String?)
    }

    var onApiResultListener: OnApiResultListener? = null

    private suspend fun fetchData(apiUrl: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val responseCode: Int = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader: BufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                    var line: String?
                    val response = StringBuilder()
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    response.toString()
                } else {
                    Log.e("USER_LOG", "Unable to fetch data from API. Response code: $responseCode")
                    null
                }
            } catch (e: Exception) {
                Log.e("USER_LOG", "Error fetching data from API: ${e.message}")
                null
            }
        }
    }

    suspend fun fetchAndNotify(apiUrl: String) {
        val result = fetchData(apiUrl)
        onApiResultListener?.onResult(result)
    }
}