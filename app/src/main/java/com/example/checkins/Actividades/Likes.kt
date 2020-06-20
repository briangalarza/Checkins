package com.example.checkins.Actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.Foursquare.Foursquare
import com.example.checkins.Foursquare.Venue
import com.example.checkins.Interfaces.ObtenerVenuesInterface
import com.example.checkins.Interfaces.UbicacionListener
import com.example.checkins.Interfaces.VenuesPorLikeInterface
import com.example.checkins.R
import com.example.checkins.RecyclerViewPrincipal.AdaptadorCustom
import com.example.checkins.RecyclerViewPrincipal.ClickListener
import com.example.checkins.RecyclerViewPrincipal.LongClickListener
import com.example.checkins.Utilidades.Ubicacion
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson

class Likes : AppCompatActivity() {


    var foursquare: Foursquare? = null
    var lista: RecyclerView? = null
    var adaptador: AdaptadorCustom?=null
    var layoutManager: RecyclerView.LayoutManager? = null

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_likes)




        lista = findViewById(R.id.recyclerViewCiudades)
        lista?.setHasFixedSize(true)

        foursquare = Foursquare(this, this)

        layoutManager = LinearLayoutManager(this)
        lista?.layoutManager = layoutManager

        initToolbar()

        if (foursquare?.hayToken()!!){
            foursquare?.obtenerVenuesDeLike(object: VenuesPorLikeInterface{
                override fun venuesGenerados(venues: ArrayList<Venue>) {
                    implementacionRecyclerView(venues)
                }

            })

        }else{
            foursquare?.mandarIniciarSesion()
        }
    }

    fun initToolbar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.app_likes)
        setSupportActionBar(toolbar)

        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener { finish() }

    }

    private fun implementacionRecyclerView(lugares:ArrayList<Venue>){
        adaptador = AdaptadorCustom(lugares, object: ClickListener {
            override fun onClick(vista: View, index: Int) {
                Toast.makeText(applicationContext,lugares.get(index).name, Toast.LENGTH_SHORT).show()
                val venueToJson = Gson()
                val venueActualString = venueToJson.toJson(lugares.get(index))
                val intent = Intent(applicationContext,
                    DetallesVenue::class.java)
                intent.putExtra(PantallaPrincipal.VENUE_ACTUAL,venueActualString)
                startActivity(intent)


            }

        },object: LongClickListener {
            override fun longClick(vista: View, index: Int) {
            }

        })

        lista?.adapter = adaptador
    }

}
