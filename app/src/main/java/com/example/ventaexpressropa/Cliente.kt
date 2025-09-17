package com.example.ventaexpressropa

data class Cliente(
    var id: String = "", // Firestore puede generar IDs, así que inicialízalo o asígnalo después
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = ""
    // Constructor vacío requerido por Firestore para deserializar objetos
    // constructor() : this("", "", "", "")
)
// Nota: Firestore funciona mejor con propiedades `var` si vas a actualizar objetos directamente.
// También requiere un constructor sin argumentos (o que todas las propiedades tengan valores por defecto como arriba)
// para poder deserializar los datos de vuelta a objetos Cliente.