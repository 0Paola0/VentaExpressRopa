package com.example.ventaexpressropa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductoAdapter(
    private val productos: List<Producto>,
    private val onClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProducto: ImageView = itemView.findViewById(R.id.ivProducto)
        val tvMarca: TextView = itemView.findViewById(R.id.tvMarca)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        holder.tvMarca.text = producto.marca
        holder.tvNombre.text = producto.nombre
        holder.tvPrecio.text = producto.precio
        holder.tvDescripcion.text = producto.descripcion

        // Cargar imagen desde drawable usando el nombre guardado en producto.imagen
        val resId = holder.itemView.context.resources.getIdentifier(
            producto.imagen ?: "",
            "drawable",
            holder.itemView.context.packageName
        )

        if (resId != 0) {
            holder.ivProducto.setImageResource(resId)
        } else {
            holder.ivProducto.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // Click en el producto
        holder.itemView.setOnClickListener { onClick(producto) }
    }

    override fun getItemCount(): Int = productos.size
}



