package com.example.ventaexpressropa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class HistorialVentasAdapter(
    private val ventas: List<Venta>
) : RecyclerView.Adapter<HistorialVentasAdapter.VentaVH>() {

    class VentaVH(view: View) : RecyclerView.ViewHolder(view) {
        val cliente: TextView = view.findViewById(R.id.item_cliente)
        val fecha: TextView = view.findViewById(R.id.item_fecha)
        val total: TextView = view.findViewById(R.id.item_total)
        val productos: TextView = view.findViewById(R.id.item_productos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial_venta, parent, false)
        return VentaVH(view)
    }

    override fun onBindViewHolder(holder: VentaVH, position: Int) {
        val venta = ventas[position]
        
        holder.cliente.text = venta.clienteNombre
        holder.total.text = String.format(Locale.getDefault(), "$%.2f", venta.total)
        
        // Formatear fecha
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(venta.fechaIso8601)
            holder.fecha.text = outputFormat.format(date)
        } catch (e: Exception) {
            holder.fecha.text = venta.fechaIso8601
        }
        
        // Mostrar productos y cantidades
        val productosText = venta.items.joinToString(", ") { item ->
            "${item.cantidad}x ${item.nombreProducto}"
        }
        holder.productos.text = productosText
    }

    override fun getItemCount(): Int = ventas.size
}
