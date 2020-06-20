package com.example.checkins.RecyclerViewCategorias
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.Foursquare.Category
import com.example.checkins.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.template_categories.view.*


class AdaptadorCustom(items:ArrayList<Category>, var listener: ClickListener, var longClickListener: LongClickListener):RecyclerView.Adapter<AdaptadorCustom.ViewHolder>() {

    var items:ArrayList<Category>? = null
    var multiSeleccion = false

    var itemsSeleccionados:ArrayList<Int>? = null
    var viewHolder:ViewHolder? = null

    init{
        this.items=items
        itemsSeleccionados = ArrayList()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.template_categories,parent,false)
        viewHolder = ViewHolder(vista, listener,longClickListener)

        return viewHolder!!

    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items?.get(position)
       // holder.foto?.setImageResource(item?.foto!!)
        holder.nombre?.text = item?.name
        Picasso.get().load(item?.icon?.urlIcono).into(holder.foto)
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
            var itemsEliminados = ArrayList<Category>()

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
        var nombre:TextView? = null
       // var precio:TextView? = null
       // var rating:RatingBar? = null
        var listener:ClickListener? = null
        var longClickListener:LongClickListener? = null

        init{
            this.foto=vista.ivFoto
            this.nombre=vista.tvNombre
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