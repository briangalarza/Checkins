package com.example.checkins.Foursquare

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.checkins.Actividades.MainActivity
import com.example.checkins.Interfaces.*
import com.example.checkins.Mensajes.Errores
import com.example.checkins.Mensajes.Mensaje
import com.example.checkins.Mensajes.Mensajes
import com.example.checkins.Utilidades.Network
import com.foursquare.android.nativeoauth.FoursquareOAuth
import com.google.gson.Gson

class Foursquare(var activity:AppCompatActivity, var activityDestino:AppCompatActivity) {

    private val CODIGO_CONEXION = 200
    private val CODIGO_INTERCAMBIO_TOKEN = 201

    private val CLIENT_ID = "LRQKUKDQERJOCP2QE1Y0FLXELF15BJM4HMLTUOPH0340QEGN"
    private val CLIENT_SECRET = "XYTF1EJX15UJ5KUSIFIFROGJFWCD2SV3ENO0XSPJC0ZOBEGR"

    private val SETTINGS = "Settings"
    private val ACCESS_TOKEN = "accessToken"

    private val URL_BASE = "https://api.foursquare.com/v2/"

    private val VERSION ="v=20200110"

    init{

    }
/**
Metodo para verificar e iniciar sesión con la api de Foursquare
 */
    fun iniciarSesion(){
        val intent = FoursquareOAuth.getConnectIntent(activity.applicationContext,CLIENT_ID)
        //Verificamos si existe la app
        if(FoursquareOAuth.isPlayStoreIntent(intent)){
            //Mostrar mensaje de que no tiene la app
            Mensaje.mensajeError(
                activity.applicationContext,
                Errores.NO_HAY_APP_FSQR
            )
            activity.startActivity(intent)
        }else{
            activity.startActivityForResult(intent,CODIGO_CONEXION)
        }
    }

    fun cerrarSesion(){
        val settings = activity.getSharedPreferences(SETTINGS,0)
        val editor = settings.edit()
        editor.putString(ACCESS_TOKEN,"")
        editor.apply()

    }


    /**
    Valida los codigos obtenidos como resultados de las conexiones
     */
     fun validarActivityResult(requestCode:Int,resultCode:Int,data: Intent?){
        when (requestCode){
            CODIGO_CONEXION->{conexionCompleta(resultCode,data)}

            CODIGO_INTERCAMBIO_TOKEN->{intercambioTokenCompleta(resultCode,data)}
        }

    }

    /**
     * Metodo para autenticar la conexión
     */
    private fun conexionCompleta(resultCode: Int,data: Intent?){
        val codigoRespuesta = FoursquareOAuth.getAuthCodeFromResult(resultCode,data)
        val exception = codigoRespuesta.exception

        if(exception == null){
            //Autenticación correcta
            val codigo = codigoRespuesta.code
            realizarIntercambioToken(codigo)
        }else{
            Mensaje.mensajeError(
                activity.applicationContext,
                Errores.ERROR_CONEXION_FSQR
            )
        }

    }

    /**
     * Inicia el intercambio con el token
     */
    private fun realizarIntercambioToken(codigo:String){
        val intent = FoursquareOAuth.getTokenExchangeIntent(activity.applicationContext,CLIENT_ID,CLIENT_SECRET,codigo)
        activity.startActivityForResult(intent,CODIGO_INTERCAMBIO_TOKEN)
    }

    /**
     * Obtiene el token de la conexión
     */
    private fun intercambioTokenCompleta(resultCode: Int,data: Intent?){
        val respuestaToken = FoursquareOAuth.getTokenFromResult(resultCode,data)
        val exception = respuestaToken.exception
        //Si no hubo problemas
        if (exception == null){
           //Almacena el token
            val accessToken = respuestaToken.accessToken
           if (!guardarToken(accessToken)){
               Mensaje.mensajeError(
                   activity.applicationContext,
                   Errores.ERROR_GUARDAR_TOKEN
               )
           }else{
               //Revisar donde se manda al siguiente activity
               navegarSiguienteActividad()
           }

        }else{
            //Hubo un problema al obtener token
            Mensaje.mensajeError(
                activity.applicationContext,
                Errores.ERROR_INTERCAMBIO_TOKEN
            )

        }
    }

    /**
     * Comprueba si el token existe
     */
    fun hayToken():Boolean{
        if (obtenerToken()==""){
            return false
        }else{
            return true
        }

    }

    /**
     * Obtiene el token almacenado por Shared Preferences
     */
    private fun obtenerToken():String{
        val settings = activity.getSharedPreferences(SETTINGS,0)
        val token = settings.getString(ACCESS_TOKEN,"")
        return token!!
    }

