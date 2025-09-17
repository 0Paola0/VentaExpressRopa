package com.example.ventaexpressropa



import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerProductos)

        val productos = listOf(
            Producto("Nike", "Camiseta deportiva", "$25.00", "Camiseta ligera para entrenar", R.drawable.nikec),
            Producto("Adidas", "Pantalón Jogger", "$35.00", "Cómodo jogger para uso diario", R.drawable.pans),
            Producto("Puma", "Zapatillas Urbanas", "$55.00", "Calzado moderno y resistente", R.drawable.pumaz),
            Producto("Zara", "Chaqueta de cuero", "$80.00", "Chaqueta elegante estilo biker", R.drawable.zara),
            Producto("H&M", "Vestido casual", "$40.00", "Vestido fresco para verano", R.drawable.vestido),
            Producto("Levi's", "Jeans Azul", "$50.00", "Jeans clásicos de mezclilla", R.drawable.levi),
            Producto("Converse", "Tenis clásicos", "$45.00", "Tenis unisex icónicos", R.drawable.tenis),
            Producto("Under Armour", "Sudadera con capucha", "$60.00", "Sudadera deportiva cómoda", R.drawable.capucha),
            Producto("Guess", "Bolso de mano", "$70.00", "Bolso elegante de cuero sintético", R.drawable.bolso),
            Producto("Tommy Hilfiger", "Camisa Polo", "$55.00", "Camisa Polo casual elegante", R.drawable.polo)
        )

        val adapter = ProductoAdapter(productos) { producto ->
            val intent = Intent(this, ProductoDetalleActivity::class.java).apply {
                putExtra("marca", producto.marca)
                putExtra("nombre", producto.nombre)
                putExtra("precio", producto.precio)
                putExtra("descripcion", producto.descripcion)
                putExtra("imageResId", producto.imageResId)
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}


