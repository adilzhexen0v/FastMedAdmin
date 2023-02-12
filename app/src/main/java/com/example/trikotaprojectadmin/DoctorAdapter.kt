package com.example.trikotaprojectadmin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DoctorAdapter(
    private val context: Context?,
    private val dataset: List<DoctorModel>
): RecyclerView.Adapter<DoctorAdapter.DoctorCardViewHolder>(){
    class DoctorCardViewHolder(view: View?): RecyclerView.ViewHolder(view!!){
        val doctorNameTextView: TextView? = view!!.findViewById(R.id.item_home_doctors_name)
        val doctorOccupationTextView: TextView? = view!!.findViewById(R.id.item_home_doctors_occupation)
        val doctorExperienceTextView: TextView? = view!!.findViewById(R.id.item_home_doctors_experience)
        val doctorHospitalTextView: TextView? = view!!.findViewById(R.id.item_home_doctors_hospital)
        val doctorImage: ImageView = view!!.findViewById(R.id.item_home_doctors_image)
        val doctorIdTextView: TextView? = view!!.findViewById(R.id.doctor_id)
        val doctorConfirmButton: Button? = view!!.findViewById(R.id.confirm_button)
        val doctorDenyButton: Button? = view!!.findViewById(R.id.deny_button)
        val doctorCVButton: ImageButton? = view!!.findViewById(R.id.cv_button)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorCardViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return DoctorCardViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: DoctorCardViewHolder, position: Int) {
        val doctorData = dataset[position]
        holder.doctorIdTextView?.text = doctorData._id
        holder.doctorNameTextView?.text = doctorData.name
        holder.doctorOccupationTextView?.text = doctorData.occupation
        holder.doctorExperienceTextView?.text = "Experience: ${doctorData.experience}"
        holder.doctorHospitalTextView?.text = doctorData.hospital
        holder.doctorConfirmButton?.setOnClickListener {
            sendRequestConfirmDoctor(doctorData._id)
        }
        holder.doctorDenyButton?.setOnClickListener {
            sendRequestDenyDoctor(doctorData._id)
        }

        if (doctorData.image != "empty") {
            Glide.with(context!!).load("https://nosql-group-project-backend.onrender.com/uploads/${doctorData.image}").into(holder.doctorImage)
        }

        if (doctorData.cv != "empty") {
            holder.doctorCVButton?.visibility = View.VISIBLE
            holder.doctorCVButton?.setOnClickListener {
                val cvPreferences = context?.getSharedPreferences("CV_INFO", Context.MODE_PRIVATE)
                val cvEditor = cvPreferences?.edit()
                cvEditor?.putString("NAME", doctorData.name)
                cvEditor?.putString("CV", doctorData.cv)
                cvEditor?.apply()
                openCV()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
    val preferences = context?.getSharedPreferences("ADMIN_INFO", Context.MODE_PRIVATE)
    val token = preferences?.getString("TOKEN", "")!!
    fun sendRequestConfirmDoctor(id: String) {

        val doctorData = DoctorConfirmOrDenyModel(
            id
        )
        val call = jsonApi.confirmDoctor(token, doctorData)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                try {
                    Toast.makeText(context, "Doctor is confirmed", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun sendRequestDenyDoctor(id: String) {
        val doctorData = DoctorConfirmOrDenyModel(
            id
        )
        val call = jsonApi.denyDoctor(token, doctorData)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                try {
                    Toast.makeText(context, "Doctor is denied", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openCV() {
        val listIntent: Intent = Intent(context , PdfReaderActivity::class.java)
        startActivity(context!!, listIntent, null)
    }
}