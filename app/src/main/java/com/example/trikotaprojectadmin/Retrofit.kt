package com.example.trikotaprojectadmin

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofitBuilder: Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl("https://nosql-group-project-backend.onrender.com/")
    .build()
val jsonApi: JsonApi = retrofitBuilder.create(JsonApi::class.java)