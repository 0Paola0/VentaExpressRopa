package com.example.ventaexpressropa




data class Producto(
    var id: String? = null,
    var nombre: String? = null,
    var descripcion: String? = null,
    var precio: String? = null,
    var stock: Int = 0,
    var marca: String? = null,
    var imagen: String? = null
)
