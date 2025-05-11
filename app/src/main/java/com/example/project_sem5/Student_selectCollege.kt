package com.example.project_sem5

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.project_sem5.Utils.APPLICATIONS
import com.example.project_sem5.Utils.COLLEGE_USER
import com.example.project_sem5.Utils.DEGREES
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class Student_selectCollege : AppCompatActivity() {
    private lateinit var collegeList: MutableList<String>
    private lateinit var listViewColleges: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_select_college)

        val degreeName = intent.getStringExtra("degreeName") ?: return
        collegeList = mutableListOf()
        listViewColleges = findViewById(R.id.listView_student_colleges)

        Firebase.firestore.collection(COLLEGE_USER)
            .get()
            .addOnSuccessListener { collegeResult ->
                for (college in collegeResult)
                {
                    if (college.exists())
                    {
                        Firebase.firestore.collection(DEGREES)
                            .whereEqualTo("name",degreeName)
                            .whereEqualTo("collegeId",college.id)
                            .get()
                            .addOnSuccessListener { degreeResult ->
                                for (degree in degreeResult)
                                {
                                    val collegeName = college.getString("college_name")
                                    collegeName?.let {
                                        collegeList.add(it)
                                    }
                                }

                                val adapter = ArrayAdapter(this, R.layout.list_item, collegeList)
                                listViewColleges.adapter = adapter

                                listViewColleges.setOnItemClickListener { _, _, position, _ ->
                                    val selectedCollege = collegeList[position]
                                    applyForDegree(degreeName, selectedCollege)
                                }
                            }
                    }
                }
            }
    }



    private fun applyForDegree(degreeName: String, collegeName: String) {
        val userId = Firebase.auth.currentUser!!.uid

        val application = hashMapOf(
            "studentId" to userId,
            "degreeName" to degreeName,
            "collegeName" to collegeName,
            "status" to "Pending"
        )

        Firebase.firestore.collection(APPLICATIONS)
            .add(application)
            .addOnSuccessListener {
                Toast.makeText(this, "Applied to $collegeName for $degreeName", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to apply", Toast.LENGTH_SHORT).show()
            }
    }
}