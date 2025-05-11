package com.example.project_sem5

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.project_sem5.Utils.DEGREES
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class Student_selectDegree : AppCompatActivity() {
    private lateinit var degreeList: MutableList<String>
    private lateinit var listViewDegrees: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_select_degree)

        degreeList = mutableListOf()
        listViewDegrees = findViewById(R.id.listView_student_degrees)

        Firebase.firestore.collection(DEGREES)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val degreeName = document.getString("name")
                    degreeName?.let {
                        degreeList.add(degreeName)
                    }
                }

                val adapter = ArrayAdapter(this, R.layout.list_item, degreeList)
                listViewDegrees.adapter = adapter

                listViewDegrees.setOnItemClickListener { _, _, position, _ ->
                    val selectedDegree = degreeList[position]
                    val intent = Intent(this, Student_selectCollege::class.java)
                    intent.putExtra("degreeName", selectedDegree)
                    startActivity(intent)
                }
            }
    }
}