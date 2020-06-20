package com.example.checkins.Actividades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.checkins.Foursquare.Foursquare
import com.example.checkins.Foursquare.Rejilla
import com.example.checkins.Foursquare.User
import com.example.checkins.GridViewDetalleVenue.AdaptadorGridView
import com.example.checkins.Interfaces.UsuariosInterface
import com.example.checkins.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_detalles_venue.*
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_perfil.gridRejilla
import kotlinx.android.synthetic.main.activity_perfil.ivFoto


class Perfil : AppCompatActivity() {

    var foursquare: Foursquare? = null


    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvFriends = findViewById<TextView>(R.id.tvFriends)
        val tvTips = findViewById<TextView>(R.id.tvTips)
        val tvPhotos = findViewById<TextView>(R.id.tvPhotos)
        val tvCheckins = findViewById<TextView>(R.id.tvCheckins)
        val rejilla = findViewById<GridView>(R.id.gridRejilla)

        val ivFoto = findViewById<CircleImageView>(R.id.ivFoto)

        foursquare = Foursquare(this, this)

        initToolbar("hola")

        if (foursquare?.hayToken()!!){
            foursquare?.obtenerUsuarioActual(object: UsuariosInterface{
                override fun obtenerUsuarioActual(usuario: User) {
                    tvNombre.text = usuario.firstName
                    tvFriends.text = String.format("%d %s",usuario.friends?.count , getString(R.string.app_perfil_friends))
                    tvTips.text = String.format("%d %s",usuario.tips?.count, getString(R.string.app_perfil_tips))
                    tvPhotos.text = String.format("%d %s",usuario.photos?.count, getString(R.string.app_perfil_photos))
                    tvCheckins.text = String.format("%d %s",usuario.checkins?.count, getString(R.string.app_perfil_checkins))
                    initToolbar(usuario.firstName)

                    val listaRejilla = ArrayList<Rejilla>()
                    Picasso.get().load(usuario.photo?.urlIcono).into(ivFoto)

                    listaRejilla.add(Rejilla(String.format("%d %s",usuario.friends?.count , getString(R.string.app_perfil_friends)),R.drawable.icono_categorias, ContextCompat.getColor(applicationContext,R.color.secondaryLightColor)))
                    listaRejilla.add(
                        Rejilla(String.format("%d %s",usuario.tips?.count, getString(R.string.app_perfil_tips)),R.drawable.icono_checkin,
                            ContextCompat.getColor(applicationContext,R.color.secondaryLightColor))
                    )
                    listaRejilla.add(
                        Rejilla(String.format("%d %s",usuario.photos?.count, getString(R.string.app_perfil_photos)),R.drawable.icono_ubicacion,
                            ContextCompat.getColor(applicationContext,R.color.secondaryLightColor))
                    )
                    listaRejilla.add(
                        Rejilla(String.format("%d %s",usuario.checkins?.count, getString(R.string.app_perfil_checkins)),R.drawable.icono_favoritos,
                            ContextCompat.getColor(applicationContext,R.color.secondaryLightColor))
                    )

                    val adaptador = AdaptadorGridView(applicationContext,listaRejilla)

                    gridRejilla.adapter = adaptador

                }
            })

        }
        else{
            foursquare?.mandarIniciarSesion()
        }
    }

    fun initToolbar(nombrePerfil:String){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(nombrePerfil)
        setSupportActionBar(toolbar)

        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener { finish() }

    }
}
