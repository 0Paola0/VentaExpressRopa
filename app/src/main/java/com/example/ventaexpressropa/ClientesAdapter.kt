package com.example.ventaexpressropa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClientesAdapter(
    private val clientes: List<Cliente>
) : RecyclerView.Adapter<ClientesAdapter.ClienteViewHolder>() {

    inner class ClienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView      = view.findViewById(R.id.tvNombreItem)
        val tvCorreo: TextView      = view.findViewById(R.id.tvCorreoItem)
        val tvTelefono: TextView    = view.findViewById(R.id.tvTelefonoItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_clientes_adapter, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = clientes[position]
        holder.tvNombre.text   = cliente.nombre
        holder.tvCorreo.text   = cliente.correo
        holder.tvTelefono.text = cliente.telefono
    }

    override fun getItemCount(): Int = clientes.size
}