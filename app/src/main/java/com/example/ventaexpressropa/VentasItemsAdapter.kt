package com.example.ventaexpressropa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VentasItemsAdapter(
    private val items: MutableList<VentaProductoItem>,
    private val onChange: () -> Unit
) : RecyclerView.Adapter<VentasItemsAdapter.ItemVH>() {

    class ItemVH(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.item_nombre)
        val marca: TextView = view.findViewById(R.id.item_marca)
        val precio: TextView = view.findViewById(R.id.item_precio)
        val cantidad: TextView = view.findViewById(R.id.item_cantidad)
        val subtotal: TextView = view.findViewById(R.id.item_subtotal)
        val eliminar: ImageButton = view.findViewById(R.id.item_eliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_venta_producto, parent, false)
        return ItemVH(view)
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        val item = items[position]
        holder.nombre.text = item.nombreProducto
        holder.marca.text = item.marcaProducto
        holder.precio.text = String.format("$%.2f", item.precioUnitario)
        holder.cantidad.text = item.cantidad.toString()
        holder.subtotal.text = String.format("$%.2f", item.subtotal)
        holder.eliminar.setOnClickListener {
            val index = holder.adapterPosition
            if (index != RecyclerView.NO_POSITION) {
                items.removeAt(index)
                notifyItemRemoved(index)
                onChange()
            }
        }
    }

    override fun getItemCount(): Int = items.size
}


