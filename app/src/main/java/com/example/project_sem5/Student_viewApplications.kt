package com.example.project_sem5

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.project_sem5.Utils.APPLICATIONS
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class Student_viewApplications : AppCompatActivity() {
    private lateinit var applicationList: MutableList<String>
    private lateinit var listViewApplications: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_view_applications)

        applicationList = mutableListOf()
        listViewApplications = findViewById(R.id.listView_student_applications)

        val cancelApplication = intent.getStringExtra("Cancel application?")


        Firebase.firestore.collection(APPLICATIONS)
            .whereEqualTo("studentId",Firebase.auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val collegeName = document.getString("collegeName")
                    val degreeName = document.getString("degreeName")
                    val status = document.getString("status")
                    val record = collegeName+"\n\n"+degreeName+"\n\nStatus : "+status

                    applicationList.add(record)
                }

                val adapter = ArrayAdapter(this, R.layout.list_item, applicationList)
                listViewApplications.adapter = adapter

                listViewApplications.setOnItemClickListener { _, _, position, _ ->
                    val selectedApplication = applicationList[position]
                    if (cancelApplication == "Yes")
                    {
                        cancelApplication(selectedApplication)
                    }
                }
            }
    }


    
    private fun cancelApplication(application: String) {
        val record = application.split("\n\n")

        Firebase.firestore.collection(APPLICATIONS)
            .whereEqualTo("studentId",Firebase.auth.currentUser!!.uid)
            .whereEqualTo("collegeName",record[0])
            .whereEqualTo("degreeName",record[1])
            .get()
            .addOnSuccessListener { result ->
                for (document in result)
                {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Document deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Document not found", Toast.LENGTH_SHORT).show()
            }
    }
}