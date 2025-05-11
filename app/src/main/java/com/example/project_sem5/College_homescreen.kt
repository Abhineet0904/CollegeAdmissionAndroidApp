package com.example.project_sem5

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.project_sem5.Models_college.User_college
import com.example.project_sem5.Utils.APPLICATIONS
import com.example.project_sem5.Utils.COLLEGE_USER
import com.example.project_sem5.Utils.DEGREES
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class College_homescreen : AppCompatActivity() {
    private lateinit var drawerLyt: DrawerLayout
    private lateinit var nav_view: NavigationView
    private lateinit var Name: TextView
    private lateinit var Email: TextView
    private lateinit var Layout1: LinearLayout
    private lateinit var No_degrees: TextView
    private lateinit var No_applications: TextView
    private lateinit var menu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_college_homescreen)

        drawerLyt = findViewById(R.id.college_main)
        nav_view = findViewById(R.id.college_navigation_view)
        Name = nav_view.getHeaderView(0).findViewById(R.id.college_name)
        Email = nav_view.getHeaderView(0).findViewById(R.id.mail)
        Layout1 = nav_view.getHeaderView(0).findViewById(R.id.layout1)
        No_degrees = nav_view.getHeaderView(0).findViewById(R.id.textView3)
        No_applications = nav_view.getHeaderView(0).findViewById(R.id.textView4)


        Layout1.setOnClickListener {
            val intent = Intent(this, College_viewDegrees::class.java)
            intent.putExtra("collegeName",Name.text)
            startActivity(intent)
        }

        menu = findViewById(R.id.menu_button)
        menu.setOnClickListener {
            drawerLyt.open()
        }
        drawerLyt.setOnClickListener {
            drawerLyt.close()
        }

        nav_view.setNavigationItemSelectedListener { item ->
            when (item.itemId)
            {
                R.id.item1 ->
                {
                    showAddDegreeDialog()
                }
                R.id.item2 ->
                {
                    Firebase.auth.signOut()
                    val intent2 = Intent(this, MainActivity::class.java)
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent2)
                    finish()
                }
                R.id.item3 ->
                {
                    Firebase.auth.currentUser!!.let { user ->
                        user.delete()
                            .addOnCompleteListener {
                                if (it.isSuccessful)
                                {
                                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()

                                    val intent3 = Intent(this, MainActivity::class.java)
                                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent3)
                                    finish()
                                }
                                else
                                {
                                    Toast.makeText(this, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
            drawerLyt.close()
            true
        }
    }



    override fun onStart() {
        super.onStart()
        Firebase.firestore.collection(COLLEGE_USER)
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists())
                {
                    val user = it.toObject<User_college>()!!
                    Name.text = user.college_name
                    Email.text = user.mail

                    getNoOfDegrees()
                    getNoOfApplications()
                }
                else
                {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
    }



    private fun getNoOfDegrees() {
        Firebase.firestore.collection(COLLEGE_USER)
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists())
                {
                    Firebase.firestore.collection(DEGREES)
                        .whereEqualTo("collegeId", it.id)
                        .get()
                        .addOnSuccessListener { degreesResult ->
                            val degreeCount = degreesResult.size()
                            No_degrees.text = degreeCount.toString()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                }
                else
                {
                    Toast.makeText(this, "College not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }



    private fun getNoOfApplications() {
        Firebase.firestore.collection(APPLICATIONS)
            .whereEqualTo("collegeName", Name.text)
            .get()
            .addOnSuccessListener { applications ->
                val applicationCount = applications.size()
                No_applications.text = applicationCount.toString()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }



    private fun showAddDegreeDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Degree")
        val input = EditText(this)
        input.hint = "Degree Name"
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->

            val degreeName = input.text.toString().trim()
            if (degreeName.isNotEmpty())
            {
                addDegreeToFirestore(degreeName)
            }
            else
            {
                Toast.makeText(this, "Degree name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }



    private fun addDegreeToFirestore(degreeName: String) {
        Firebase.firestore.collection(COLLEGE_USER)
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists())
                {
                    val degree = hashMapOf(
                        "name" to degreeName,
                        "collegeId" to it.id
                    )
                    Firebase.firestore.collection(DEGREES)
                        .add(degree)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Degree added successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                }
                else
                {
                    Toast.makeText(this, "College not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }
}