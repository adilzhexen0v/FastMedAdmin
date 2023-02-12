package com.example.trikotaprojectadmin

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface JsonApi {
    @POST("admin/login")
    fun loginAdmin(
        @Body adminData: AdminModel
    ): Call<JsonObject>

    @GET("admin/doctors")
    fun showListOfDoctors(
        @Header("Authorization") authToken: String
    ): Call<JsonObject>

    @POST("admin/doctor/confirm")
    fun confirmDoctor(
        @Header("Authorization") authToken: String,
        @Body doctorData: DoctorConfirmOrDenyModel
    ): Call<JsonObject>

    @POST("admin/doctor/deny")
    fun denyDoctor(
        @Header("Authorization") authToken: String,
        @Body doctorData: DoctorConfirmOrDenyModel
    ): Call<JsonObject>
}