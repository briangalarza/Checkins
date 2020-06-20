package com.example.checkins.Utilidades

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import com.example.checkins.Interfaces.UbicacionListener
import com.example.checkins.Mensajes.Errores
import com.example.checkins.Mensajes.Mensaje
import com.example.checkins.Mensajes.Mensajes
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class Ubicacion (var activity: AppCompatActivity, ubicacionListener: UbicacionListener) {
    private val permisoFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

    private val CODIGO_SOLICITUD_PERMISO = 100

    private var fusedLocationClient: FusedLocationProviderClient? = null

    //Que tan especifico se encuentra
    var locationRequest: LocationRequest? = null

    //Respuesta de la ubicación
    var callback: LocationCallback? = null

    init {
        fusedLocationClient = FusedLocationProviderClient(activity.applicationContext)
        inicializarLocationRequest()

        callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {

                super.onLocationResult(locationResult)

                ubicacionListener.ubicacionResponse(locationResult!!)
            }
        }
    }

    /**
     * Inicia los atributos de location request
     */
    private fun inicializarLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest?.interval = 100000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Retorna tue en caso de que existan los permisos de ubicación
     */
    private fun validarPermisoUbicacion():Boolean{

        val hayUbicacionPrecisa = ActivityCompat.checkSelfPermission(activity.applicationContext,permisoFineLocation) == PackageManager.PERMISSION_GRANTED
        val hayUbicacionOrdinaria = ActivityCompat.checkSelfPermission(activity.applicationContext,permisoCoarseLocation) == PackageManager.PERMISSION_GRANTED

        return hayUbicacionOrdinaria && hayUbicacionPrecisa
    }

    /**
     * Solicita los permisos
     */
    private fun pedirPermisos(){
        val deboProveerPermiso = ActivityCompat.shouldShowRequestPermissionRationale(activity,permisoFineLocation)
        //En ambos casos debo pedir el permiso
        if (deboProveerPermiso){
            //Mandar un mensaje solicitando permisos
            Mensaje.mensaje(activity.applicationContext,
                Mensajes.RATIONALE)
        }
        solicitudPermiso()
    }

    /**
     * Solicitud del permiso
     */
    private fun solicitudPermiso(){
       //ActivityCompat
        requestPermissions(activity,arrayOf(permisoFineLocation,permisoCoarseLocation),CODIGO_SOLICITUD_PERMISO)

    }

    /**
     * Metodo que obtiene la ubicación una vez dado el permiso
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            CODIGO_SOLICITUD_PERMISO ->{
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    obtenerUbicacion()

                }else{
                    Mensaje.mensajeError(activity.applicationContext,
                        Errores.ERROR_PERMISO_NEGADO)
                }

            }
        }
    }
    /**
     *  Detiene la actualización de la ubicacion
     */

    fun detenerActualizacionUbicacion(){
        this.fusedLocationClient?.removeLocationUpdates(callback)
    }

    fun inicializarUbicacion() {
        if (validarPermisoUbicacion()) {
            obtenerUbicacion()
        } else {
            pedirPermisos()
        }
    }

    /**
     * Le indicamos que ignore el tema permisos debido a que solo es solicitado cuando ya existen
     */
    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion(){
        validarPermisoUbicacion()
        fusedLocationClient?.requestLocationUpdates(locationRequest,callback,null)
    }


}