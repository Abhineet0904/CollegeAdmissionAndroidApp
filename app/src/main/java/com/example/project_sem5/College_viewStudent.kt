package com.example.project_sem5

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.project_sem5.Models_student.User_student
import com.example.project_sem5.Utils.APPLICATIONS
import com.example.project_sem5.Utils.STUDENT_USER
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import java.io.File
import java.util.logging.Logger.global

class College_viewStudent : AppCompatActivity() {
    private lateinit var degreeName: String
    private lateinit var collegeName: String

    private lateinit var image: ImageView
    private lateinit var first_name: TextView
    private lateinit var last_name: TextView
    private lateinit var dob: TextView
    private lateinit var gender: TextView
    private lateinit var ssc: TextView
    private lateinit var hsc: TextView
    private lateinit var education: TextView
    private lateinit var nationality: TextView
    private lateinit var contact_number: TextView
    private lateinit var address: TextView
    private lateinit var quota: TextView
    private lateinit var Status: TextView
    private lateinit var downloadButton: Button

    private lateinit var button1: Button
    private lateinit var button2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_college_view_student)

        image = findViewById(R.id.imageView1)
        first_name = findViewById(R.id.textFirstName)
        last_name = findViewById(R.id.textLastName)
        dob = findViewById(R.id.textDOB)
        gender = findViewById(R.id.textGender)
        education = findViewById(R.id.textEducation)
        ssc = findViewById(R.id.textSSC)
        hsc = findViewById(R.id.textHSC)
        nationality = findViewById(R.id.textNationality)
        contact_number = findViewById(R.id.textContactNumber)
        address = findViewById(R.id.textAddress)
        quota = findViewById(R.id.textQuota)
        Status = findViewById(R.id.textViewApplicationStatus)
        downloadButton = findViewById(R.id.download_pdfs)

        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)


        val studentId = intent.getStringExtra("studentId") ?: ""
        loadStudentDetails(studentId)
        degreeName = intent.getStringExtra("degreeName") ?: ""
        collegeName = intent.getStringExtra("collegeName") ?: ""



        downloadButton.setOnClickListener {
            downloadStudentPdfs(studentId)
        }

        button1.setOnClickListener {
            updateApplicationStatus(studentId, true)
        }
        button2.setOnClickListener {
            updateApplicationStatus(studentId, false)
        }
    }



    private fun loadStudentDetails(studentId: String) {
        Firebase.firestore.collection(STUDENT_USER)
            .document(studentId).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val student = it.toObject<User_student>()!!

                    first_name.append(student.first_name)
                    last_name.append(student.last_name)
                    dob.append(student.dob)
                    gender.append(student.gender)
                    education.append(student.education)
                    ssc.append(student.ssc!!.toFloat().toString())
                    hsc.append(student.hsc!!.toFloat().toString())
                    nationality.append(student.nationality)
                    contact_number.append(student.contact_number)
                    address.append(student.address)
                    quota.append(student.quota)

                    Picasso.get().load(student.image).into(image)

                    getApplicationStatus(it.id)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }



    private fun getApplicationStatus(studentId: String) {
        Firebase.firestore.collection(APPLICATIONS)
            .whereEqualTo("studentId",studentId)
            .whereEqualTo("degreeName",degreeName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result)
                {
                    val status = document.getString("status")
                    if (Status.text.endsWith(" : "))
                    {
                        Status.append(status)
                    }
                    else
                    {
                        Status.text = "Application Status : "+status
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"Unable to retrieve application status",Toast.LENGTH_SHORT).show()
            }
    }



    private fun downloadStudentPdfs(studentId: String) {

        if (quota.text == "Quota : Open")
        {
            for (i in 1..3)
            {
                val pdfReference = Firebase.storage.reference.child(
                    "Student_PDFs/$studentId/${studentId}_pdf$i.pdf"
                )
                val localFile = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "${studentId}_pdf$i.pdf"
                )

                pdfReference.getFile(localFile)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Downloaded ${localFile.name}", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->

                        Toast.makeText(this, "Failed to download ${localFile.name}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        else
        {
            for (i in 1..4)
            {
                val pdfReference = Firebase.storage.reference.child(
                    "Student_PDFs/$studentId/${studentId}_pdf$i.pdf"
                )
                val localFile = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "${studentId}_pdf$i.pdf"
                )

                pdfReference.getFile(localFile)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Downloaded ${localFile.name}", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->

                        Toast.makeText(this, "Failed to download ${localFile.name}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }



    private fun updateApplicationStatus(studentId: String, isApproved: Boolean) {
        val status = if (isApproved) "Accepted" else "Rejected"
        Firebase.firestore.collection(APPLICATIONS)
            .whereEqualTo("studentId",studentId)
            .whereEqualTo("degreeName",degreeName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result)
                {
                    document.reference.update("status",status)
                        .addOnSuccessListener {
                            sendStudentEmail(studentId,status)
                            Toast.makeText(this, "Application has been $status", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error updating status: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }



    private fun sendStudentEmail(studentId: String, status: String) {
        getStudentEmail(studentId) { studentmail ->
            if (studentmail != null)
            {
                val subject = "Application "+status;
                val msg = "Your application associated with Student ID '"+
                        studentId+"'\nfor  '"+degreeName.toUpperCase()+"'\nin  '"+
                        collegeName.toUpperCase()+"'\nhas been  '"+status.toUpperCase()+"'."

                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(studentmail))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, msg)
                }

                try
                {
                    startActivity(Intent.createChooser(emailIntent, "Send email."))
                }
                catch (e: Exception)
                {
                    Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Student email could not be retrieved", Toast.LENGTH_SHORT).show()
            }
        }


    }



    private fun getStudentEmail(studentId: String, callback: (String?) -> Unit) {
        Firebase.firestore.collection(APPLICATIONS)
            .whereEqualTo("studentId", studentId)
            .whereEqualTo("degreeName", degreeName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Firebase.firestore.collection(STUDENT_USER)
                        .document(studentId).get()
                        .addOnSuccessListener {
                            val student = it.toObject<User_student>()!!

                            val studentMail = student.mail
                            callback(studentMail)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT)
                                .show()
                            callback(null)
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                callback(null)
            }
    }



    /*private fun notifyStudent(studentId: String, status: String) {
        val notificationMessage = if (status == "approved")
        {
            "Congratulations! Your application has been approved."
        }
        else
        {
            "We're sorry, but your application has been rejected."
        }
        Firebase.firestore.collection(STUDENT_USER).document(studentId).get()
            .addOnSuccessListener { document ->
                if (document.exists())
                {
                    val fcmToken = document.getString("fcmToken")
                    if (!fcmToken.isNullOrEmpty())
                    {
                        sendNotificationToServer(fcmToken, notificationMessage)
                    }
                    else
                    {
                        Toast.makeText(this, "FCM token not found for the student.", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(this, "Student document not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendNotificationToServer(token: String, message: String) {
        val jsonObject = JSONObject().apply {
            put("to", token)
            put("data", JSONObject().apply {
                put("message", message)
            })
        }
        val request = JsonObjectRequest(
            Request.Method.POST, "YOUR_SERVER_ENDPOINT", jsonObject,
            { response ->
                Toast.makeText(this, "Notification sent successfully!", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Error sending notification: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "key=YOUR_SERVER_KEY" // Use your server key from Firebase
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        Volley.newRequestQueue(this).add(request)
    }*/
}