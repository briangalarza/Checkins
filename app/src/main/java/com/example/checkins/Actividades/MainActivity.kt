package com.example.checkins.Actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.checkins.Foursquare.Foursquare
import com.example.checkins.R

class MainActivity : AppCompatActivity() {

    var foursquare: Foursquare? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bLogin = findViewById<Button>(R.id.bLogin)

        foursquare = Foursquare(
            this,
            PantallaPrincipal()
        )
        if(foursquare?.hayToken()!!){
            foursquare?.navegarSiguienteActividad()

        }
        bLogin.setOnClickListener {
            Toast.makeText(this,"Prueba",Toast.LENGTH_SHORT).show()

            foursquare?.iniciarSesion()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        foursquare?.validarActivityResult(requestCode, resultCode, data)

    }
}
