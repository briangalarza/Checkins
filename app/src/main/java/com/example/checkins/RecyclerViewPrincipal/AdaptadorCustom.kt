package com.example.checkins.RecyclerViewPrincipal
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.R
import com.example.checkins.Foursquare.Venue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.template_venues.view.*

class AdaptadorCustom(items:ArrayList<Venue>, var listener: ClickListener, var longClickListener: LongClickListener):RecyclerView.Adapter<AdaptadorCustom.ViewHolder>() {

    var items:ArrayList<Venue>? = null
    var multiSeleccion = false

    var itemsSeleccionados:ArrayList<Int>? = null
    var viewHolder:ViewHolder? = null

    init{
        this.items=items
        itemsSeleccionados = ArrayList()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorCustom.ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.template_venues,parent,false)
        viewHolder = ViewHolder(vista, listener,longClickListener)

        return viewHolder!!

    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder: AdaptadorCustom.ViewHolder, position: Int) {
        val item = items?.get(position)
       // holder.foto?.setImageResource(item?.foto!!)

        Picasso.get().load(item?.imagePreview).placeholder(R.drawable.placeholder_venue).into(holder.foto)
        Picasso.get().load(item?.iconCategory).placeholder(R.drawable.icono_categorias).into(holder.iconoCategoria)


        holder.nombre?.text = item?.name
        holder.state?.text = String.format("%s,%s",item?.location?.state, item?.location?.country)

        if (item?.categories?.size!! >0){
            holder.category?.text = item?.categories?.get(0)?.name
        }else{
            holder.category?.setText(R.string.app_pantalla_principal_category)
        }
        if (item?.stats?.checkinsCount !=null){
            holder.checkins?.text = item?.stats?.checkinsCount.toString()
        }

        // holder.precio?.text = "$ " + item?.precio.toString()
       // holder.rating?.rating = item?.rating!!

        if (itemsSeleccionados?.contains(position)!!){
            holder.vista.setBackgroundColor(Color.LTGRAY)
        }
        else{
            holder.vista.setBackgroundColor(Color.WHITE)
        }


    }

    fun iniciarActionMode(){
        multiSeleccion = true
    }

    fun destruirActionMode(){
        multiSeleccion = false
        itemsSeleccionados?.clear()
        notifyDataSetChanged()
    }

    fun terminarActionMode(){

        for (item in itemsSeleccionados!!){
                itemsSeleccionados?.remove(item)
        }
        multiSeleccion = false

        notifyDataSetChanged()

    }

    fun seleccionarItem (index:Int){
        if(multiSeleccion){
            if(itemsSeleccionados?.contains(index)!!){

            itemsSeleccionados?.remove(index)
            }
            else{
                itemsSeleccionados?.add(index)
            }
            notifyDataSetChanged()
        }
    }

    fun obtenerNumeroElementosSeleccionados():Int{
        return itemsSeleccionados?.count()!!
    }

    fun eliminarSeleccionados(){
        if (itemsSeleccionados?.count()!! >0){
            var itemsEliminados = ArrayList<Venue>()

            for (index in itemsSeleccionados!!){
                itemsEliminados.add(items?.get(index)!!)

            }
            items?.removeAll(itemsEliminados)
            itemsSeleccionados?.clear()
        }else{

        }
    }


    class ViewHolder(vista: View, listener: ClickListener,longClickListener: LongClickListener):RecyclerView.ViewHolder(vista),View.OnClickListener, View.OnLongClickListener{


        var vista = vista
        var foto: ImageView? = null
        var iconoCategoria: ImageView? = null

        var nombre:TextView? = null
        var state:TextView? = null
        var category:TextView? = null
        var checkins:TextView? = null
       // var precio:TextView? = null
       // var rating:RatingBar? = null
        var listener:ClickListener? = null
        var longClickListener:LongClickListener? = null

        init{
            //this.foto=vista.findViewById(R.id.ivFoto)
            this.nombre=vista.tvNombre
            this.state = vista.tvState
            this.category = vista.tvCategory
            this.checkins = vista.tvCheckin
            this.foto = vista.ivFoto
            iconoCategoria = vista.ivCategoria
            //this.precio=vista.findViewById(R.id.tvPrecio)
            //this.rating=vista.findViewById(R.id.tvRating)
            this.listener = listener
            this.longClickListener = longClickListener
            vista.setOnClickListener(this)
            vista.setOnLongClickListener(this)

        }

        override fun onClick(v: View?) {
            this.listener?.onClick(v!!,adapterPosition)

        }

        override fun onLongClick(v: View?): Boolean {
            this.longClickListener?.longClick(v!!,adapterPosition)
            return true
        }

    }
}