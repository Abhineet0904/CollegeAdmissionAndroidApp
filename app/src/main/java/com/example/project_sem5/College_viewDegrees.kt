package com.example.project_sem5

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.project_sem5.Utils.DEGREES
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class College_viewDegrees : AppCompatActivity() {
    private lateinit var degreeList: MutableList<String>
    private lateinit var listViewDegrees: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_college_view_degrees)

        val collegeName = intent.getStringExtra("collegeName")

        degreeList = mutableListOf()
        listViewDegrees = findViewById(R.id.listView_college_degrees)

        Firebase.firestore.collection(DEGREES)
            .whereEqualTo("collegeId",Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                for (document in result)
                {
                    val degreeName = document.getString("name")
                    degreeName?.let {
                        degreeList.add(it)
                    }
                }

                val adapter = ArrayAdapter(this, R.layout.list_item, degreeList)
                listViewDegrees.adapter = adapter

                listViewDegrees.setOnItemClickListener { _, _, position, _ ->
                    val selectedDegree = degreeList[position]
                    val intent = Intent(this, College_viewApplications::class.java)
                    intent.putExtra("degreeName", selectedDegree)
                    intent.putExtra("collegeName", collegeName)
                    startActivity(intent)
                }
            }
    }
}