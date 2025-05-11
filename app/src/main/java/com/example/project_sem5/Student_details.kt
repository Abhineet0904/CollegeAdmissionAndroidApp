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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.project_sem5.Models_student.User_student
import com.example.project_sem5.Utils.PICK_PDF_REQUEST12
import com.example.project_sem5.Utils.PICK_PDF_REQUEST7
import com.example.project_sem5.Utils.PICK_PDF_REQUEST8
import com.example.project_sem5.Utils.PICK_PDF_REQUEST9
import com.example.project_sem5.Utils.STUDENT_USER
import com.example.project_sem5.Utils.USER_IMAGE_FOLDER
import com.example.project_sem5.Utils.uploadImage
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import java.io.InputStream

class Student_details : AppCompatActivity() {
    private lateinit var img1 : ImageView

    private lateinit var textInp2 : TextInputLayout
    private lateinit var textInp3 : TextInputLayout
    private lateinit var textInp4 : TextInputLayout

    private lateinit var spinner5 : Spinner
    private lateinit var spinnerError5 : TextView

    private lateinit var textInp6 : TextInputLayout

    private lateinit var textInp7 : TextInputLayout
    private lateinit var textView7 : TextView
    private var pdfUri7 : Uri ? = null
    private lateinit var progBar7 : ProgressBar

    private lateinit var textInp8 : TextInputLayout
    private lateinit var textView8 : TextView
    private var pdfUri8 : Uri ? = null
    private lateinit var progBar8 : ProgressBar

    private lateinit var textInp9 : TextInputLayout
    private lateinit var textView9 : TextView
    private var pdfUri9 : Uri ? = null
    private lateinit var progBar9 : ProgressBar

    private lateinit var textInp10 : TextInputLayout
    private lateinit var textInp11 : TextInputLayout

    private lateinit var spinner12 : Spinner
    private lateinit var textView12 : TextView
    private var pdfUri12 : Uri ? = null
    private lateinit var progBar12 : ProgressBar

    private lateinit var btn1 : Button
    private var photoUploaded : Boolean = false
    private var pdfUploaded = mutableListOf(false,false,false,false)