    /**
     * Almacena el token en SharedPreferences
     */
    private fun guardarToken(token:String):Boolean{
        if(token.isEmpty()){
            return false
        }
        val settings = activity.getSharedPreferences(SETTINGS,0)
        val editor = settings.edit()
        editor.putString(ACCESS_TOKEN,token)
        editor.commit()

        return true
    }

    /**
     * Transición al segundo activity en caso de tener exito
     */
    fun navegarSiguienteActividad(){
        activity.startActivity(Intent(this.activity,activityDestino::class.java))
        activity.finish()
    }


    fun mandarIniciarSesion(){
        activity.startActivity(Intent(this.activity,MainActivity::class.java))
        activity.finish()
    }

    /**
     * Recupera la lista de lugares
     */
    fun obtenerVenues(lat:String,lon:String,obtenerVenuesInterface: ObtenerVenuesInterface){
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "search/"
        val ll = "ll=" + lat + "," + lon
        val token = "oauth_token=" +obtenerToken()
        val url = URL_BASE + seccion + metodo + "?" + ll + "&" + token + "&" + VERSION
        network.httpRequest(activity.applicationContext,url,object:
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                var gson = Gson()
                var objectoRespuesta =gson.fromJson(response, FoursquareAPIRequestVenues::class.java)

                var meta = objectoRespuesta.meta
                var venues = objectoRespuesta.response?.venues!!



                if(meta?.code==200){
                    //Mandar mensaje de que se completó el quey correctamente


                    obtenerVenuesInterface.venuesGenerados(venues)
                    for (venue in venues){
                        obtenerImagePreview(venue.id, object: ImagePreviewInterface{
                            override fun obtenerImagePreview(photos: ArrayList<Photo>) {
                                if (photos.count()>0){
                                    //Cargamos las imagenes
                                    val urlImagen = photos.get(0).construirURLImagen(obtenerToken(),VERSION,"original")
                                    venue.imagePreview = urlImagen

                                    if (venue.categories?.count()!! > 0){
                                        var urlIcono = venue.categories?.get(0)?.icon?.construirURLImagen(obtenerToken(),VERSION,"64")
                                        venue.iconCategory = urlIcono

                                    }


                                }

                            }

                        })
                    }
                }else{
                    if(meta?.code ==400){
                        //mostrar problema al usuario
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    }else{
                        //mostrar un mensaje genérico
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }
            }

        })

    }

    fun obtenerVenuesDeLike(venuesPorLikeInterface: VenuesPorLikeInterface){
        val network = Network(activity)
        val seccion = "users/"
        val metodo = "self/"
        val token = "oauth_token=" +obtenerToken()
        val url = URL_BASE + seccion + metodo + "venuelikes?limit=10" +  "&" + token + "&" + VERSION

        network.httpRequest(activity.applicationContext,url,object:
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                var gson = Gson()
                var objectoRespuesta =gson.fromJson(response, VenuesDeLike::class.java)

                var meta = objectoRespuesta.meta
                var venues = objectoRespuesta.response?.venues?.items!!


                if(meta?.code==200){
                    //Mandar mensaje de que se completó el quey correctamente
                    venuesPorLikeInterface.venuesGenerados(venues)
                }else{
                    if(meta?.code ==400){
                        //mostrar problema al usuario
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    }else{
                        //mostrar un mensaje genérico
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }
            }

        })

    }


    private fun obtenerImagePreview(venueId:String, imagePreviewInterface: ImagePreviewInterface){
        val network = Network(activity)
        val seccion = "venues/"
        val metodo =  "photos/"
        val token = "oauth_token=" +obtenerToken()
        val parametros = "limit=1"

        val url = URL_BASE + seccion + venueId + "/" + metodo + "?" + parametros  + "&" + token + "&" + VERSION

        network.httpRequest(activity.applicationContext,url,object:
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                var gson = Gson()
                var objectoRespuesta =gson.fromJson(response, ImagePreviewVenueResponse::class.java)

                var meta = objectoRespuesta.meta
                var photos = objectoRespuesta.response?.photos?.items
                Log.d("Error",photos.toString())
                if(meta?.code==200){
                    //Mandar mensaje de que se completó el quey correctamente
                    imagePreviewInterface.obtenerImagePreview(photos!!)

                }else{
                    if(meta?.code ==400){
                        //mostrar problema al usuario
                      /*  Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )*/
                    }else{
                        //mostrar un mensaje genérico
                       /* Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )*/
                    }
                }
            }

        })
    }




    fun nuevoCheckin(id:String, location: Location, mensaje:String){
        val network = Network(activity)

        val seccion = "checkins/"
        val metodo = "add/"
        val token = "oauth_token=" +obtenerToken()
        val query = "?venuesId=" + id + "&shout=" + mensaje + "&ll=" + location.lat.toString() + "," + location.lng.toString() + "&" + token + "&" + VERSION

        val url = URL_BASE + seccion + metodo + query

        network.httpPOSTRequest(activity.applicationContext,url,object:
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                Log.d("response", response)
                var gson = Gson()
                var objectoRespuesta =gson.fromJson(response, FoursquareAPInuevoCheckin::class.java)


                var meta = objectoRespuesta.meta

                if(meta?.code==200){
                    //Mandar mensaje de que se completó el quey correctamente
                    Mensaje.mensaje(activity.applicationContext,Mensajes.CHECKIN_SUCCESS)
                }else{
                    if(meta?.code ==400){
                        //mostrar problema al usuario
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    }else{
                        //mostrar un mensaje genérico
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }

            }
        })
    }


    fun nuevoLike(id:String){
        val network = Network(activity)

        val seccion = "venues/"
        val metodo = "like/"
        val token = "oauth_token=" +obtenerToken()
        val query = "?" + token +  "&" + VERSION

        val url = URL_BASE + seccion + id + "/" + metodo + query

        network.httpPOSTRequest(activity.applicationContext,url,object:
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                Log.d("response", response)
                var gson = Gson()
                var objectoRespuesta =gson.fromJson(response, FoursquareAPInuevoCheckin::class.java)


                var meta = objectoRespuesta.meta

                if(meta?.code==200){
                    //Mandar mensaje de que se completó el quey correctamente
                    Mensaje.mensaje(activity.applicationContext,Mensajes.LIKE_SUCCESS)
                }else{
                    if(meta?.code ==400){
                        //mostrar problema al usuario
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    }else{
                        //mostrar un mensaje genérico
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }

            }
        })
    }

    fun obtenerUsuarioActual(usuarioActualInterface: UsuariosInterface){
        val network = Network(activity)

        val seccion = "users/"
        val metodo = "self/"
        val token = "oauth_token=" +obtenerToken()
        val query = "?" + token + "&" + VERSION

        val url = URL_BASE + seccion + metodo + query

        network.httpRequest(activity.applicationContext,url,object:
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                Log.d("response", response)
                var gson = Gson()
                var objectoRespuesta =gson.fromJson(response, FoursquareAPISelfUser::class.java)


                var meta = objectoRespuesta.meta

                if(meta?.code==200){
                    //Mandar mensaje de que se completó el quey correctamente

                    val usuario = objectoRespuesta?.response?.user!!
                    usuario.photo?.construirURLImagen(obtenerToken(),VERSION,"128x128")
                    usuarioActualInterface.obtenerUsuarioActual(objectoRespuesta.response?.user!!)


                }else{
                    if(meta?.code ==400){
                        //mostrar problema al usuario
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    }else{
                        //mostrar un mensaje genérico
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }

            }
        })

    }

    fun cargarCategorias(categoriasInterface:CategoriasVenuesInterface){

        val network = Network(activity)

        val seccion = "venues/"
        val metodo = "categories/"
        val token = "oauth_token=" +obtenerToken()
        val query = "?" + token + "&" + VERSION

        val url = URL_BASE + seccion + metodo + query

        network.httpRequest(activity.applicationContext,url,object:
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                Log.d("response", response)
                var gson = Gson()
                var objectoRespuesta =gson.fromJson(response, FoursquareAPICategorias::class.java)


                var meta = objectoRespuesta.meta
                val categories = objectoRespuesta.response?.categories!!


                if(meta?.code==200){
                    //Mandar mensaje de que se completó el quey correctamente
                   for(categoria in categories){
                       categoria.icon?.construirURLImagen(obtenerToken(),VERSION,"bg_64")!!
                   }
                    categoriasInterface.categoriasVenues(objectoRespuesta.response?.categories!!)


                }else{
                    if(meta?.code ==400){
                        //mostrar problema al usuario
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    }else{
                        //mostrar un mensaje genérico
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }

            }
        })

    }

    fun obtenerVenues(lat:String,lon:String,categoryId:String, obtenerVenuesInterface: ObtenerVenuesInterface){
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "search/"
        val ll = "ll=" + lat + "," + lon
        val categoria ="categoryId=" + categoryId
        val token = "oauth_token=" +obtenerToken()
        val url = URL_BASE + seccion + metodo + "?" + ll + "&" + categoria + "&" + token + "&" + VERSION
        network.httpRequest(activity.applicationContext,url,object:
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                var gson = Gson()
                var objectoRespuesta =gson.fromJson(response, FoursquareAPIRequestVenues::class.java)

                var meta = objectoRespuesta.meta
                var venues = objectoRespuesta.response?.venues!!



                if(meta?.code==200){
                    //Mandar mensaje de que se completó el quey correctamente
                    obtenerVenuesInterface.venuesGenerados(venues)
                }else{
                    if(meta?.code ==400){
                        //mostrar problema al usuario
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    }else{
                        //mostrar un mensaje genérico
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }
            }

        })

    }



}