package com.example.perfectweatherapp.Extentions

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser

class Utf8StringRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : Request<String>(method, url, errorListener) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
        val utf8String = String(response.data, charset("UTF-8"))
        return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: String) {
        listener.onResponse(response)
    }
} // функция для получения города на русском