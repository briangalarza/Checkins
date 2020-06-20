package com.example.checkins.Utilidades

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo


import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.checkins.Interfaces.HTTPResponse
import com.example.checkins.Mensajes.Errores
import com.example.checkins.Mensajes.Mensaje

class Network(var activity: AppCompatActivity) {



        fun hayRed(): Boolean {
            val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            return isConnected


        }

        fun httpRequest(context: Context, url: String, httpResponse: HTTPResponse) {
            if (hayRed()) {
                val queue = Volley.newRequestQueue(context)

                val solicitud =
                    StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->

                        httpResponse.httpResponseSuccess(response)


                    }, Response.ErrorListener { error ->
                        Log.d("HTTP_Request", error.message.toString())
                        Mensaje.mensajeError(context, Errores.HTTP_ERROR)
                    })

                queue.add(solicitud)

            } else {
                Mensaje.mensajeError(context, Errores.NO_HAY_RED)

            }


        }

    fun httpPOSTRequest(context: Context, url: String, httpResponse: HTTPResponse) {
        if (hayRed()) {
            val queue = Volley.newRequestQueue(context)

            val solicitud =
                StringRequest(Request.Method.POST, url, Response.Listener<String> { response ->

                    httpResponse.httpResponseSuccess(response)


                }, Response.ErrorListener { error ->
                    Log.d("HTTP_Request", error.message.toString())
                    Mensaje.mensajeError(context, Errores.HTTP_ERROR)
                })

            queue.add(solicitud)

        } else {
            Mensaje.mensajeError(context, Errores.NO_HAY_RED)

        }


    }



}