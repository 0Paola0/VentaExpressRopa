package com.example.ventaexpressropa

data class VentaProductoItem(
    val nombreProducto: String = "",
    val marcaProducto: String = "",
    val precioUnitario: Double = 0.0,
    val cantidad: Int = 0,
    val subtotal: Double = 0.0
)

data class Venta(
    val id: String = "",
    val clienteId: String = "",
    val clienteNombre: String = "",
    val fechaIso8601: String = "",
    val total: Double = 0.0,
    val items: List<VentaProductoItem> = emptyList()
)


