package com.example.tiendamanga

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class ProductDTO(
    val id: Int,
    val nombre: String,
    val precio: Int,
    val imagen: String,
    val stock: Int,
    val destacado: Boolean
)

interface ApiService {
    @GET("api/products")
    suspend fun getProducts(): List<ProductDTO>
}

fun provideApi(context: Context): ApiService {
    val client = okhttp3.OkHttpClient.Builder()
        .addInterceptor(AssetsInterceptor(context))
        .build()

    return Retrofit.Builder()
        .baseUrl("http://localhost/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}



