package com.example.ventaexpressropa

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class VentasProductoDialogFragment(
    private val catalogo: List<CatalogoProducto>,
    private val onAdd: (producto: CatalogoProducto, cantidad: String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_agregar_producto, null)
        val selector: AutoCompleteTextView = view.findViewById(R.id.edit_nombre)
        val cantidad: EditText = view.findViewById(R.id.edit_cantidad)

        val nombres = catalogo.map { it.nombre }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres)
        selector.setAdapter(adapter)
        selector.threshold = 0
        selector.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && adapter.count > 0) selector.showDropDown()
        }
        selector.setOnClickListener {
            if (adapter.count > 0) selector.showDropDown()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Agregar producto del catálogo")
            .setView(view)
            .setPositiveButton("Agregar") { _, _ ->
                val nombreSel = selector.text.toString().trim()
                val producto = catalogo.firstOrNull { it.nombre.equals(nombreSel, true) }
                if (producto != null) {
                    onAdd(producto, cantidad.text.toString().trim())
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}


