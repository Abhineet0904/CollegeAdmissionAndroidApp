package com.example.project_sem5

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.project_sem5.Models_student.User_student
import com.example.project_sem5.Utils.APPLICATIONS
import com.example.project_sem5.Utils.COLLEGE_USER
import com.example.project_sem5.Utils.DEGREES
import com.example.project_sem5.Utils.STUDENT_USER
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso

class Student_homescreen : AppCompatActivity() {
    private lateinit var drawerLyt : DrawerLayout
    private lateinit var nav_view : NavigationView
    private lateinit var Profile_pic : ImageView
    private lateinit var Name : TextView
    private lateinit var Email : TextView
    private lateinit var Layout1: LinearLayout
    private lateinit var No_applications: TextView
    private lateinit var Applications_accepted: TextView
    private lateinit var menu : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_homescreen)

        drawerLyt = findViewById(R.id.student_main)
        nav_view = findViewById(R.id.student_navigation_view)
        Profile_pic = nav_view.getHeaderView(0).findViewById(R.id.imageView1)
        Name = nav_view.getHeaderView(0).findViewById(R.id.name)
        Email = nav_view.getHeaderView(0).findViewById(R.id.mail)
        Layout1 = nav_view.getHeaderView(0).findViewById(R.id.layout1)
        No_applications = nav_view.getHeaderView(0).findViewById(R.id.textView3)
        Applications_accepted = nav_view.getHeaderView(0).findViewById(R.id.textView4)


        Layout1.setOnClickListener {
            val intent = Intent(this, Student_viewApplications::class.java)
            intent.putExtra("Cancel Application?","No")
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
                    val intent1 = Intent(this, Student_selectDegree::class.java)
                    startActivity(intent1)
                }
                R.id.item2 ->
                {
                    val intent2 = Intent(this, Student_viewApplications::class.java)
                    intent2.putExtra("Cancel application?", "Yes")
                    startActivity(intent2)
                }
                R.id.item3 ->
                {
                    Firebase.auth.signOut()
                    val intent3 = Intent(this,MainActivity::class.java)
                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent3)
                    finish()
                }
                R.id.item4 ->
                {
                    Firebase.auth.currentUser!!.let { user ->
                        user.delete()
                            .addOnCompleteListener {
                                if (it.isSuccessful)
                                {
                                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()

                                    val intent4 = Intent(this,MainActivity::class.java)
                                    intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent4)
                                    finish()
                                }
                                else
                                {
                                    Toast.makeText(this,it.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
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
        Firebase.firestore.collection(STUDENT_USER)
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists())
                {
                    val user = it.toObject<User_student>()!!
                    Picasso.get().load(user.image).into(Profile_pic)
                    Name.text = user.first_name+" "+user.last_name
                    Email.text = user.mail

                    getNoOfApplications()
                    getNoOfAcceptedApplications()
                }
                else
                {
                    Toast.makeText(this,"User not found",Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener {
                Toast.makeText(this,"Error fetching data",Toast.LENGTH_SHORT).show()
            }
    }



    private fun getNoOfApplications() {
        Firebase.firestore.collection(STUDENT_USER)
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists())
                {
                    Firebase.firestore.collection(APPLICATIONS)
                        .whereEqualTo("studentId", it.id)
                        .get()
                        .addOnSuccessListener { applications ->
                            val count = applications.size()
                            No_applications.text = count.toString()
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



    private fun getNoOfAcceptedApplications() {
        Firebase.firestore.collection(APPLICATIONS)
            .whereEqualTo("studentId",Firebase.auth.currentUser!!.uid)
            .whereEqualTo("status","Accepted").get()
            .addOnSuccessListener { result ->
                val count = result.size()
                Applications_accepted.text = count.toString()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }
}