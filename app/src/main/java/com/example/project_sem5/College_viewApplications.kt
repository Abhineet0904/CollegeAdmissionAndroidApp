package com.example.project_sem5

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.project_sem5.Models_college.User_college
import com.example.project_sem5.Utils.APPLICATIONS
import com.example.project_sem5.Utils.COLLEGE_USER
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class College_viewApplications : AppCompatActivity() {
    private lateinit var studentList: MutableList<String>
    private lateinit var listViewStudents: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_college_view_applications)

        val degreeName = intent.getStringExtra("degreeName")
        val collegeName = intent.getStringExtra("collegeName")

        studentList = mutableListOf()
        listViewStudents = findViewById(R.id.listView_college_students)


        Firebase.firestore.collection(APPLICATIONS)
            .whereEqualTo("collegeName",collegeName)
            .whereEqualTo("degreeName", degreeName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result)
                {
                    val studentId = document.getString("studentId")
                    studentId?.let {
                        studentList.add(it)
                    }
                }

                val adapter = ArrayAdapter(this, R.layout.list_item, studentList)
                listViewStudents.adapter = adapter

                listViewStudents.setOnItemClickListener { _, _, position, _ ->
                    val selectedStudentId = studentList[position]
                    val intent = Intent(this, College_viewStudent::class.java)
                    intent.putExtra("studentId", selectedStudentId)
                    intent.putExtra("degreeName",degreeName)
                    intent.putExtra("collegeName",collegeName)
                    startActivity(intent)
                }
            }
    }
}