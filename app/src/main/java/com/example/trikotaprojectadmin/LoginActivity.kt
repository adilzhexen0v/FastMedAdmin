package com.example.trikotaprojectadmin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.trikotaprojectadmin.databinding.ActivityLoginBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://nosql-group-project-backend.onrender.com/")
            .build()
        val jsonApi = retrofitBuilder.create(JsonApi::class.java)
        val preferences = this?.getSharedPreferences("ADMIN_INFO", Context.MODE_PRIVATE)
        val editor = preferences?.edit()

        binding.loginBtn.setOnClickListener {
            var errorCounter = 0
            if (binding.enterUsernameText.text.isEmpty()) {
                errorCounter++
                binding.usernameError.text = getString(R.string.error)
            }

            if (binding.enterPasswordText.text.isEmpty()) {
                errorCounter++
                binding.passwordError.text = getString(R.string.error)
            }
            val admin = AdminModel(
                binding.enterUsernameText.text.toString(),
                binding.enterPasswordText.text.toString()
            )

            if (errorCounter == 0) {
                binding.error.text = "Please wait..."
                binding.error.setTextColor(resources.getColor(R.color.green_700))
                val call = jsonApi.loginAdmin(admin)
                call.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        try {
                            if (response.code() == 400) {
                                val mess = "Incorrect email or password"
                                binding.error.text = mess
                                binding.error.setTextColor(resources.getColor(R.color.red))
                            } else {
                                val json = JSONObject(Gson().toJson(response.body()))
                                val token = json.getString("token")
                                editor?.putString("TOKEN", token)
                                val id = json.getString("_id")
                                editor?.putString("ID", id)
                                editor?.apply()
                                val listIntent: Intent = Intent(this@LoginActivity, AdminActivity::class.java)
                                startActivity(listIntent)
                            }
                        } catch (e: Exception) {
                            binding.error.text = e.toString()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, t.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}