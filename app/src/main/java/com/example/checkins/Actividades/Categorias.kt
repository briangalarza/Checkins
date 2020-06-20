package com.example.checkins.Actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.Foursquare.Category
import com.example.checkins.Foursquare.Foursquare
import com.example.checkins.Interfaces.CategoriasVenuesInterface
import com.example.checkins.R
import com.example.checkins.RecyclerViewCategorias.AdaptadorCustom
import com.example.checkins.RecyclerViewCategorias.ClickListener
import com.example.checkins.RecyclerViewCategorias.LongClickListener
import com.google.gson.Gson

class Categorias : AppCompatActivity() {

    //Recycler View
    var lista: RecyclerView? = null
    var adaptador: AdaptadorCustom?=null
    var layoutManager: RecyclerView.LayoutManager? = null

    var toolbar: Toolbar? = null

    companion object{
        val CATEGORIA_ACTUAL ="checkins.Categorias"


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        initToolbar()
        initRecyclerView()


        val fsqr = Foursquare(this,Categorias())
        if(fsqr?.hayToken()) {
            fsqr.cargarCategorias(object: CategoriasVenuesInterface{
                override fun categoriasVenues(categorias: ArrayList<Category>) {
                    implementacionRecyclerView(categorias)
                }
            })
        }else{
            fsqr?.mandarIniciarSesion()

        }

    }

    private fun initRecyclerView(){
        lista = findViewById(R.id.recyclerViewCategorias)
        lista?.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(this)
        lista?.layoutManager = layoutManager
    }

    private fun implementacionRecyclerView(categorias:ArrayList<Category>){
        adaptador = AdaptadorCustom(categorias, object: ClickListener {
            override fun onClick(vista: View, index: Int) {

                val categoriaToJson = Gson()
                val categoriaActualString = categoriaToJson.toJson(categorias.get(index))

                val intent = Intent(applicationContext,
                    VenuesPorCategoria::class.java)
                intent.putExtra(CATEGORIA_ACTUAL,categoriaActualString)
                startActivity(intent)
            }

        },object: LongClickListener {
            override fun longClick(vista: View, index: Int) {}

        })

        lista?.adapter = adaptador
    }

    fun initToolbar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.app_categories)
        setSupportActionBar(toolbar)

        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener { finish() }

    }
}

