package com.example.checkins.Actividades

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.*
import com.example.checkins.Foursquare.Foursquare
import com.example.checkins.Foursquare.Venue
import com.example.checkins.Interfaces.ObtenerVenuesInterface
import com.example.checkins.Interfaces.UbicacionListener
import com.example.checkins.Interfaces.VenuesPorLikeInterface
import com.example.checkins.RecyclerViewPrincipal.AdaptadorCustom
import com.example.checkins.RecyclerViewPrincipal.ClickListener
import com.example.checkins.RecyclerViewPrincipal.LongClickListener
import com.example.checkins.Utilidades.Ubicacion
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson

class PantallaPrincipal : AppCompatActivity() {

    var ubicacion: Ubicacion? = null
    var foursquare: Foursquare? = null

    var lista: RecyclerView? = null
    var adaptador:AdaptadorCustom?=null
    var layoutManager: RecyclerView.LayoutManager? = null

    var toolbar:Toolbar? = null
companion object{
    val VENUE_ACTUAL ="checkins.PantallaPrincipal"


}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        //Solucionar el activityDestino
        foursquare = Foursquare(this, this)

        lista = findViewById(R.id.recyclerViewCiudades)
        lista?.setHasFixedSize(true)

        initToolbar()

        //Creamos el layout Manager y lo asociamos al recycler view
        layoutManager = LinearLayoutManager(this)
        lista?.layoutManager = layoutManager

        if (foursquare?.hayToken()!!){
            ubicacion = Ubicacion(this, object :
                UbicacionListener {
                override fun ubicacionResponse(locationResult: LocationResult) {
                    val lat = locationResult.lastLocation.latitude.toString()
                    val lon = locationResult.lastLocation.longitude.toString()
                    //Toast.makeText(applicationContext,locationResult.lastLocation.latitude.toString(),Toast.LENGTH_SHORT).show()

                    foursquare?.obtenerVenues(lat, lon, object :
                        ObtenerVenuesInterface {
                        override fun venuesGenerados(venues: ArrayList<Venue>) {
                            implementacionRecyclerView(venues)

                            /*for (venue in venues){
                               Log.d("Venue",venue.name)
                           }*/
                        }

                    })
                }

            })

        }else{
            foursquare?.mandarIniciarSesion()
        }



    }

    private fun implementacionRecyclerView(lugares:ArrayList<Venue>){
        adaptador = AdaptadorCustom(lugares, object: ClickListener {
            override fun onClick(vista: View, index: Int) {
                Toast.makeText(applicationContext,lugares.get(index).name,Toast.LENGTH_SHORT).show()
                val venueToJson = Gson()
                val venueActualString = venueToJson.toJson(lugares.get(index))
                //Log.d("venueActualString",venueActualString)

                val intent = Intent(applicationContext,
                    DetallesVenue::class.java)
                intent.putExtra(VENUE_ACTUAL,venueActualString)
                startActivity(intent)


            }

        },object: LongClickListener {
            override fun longClick(vista: View, index: Int) {
             /*   Toast.makeText(applicationContext,"Prueba",Toast.LENGTH_SHORT).show()
                if (!isActionMode){
                    startSupportActionMode(callBack)
                    isActionMode = true
                    adaptador?.seleccionarItem(index)

                }
                else{
                    adaptador?.seleccionarItem(index)
                }
                actionMode?.title= adaptador?.obtenerNumeroElementosSeleccionados().toString() + " Seleccionados"
            */}

        })

        lista?.adapter = adaptador
    }

    fun initToolbar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal,menu)

        return super.onCreateOptionsMenu(menu)
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.iconoCategorias ->{
                val intent = Intent(this, Categorias::class.java)
                startActivity(intent)
                return true

            }
            R.id.iconoFavoritos->{
                val intent = Intent(this, Likes::class.java)
                startActivity(intent)
                return true
            }
            R.id.iconoPerfil->{
                val intent = Intent(this, Perfil::class.java)
                startActivity(intent)
                return true

            }
            R.id.iconoCerrarSesión->{
                foursquare?.cerrarSesion()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        ubicacion?.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    override fun onStart() {
        super.onStart()
        ubicacion?.inicializarUbicacion()
    }

    override fun onPause() {
        super.onPause()
        ubicacion?.detenerActualizacionUbicacion()
    }
}