    private lateinit var user_std : User_student


    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri ->
        uri?.let {
            uploadImage(uri, USER_IMAGE_FOLDER)
            {
                if(it==null)
                {

                }
                else
                {
                    user_std.image = it
                    img1.setImageURI(uri)
                    photoUploaded = true
                }
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_details)

        var email = ""
        var password = ""
        Firebase.firestore.collection(STUDENT_USER)
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject<User_student>()!!
                    email = user.mail.toString()
                    password = user.pswrd.toString()
                }
            }
        user_std = User_student()


        img1 = findViewById(R.id.imageView1)
        img1.setOnLongClickListener {
            Toast.makeText(this,"Click to select passport size photo", Toast.LENGTH_SHORT).show()
            true
        }
        img1.setOnClickListener {
            launcher.launch("image/*")
        }


        textInp2 = findViewById(R.id.textInput2)
        textInp3 = findViewById(R.id.textInput3)
        textInp4 = findViewById(R.id.textInput4)
        textInp6 = findViewById(R.id.textInput6)
        textInp7 = findViewById(R.id.textInput7)
        textInp8 = findViewById(R.id.textInput8)
        textInp9 = findViewById(R.id.textInput9)
        textInp10 = findViewById(R.id.textInput10)
        textInp11 = findViewById(R.id.textInput11)


        spinner5 = findViewById(R.id.spinner5)
        spinnerError5 = findViewById(R.id.spinnerError5)
        val adapter5 = ArrayAdapter.createFromResource(this,R.array.gender_list,R.layout.spinner_closed_layout)
        adapter5.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner5.adapter = adapter5
        spinner5.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerError5.visibility = View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinnerError5.visibility = View.VISIBLE
            }
        }


        spinner12 = findViewById(R.id.spinner12)
        val adapter12 = ArrayAdapter.createFromResource(this,R.array.quota_list,R.layout.spinner_closed_layout)
        adapter12.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner12.adapter = adapter12
        spinner12.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                textView12.text = "Upload all documents in 1 pdf (lesser than 1MB)"
                textView12.setTextColor(resources.getColor(android.R.color.white))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                textView12.text = "Select quota"
                textView12.setTypeface(null,Typeface.BOLD)
                textView12.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
        }


        textView7 = findViewById(R.id.textView7)
        textView7.setOnClickListener {
            val intent1 = Intent(Intent.ACTION_GET_CONTENT)
            intent1.type = "application/pdf"
            intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
            startActivityForResult(Intent.createChooser(intent1,"Select marksheet"), PICK_PDF_REQUEST7)
        }
        progBar7 = findViewById(R.id.progressBar7)


        textView8 = findViewById(R.id.textView8)
        textView8.setOnClickListener {
            val intent2 = Intent(Intent.ACTION_GET_CONTENT)
            intent2.type = "application/pdf"
            intent2.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
            startActivityForResult(Intent.createChooser(intent2,"Select marksheet"), PICK_PDF_REQUEST8)
        }
        progBar8 = findViewById(R.id.progressBar8)


        textView9 = findViewById(R.id.textView9)
        textView9.setOnClickListener {
            val intent3 = Intent(Intent.ACTION_GET_CONTENT)
            intent3.type = "application/pdf"
            intent3.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
            startActivityForResult(Intent.createChooser(intent3,"Select id proof"), PICK_PDF_REQUEST9)
        }
        progBar9 = findViewById(R.id.progressBar9)


        textView12 = findViewById(R.id.textView12)
        textView12.setOnClickListener {
            val intent4 = Intent(Intent.ACTION_GET_CONTENT)
            intent4.type = "application/pdf"
            intent4.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
            startActivityForResult(Intent.createChooser(intent4,"Select documents"), PICK_PDF_REQUEST12)
        }
        progBar12 = findViewById(R.id.progressBar12)


        btn1 = findViewById(R.id.button1)
        btn1.setOnClickListener {
            val et2 = textInp2.editText!!
            val et3 = textInp3.editText!!
            val et4 = textInp4.editText!!
            val et6 = textInp6.editText!!
            val et7 = textInp7.editText!!
            val et8 = textInp8.editText!!
            val et9 = textInp9.editText!!
            val et10 = textInp10.editText!!
            val et11 = textInp11.editText!!

            if (photoUploaded==false)
            {
                Toast.makeText(this,"Click on the profile icon to select an image",Toast.LENGTH_SHORT).show()
            }
            else if (et2.text.toString()=="")
            {
                et2.error = "Enter first name"
            }
            else if (et3.text.toString()=="")
            {
                et3.error = "Enter last name"
            }
            else if (et4.text.toString()=="")
            {
                et4.error = "Enter date of birth"
            }
            else if (spinner5.selectedItemPosition == 0)
            {
                spinnerError5.visibility = View.VISIBLE
            }
            else if (et6.text.toString()=="")
            {
                et6.error = "Enter educational qualifications"
            }
            else if (et7.text.toString()=="")
            {
                et7.error = "Enter SSC percentage"
            }
            else if (pdfUploaded.elementAt(0)==false)
            {
                textView7.setTypeface(null,Typeface.BOLD)
                textView7.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
            else if (et8.text.toString()=="")
            {
                et8.error = "Enter HSC percentage"
            }
            else if (pdfUploaded.elementAt(1)==false)
            {
                textView8.setTypeface(null,Typeface.BOLD)
                textView8.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
            else if (et9.text.toString()=="")
            {
                et9.error = "Enter nationality"
            }
            else if (pdfUploaded.elementAt(2)==false)
            {
                textView9.setTypeface(null,Typeface.BOLD)
                textView9.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
            else if (et10.text.toString()=="")
            {
                et10.error = "Enter contact number"
            }
            else if (et11.text.toString()=="")
            {
                et11.error = "Enter address"
            }
            else if (spinner12.selectedItemPosition == 0)
            {
                textView12.text = "Select quota"
                textView12.setTypeface(null,Typeface.BOLD)
                textView12.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
            else if (pdfUploaded.elementAt(3)==false)
            {
                if (spinner12.selectedItemPosition != 1)
                {
                    textView12.text = "Upload all documents in 1 pdf (lesser than 1 MB)"
                    textView12.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
                else
                {
                    pdfUploaded[3] = true
                }
            }
            else
            {
                user_std.mail = email
                user_std.pswrd = password
                user_std.first_name = et2.text.toString()
                user_std.last_name = et3.text.toString()
                user_std.dob = et4.text.toString()
                user_std.gender = spinner5.selectedItem.toString()
                user_std.education = et6.text.toString()
                user_std.ssc = et7.text.toString().toFloat()
                user_std.hsc = et8.text.toString().toFloat()
                user_std.nationality = et9.text.toString()
                user_std.contact_number = et10.text.toString()
                user_std.address = et11.text.toString()
                user_std.quota = spinner12.selectedItem.toString()

                Firebase.firestore.collection(STUDENT_USER)
                    .document(Firebase.auth.currentUser!!.uid).set(user_std)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Registration completed", Toast.LENGTH_SHORT).show()

                        val intent4 = Intent(this,Student_homescreen::class.java)
                        startActivity(intent4)
                        finish()
                    }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PDF_REQUEST7 && resultCode == Activity.RESULT_OK && data!=null && data.data!=null)
        {
            pdfUri7 = data.data
            if (validatePdf(pdfUri7!!))
            {
                uploadPdf(pdfUri7!!,progBar7,textView7,0)
            }
            else
            {
                textView7.text = "Select PDF whose size is lesser than 1 MB"
                textView7.setTypeface(null,Typeface.BOLD)
                textView7.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
        }
        else if (requestCode == PICK_PDF_REQUEST8 && resultCode == Activity.RESULT_OK && data!=null && data.data!=null)
        {
            pdfUri8 = data.data
            if (validatePdf(pdfUri8!!))
            {
                uploadPdf(pdfUri8!!,progBar8,textView8,1)
            }
            else
            {
                textView8.text = "Select PDF whose size is lesser than 1 MB"
                textView7.setTypeface(null,Typeface.BOLD)
                textView8.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
        }
        else if (requestCode == PICK_PDF_REQUEST9 && resultCode == Activity.RESULT_OK && data!=null && data.data!=null)
        {
            pdfUri9 = data.data
            if (validatePdf(pdfUri9!!))
            {
                uploadPdf(pdfUri9!!,progBar9,textView9,2)
            }
            else
            {
                textView9.text = "Select PDF whose size is lesser than 1 MB"
                textView7.setTypeface(null,Typeface.BOLD)
                textView9.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
        }
        else if (requestCode == PICK_PDF_REQUEST12 && resultCode == Activity.RESULT_OK && data!=null && data.data!=null)
        {
            pdfUri12 = data.data
            if (validatePdf(pdfUri12!!))
            {
                uploadPdf(pdfUri12!!,progBar12,textView12,3)
            }
            else
            {
                textView12.text = "Select PDF whose size is lesser than 1 MB"
                textView12.setTypeface(null,Typeface.BOLD)
                textView12.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
        }
    }



    private fun validatePdf(uri: Uri): Boolean {
        val inputStream: InputStream ?= contentResolver.openInputStream(uri)
        val sizeInBytes = inputStream?.available() ?: 0
        inputStream?.close()
        return sizeInBytes < (1* 1024*1024)
    }



    private fun uploadPdf(uri: Uri, progressBar: ProgressBar, textView: TextView, index: Int) {
        if (uri != null)
        {
            val userId = Firebase.auth.currentUser!!.uid
            val storageRef = Firebase.storage.reference.child("Student_PDFs/$userId/${userId}_pdf${index+1}.pdf")
            progressBar.visibility = View.VISIBLE
            storageRef.putFile(uri)
                .addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred)/it.totalByteCount
                    progressBar.progress = progress.toInt()
                }
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val filename = getFileName(uri)
                        savePdfUrl(userId,downloadUrl.toString(),progressBar,textView,filename,index)
                    }
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
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



    private fun savePdfUrl(userId: String, pdfUrl: String, progressBar: ProgressBar, textView: TextView, fileName: String, index: Int) {
        if (userId != null)
        {
            Firebase.firestore.collection(STUDENT_USER).document(userId)
                .update("Student_pdfs",FieldValue.arrayUnion(pdfUrl))
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE
                    textView.error = null
                    textView.text = fileName
                    textView.setTextColor(Color.parseColor("#FFFFFF"))
                    pdfUploaded[index] = true
                    Toast.makeText(this,"PDF uploaded successfully",Toast.LENGTH_SHORT).show()
                }
        }
    }
}