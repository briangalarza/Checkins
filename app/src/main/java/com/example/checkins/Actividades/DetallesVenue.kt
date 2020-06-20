package com.example.checkins.Actividades

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.checkins.Foursquare.Foursquare
import com.example.checkins.Foursquare.Rejilla
import com.example.checkins.Interfaces.UsuariosInterface
import com.example.checkins.R
import com.example.checkins.Foursquare.User
import com.example.checkins.Foursquare.Venue
import com.example.checkins.GridViewDetalleVenue.AdaptadorGridView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detalles_venue.*
import java.net.URLEncoder

class DetallesVenue : AppCompatActivity() {

    var toolbar: Toolbar? = null

    var bCheckin:Button? = null
    var bLike: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_venue)

        bCheckin = findViewById(R.id.bCheckin)
        bLike = findViewById(R.id.bLike)

        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvState = findViewById<TextView>(R.id.tvState)
        val tvCountry = findViewById<TextView>(R.id.tvCountry)
        val tvCategory = findViewById<TextView>(R.id.tvCategory)
        val tvCheckins = findViewById<TextView>(R.id.tvCheckins)
        val tvUsers = findViewById<TextView>(R.id.tvUsers)
        val tvTips = findViewById<TextView>(R.id.tvTips)
        val ivFoto = findViewById<ImageView>(R.id.ivFoto)

        val rejilla = findViewById<GridView>(R.id.gridRejilla)

        val venueActualString = intent.getStringExtra(PantallaPrincipal.VENUE_ACTUAL)
        val gson = Gson()
        val venueActual = gson.fromJson(
            venueActualString,
            Venue::class.java
        )

        val listaRejilla = ArrayList<Rejilla>()
        // Log.d("Venue",venueActual.name)

        initToolbar(venueActual.name)

        tvNombre.text = venueActual.name
        tvState.text = venueActual.location?.state
        tvCountry.text = venueActual.location?.country
        tvCategory.text = venueActual.categories?.get(0)?.name
        tvCheckins.text = venueActual.stats?.checkinsCount.toString()
        tvUsers.text = venueActual.stats?.usersCount.toString()
        tvTips.text = venueActual.stats?.tipCount.toString()
        Picasso.get().load(venueActual.imagePreview).placeholder(R.drawable.placeholder_venue).into(ivFoto)

        listaRejilla.add(Rejilla(venueActual.categories?.get(0)?.name!!,R.drawable.icono_categorias, ContextCompat.getColor(this,R.color.secondaryLightColor)))
        listaRejilla.add(Rejilla(venueActual.stats?.checkinsCount.toString(),R.drawable.icono_checkin,ContextCompat.getColor(this,R.color.secondaryLightColor)))
        listaRejilla.add(Rejilla(venueActual.stats?.usersCount.toString(),R.drawable.icono_ubicacion,ContextCompat.getColor(this,R.color.secondaryLightColor)))
        listaRejilla.add(Rejilla(venueActual.stats?.tipCount.toString(),R.drawable.icono_favoritos,ContextCompat.getColor(this,R.color.secondaryLightColor)))

        val adaptador = AdaptadorGridView(applicationContext,listaRejilla)

        gridRejilla.adapter = adaptador



        val foursquare = Foursquare(
            this,
            DetallesVenue()
        )



        bCheckin?.setOnClickListener {
            if (foursquare.hayToken()) {
                val etMensaje = EditText(this)
                etMensaje.hint = "Hola"

                AlertDialog.Builder(this)
                    .setTitle("Nuevo Check-in")
                    .setMessage("Ingresa un mensaje")
                    .setView(etMensaje)
                    .setPositiveButton("Check-in",DialogInterface.OnClickListener { dialog, which ->
                        val mensaje = URLEncoder.encode(etMensaje.text.toString(), "UTF-8")
                        foursquare.nuevoCheckin(venueActual.id,venueActual.location!!,mensaje)

                    })
                    .setNegativeButton("Cancelar",DialogInterface.OnClickListener { dialog, which ->  })
                    .show()

            }else{
                foursquare?.mandarIniciarSesion()
            }
        }

        bLike?.setOnClickListener {
            if (foursquare.hayToken()) {
                foursquare.nuevoLike(venueActual.id)
            }
            else{
                foursquare?.mandarIniciarSesion()
            }
        }
    }


    fun initToolbar(venue:String){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(venue)
        setSupportActionBar(toolbar)

        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener { finish() }

    }
}
