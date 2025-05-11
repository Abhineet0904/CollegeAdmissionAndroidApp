package com.example.project_sem5

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.project_sem5.Models_student.User_student
import com.example.project_sem5.Utils.STUDENT_USER
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class Student_signup : AppCompatActivity() {
    private lateinit var textInp1 : TextInputLayout
    private lateinit var textInp2 : TextInputLayout
    private lateinit var textInp3 : TextInputLayout

    private lateinit var btn1 : Button
    private lateinit var tv1 : TextView

    private lateinit var user_std : User_student

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_signup)

        user_std = User_student()

        textInp1 = findViewById(R.id.textInput1)
        textInp2 = findViewById(R.id.textInput2)
        textInp3 = findViewById(R.id.textInput3)

        btn1 = findViewById(R.id.button1)
        btn1.setOnClickListener {
            val et1 = textInp1.editText!!
            val et2 = textInp2.editText!!
            val et3 = textInp3.editText!!

            if (et1.text.toString()=="")
            {
                et1.error = "Enter your email address"
            }
            else if (et2.text.toString()=="")
            {
                et2.error = "Enter password"
            }
            else if (et3.text.toString()=="")
            {
                et3.error = "Confirm the password"
            }
            else
            {
                if (et1.text.toString().contains("@") && et1.text.toString().contains(".com"))
                {
                    if (et2.text.toString().length < 12)
                    {
                        et2.error = "Length of password should be atleast 12 characters"
                    }
                    else
                    {
                        if (et2.text.toString() != et3.text.toString())
                        {
                            et3.error = "Password and confirmation password should be same"
                        }
                        else
                        {
                            val mail = et1.text.toString()
                            val pswrd = et2.text.toString()

                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail,pswrd).addOnCompleteListener { result->
                                if (result.isSuccessful)
                                {
                                    user_std.mail = mail
                                    user_std.pswrd = pswrd

                                    Firebase.firestore.collection(STUDENT_USER)
                                        .document(Firebase.auth.currentUser!!.uid).set(user_std)
                                        .addOnSuccessListener {
                                            Toast.makeText(this,"Account created", Toast.LENGTH_SHORT).show()

                                            val intent1 = Intent(this,Student_details::class.java)
                                            startActivity(intent1)
                                            finish()
                                    }
                                }
                                else
                                {
                                    Toast.makeText(this,result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
                else
                {
                    et1.error = "Enter valid email address"
                }
            }
        }


        tv1 = findViewById(R.id.textView1)
        val originalText = tv1.text
        val appendedText = " Login"
        val spannableString = SpannableString(appendedText)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(UnderlineSpan(), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#003CA2")), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent2 = Intent(this@Student_signup, Student_login::class.java)
                startActivity(intent2)
            }
        }
        spannableString.setSpan(clickableSpan, 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv1.text = originalText
        tv1.append(spannableString)
        tv1.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }
}