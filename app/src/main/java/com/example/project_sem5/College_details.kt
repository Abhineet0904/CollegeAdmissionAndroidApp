package com.example.project_sem5

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.project_sem5.Models_college.User_college
import com.example.project_sem5.Models_student.User_student
import com.example.project_sem5.Utils.COLLEGE_USER
import com.example.project_sem5.Utils.STUDENT_USER
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream

class College_details : AppCompatActivity() {
    private lateinit var textInp1 : TextInputLayout

    private lateinit var textInp2 : TextInputLayout
    private lateinit var textView2 : TextView
    private var pdfUri2 : Uri? = null
    private lateinit var progBar2 : ProgressBar

    private lateinit var textInp3 : TextInputLayout
    private lateinit var textInp4 : TextInputLayout

    private lateinit var btn1 : Button
    private var pdfUploaded : Boolean = false

    private lateinit var user_clg : User_college


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_college_details)

        var email = ""
        var password = ""
        Firebase.firestore.collection(COLLEGE_USER)
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject<User_college>()!!
                    email = user.mail.toString()
                    password = user.pswrd.toString()
                }
            }
        user_clg = User_college()


        textInp1 = findViewById(R.id.textInput1)
        textInp2 = findViewById(R.id.textInput2)
        textInp3 = findViewById(R.id.textInput3)
        textInp4 = findViewById(R.id.textInput4)


        textView2 = findViewById(R.id.textView2)
        textView2.setOnClickListener {
            val intent1 = Intent(Intent.ACTION_GET_CONTENT)
            intent1.type = "application/pdf"
            intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
            startActivityForResult(Intent.createChooser(intent1,"Select marksheet"),1)
        }
        progBar2 = findViewById(R.id.progressBar2)


        btn1 = findViewById(R.id.button1)
        btn1.setOnClickListener {
            val et1 = textInp1.editText!!
            val et2 = textInp2.editText!!
            val et3 = textInp3.editText!!
            val et4 = textInp4.editText!!

            if (et1.text.toString()=="")
            {
                et1.error = "Enter college name"
            }
            else if (et2.text.toString()=="")
            {
                et2.error = "Enter UGC affiliation number"
            }
            else if (pdfUploaded==false)
            {
                textView2.setTypeface(null,Typeface.BOLD)
                textView2.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
            else if (et3.text.toString()=="")
            {
                et3.error = "Enter contact number"
            }
            else if (et4.text.toString()=="")
            {
                et4.error = "Enter address"
            }
            else
            {
                user_clg.mail = email
                user_clg.pswrd = password
                user_clg.college_name = et1.text.toString()
                user_clg.ugcno = et2.text.toString()
                user_clg.contact_number = et3.text.toString()
                user_clg.address = et4.text.toString()

                Firebase.firestore.collection(COLLEGE_USER)
                    .document(Firebase.auth.currentUser!!.uid).set(user_clg)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Registration completed", Toast.LENGTH_SHORT).show()

                        val intent2 = Intent(this,College_homescreen::class.java)
                        startActivity(intent2)
                        finish()
                    }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            pdfUri2 = data.data
            if (validatePdf(pdfUri2!!))
            {
                uploadPdf(pdfUri2!!)
            }
            else
            {
                textView2.text = "Select PDF whose size is lesser than 1 MB"
                textView2.setTypeface(null, Typeface.BOLD)
                textView2.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
        }
    }



    private fun validatePdf(uri: Uri): Boolean {
        val inputStream: InputStream?= contentResolver.openInputStream(uri)
        val sizeInBytes = inputStream?.available() ?: 0
        inputStream?.close()
        return sizeInBytes < (1* 1024*1024)
    }



    private fun uploadPdf(uri: Uri) {
        if (uri != null)
        {
            val userId = Firebase.auth.currentUser!!.uid
            val storageRef = FirebaseStorage.getInstance().reference.child("College_PDF/$userId/${userId}_accreditation.pdf")
            progBar2.visibility = View.VISIBLE
            storageRef.putFile(uri)
                .addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred)/it.totalByteCount
                    progBar2.progress = progress.toInt()
                }
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val filename = getFileName(uri)
                        savePdfUrl(userId,downloadUrl.toString(),filename)
                    }
                }
                .addOnFailureListener {
                    progBar2.visibility = View.GONE
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
                }
        }
    }



    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var result = ""
        if (uri.scheme == "content")
        {
            contentResolver.query(uri,null,null,null,null)?.use {
                if (it.moveToFirst())
                {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result.isEmpty())
        {
            result = uri.path ?: "Unknown"
            val cut = result.lastIndexOf("/")
            if (cut != -1)
            {
                result = result.substring(cut+1)
            }
        }
        return result
    }



    private fun savePdfUrl(userId: String, pdfUrl: String, fileName: String) {
        if (userId != null)
        {
            Firebase.firestore.collection(COLLEGE_USER).document(userId)
                .update("College_pdf", FieldValue.arrayUnion(pdfUrl))
                .addOnSuccessListener {
                    progBar2.visibility = View.GONE
                    textView2.error = null
                    textView2.text = fileName
                    textView2.setTextColor(Color.parseColor("#FFFFFF"))
                    pdfUploaded = true
                    Toast.makeText(this,"PDF uploaded successfully",Toast.LENGTH_SHORT).show()
                }
        }
    }
}