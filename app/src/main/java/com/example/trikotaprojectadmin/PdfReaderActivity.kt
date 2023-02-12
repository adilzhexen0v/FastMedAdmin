package com.example.trikotaprojectadmin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.trikotaprojectadmin.databinding.ActivityViewPdfBinding

class PdfReaderActivity: AppCompatActivity() {
    private lateinit var binding: ActivityViewPdfBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this).load("https://sipdomik.ru/templates/images/loading.gif").into(binding.pdfLoading)
        val cvPreferences = this?.getSharedPreferences("CV_INFO", Context.MODE_PRIVATE)
        val name = cvPreferences?.getString("NAME", "")
        val cvUrl = cvPreferences?.getString("CV", "")
        binding.pdfAuthor.text = "$name's CV"
        RetrievePdfFromURL(binding.pdfView).execute("https://nosql-group-project-backend.onrender.com/uploads/$cvUrl")
        binding.closeBtn.setOnClickListener {
            closePDF()
        }
    }
    private fun closePDF() {
        val listIntent: Intent = Intent(this@PdfReaderActivity, AdminActivity::class.java)
        startActivity(listIntent)
    }
}