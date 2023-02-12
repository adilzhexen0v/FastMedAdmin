package com.example.trikotaprojectadmin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.trikotaprojectadmin.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.refresh.setOnClickListener {
            finish()
            startActivity(intent)
        }

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://nosql-group-project-backend.onrender.com/")
            .build()
        val jsonApi = retrofitBuilder.create(JsonApi::class.java)
        val preferences = this@AdminActivity?.getSharedPreferences("ADMIN_INFO", Context.MODE_PRIVATE)
        val token = preferences?.getString("TOKEN", "")!!
        Glide.with(this).load("https://sipdomik.ru/templates/images/loading.gif").into(binding.loading)
        try {
            val call = jsonApi.showListOfDoctors(token)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    try {
                        val json = JSONObject(Gson().toJson(response.body()))
                        val doctorsJsonArray = json.getJSONArray("doctors")
                        val doctorsList: MutableList<DoctorModel> = mutableListOf()
                        for (i in 0 until doctorsJsonArray.length()) {
                            val id = doctorsJsonArray.getJSONObject(i).getString("_id")
                            val name = doctorsJsonArray.getJSONObject(i).getString("name")
                            val surname = doctorsJsonArray.getJSONObject(i).getString("surname")
                            val occupation = JSONObject(
                                doctorsJsonArray
                                    .getJSONObject(i)
                                    .getString("occupation")
                            ).getString("occupation")
                            val experience = doctorsJsonArray.getJSONObject(i).getString("experience")
                            val hospitalName = JSONObject(
                                doctorsJsonArray
                                    .getJSONObject(i)
                                    .getString("hospitalId")
                            ).getString("hospital")
                            val hospitalCity = JSONObject(
                                doctorsJsonArray
                                    .getJSONObject(i)
                                    .getString("hospitalId")
                            ).getString("city")
                            val hospitalAddress = JSONObject(
                                doctorsJsonArray
                                    .getJSONObject(i)
                                    .getString("hospitalId")
                            ).getString("address")
                            var cvLink = "empty"
                            try {
                                cvLink = doctorsJsonArray.getJSONObject(i).getString("cv")
                            } catch (e: Exception) {
                                println(e)
                            }
                            var img = "empty"
                            try {
                                img = doctorsJsonArray.getJSONObject(i).getString("imageUrl")
                            } catch (e: Exception) {
                                println(e)
                            }
                            val doctor = DoctorModel (
                                id,
                                "$name $surname",
                                occupation,
                                experience,
                                "$hospitalName, $hospitalCity, $hospitalAddress",
                                img,
                                cvLink
                            )
                            doctorsList.add(doctor)
                        }
                        val doctorsRecyclerView = binding.recyclerView
                        doctorsRecyclerView.adapter = DoctorAdapter(this@AdminActivity, doctorsList)
                        binding.loading.visibility = View.INVISIBLE
                        if (doctorsJsonArray.length() == 0) {
                            binding.message.text = "Right now we don't have doctors, which send request"
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@AdminActivity, "From catch: " + e.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@AdminActivity, "From onFailure: " + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(this@AdminActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}