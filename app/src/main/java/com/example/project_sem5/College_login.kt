package com.example.project_sem5

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.project_sem5.Utils.COLLEGE_USER
import com.example.project_sem5.Utils.STUDENT_USER
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class College_login : AppCompatActivity() {
    private lateinit var textInp1 : TextInputLayout
    private lateinit var textInp2 : TextInputLayout
    private lateinit var btn1 : Button
    private lateinit var tv1 : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_college_login)

        textInp1 = findViewById(R.id.textInput1)
        textInp2 = findViewById(R.id.textInput2)

        btn1 = findViewById(R.id.button1)
        btn1.setOnClickListener {
            val et1 = textInp1.editText!!
            val et2 = textInp2.editText!!

            if (et1.text.toString()=="")
            {
                et1.error = "Enter email address"
            }
            else if (et2.text.toString()=="")
            {
                et2.error = "Enter password"
            }
            else
            {
                val mail = et1.text.toString()
                val pswrd = et2.text.toString()

                Firebase.firestore.collection(COLLEGE_USER)
                    .whereEqualTo("mail",mail).get()
                    .addOnSuccessListener { querySnapshot1 ->

                        if (querySnapshot1.isEmpty == false)
                        {
                            Firebase.auth.signInWithEmailAndPassword(mail, pswrd)
                                .addOnCompleteListener {
                                    if (it.isSuccessful)
                                    {
                                        val intent1 = Intent(this, College_homescreen::class.java)
                                        intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent1)
                                        finish()
                                    }
                                    else
                                    {
                                        Toast.makeText(this, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        else
                        {
                            Firebase.firestore.collection(STUDENT_USER)
                                .whereEqualTo("mail",mail).get()
                                .addOnSuccessListener { querySnapshot2 ->

                                    if (querySnapshot2.isEmpty == false)
                                    {
                                        Toast.makeText(this,"This is a student account",Toast.LENGTH_SHORT).show()
                                    }
                                    else
                                    {
                                        Toast.makeText(this,"This account doesn't exist",Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
            }
        }


        tv1 = findViewById(R.id.textView1)
        val originalText = tv1.text
        val appendedText = "Forgot password?"
        val spannableString = SpannableString(appendedText)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(UnderlineSpan(), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#003CA2")), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val mail = textInp1.editText!!.text.toString()
                if (textInp1.editText!!.text.toString()=="")
                {
                    textInp1.editText!!.error = "Enter email even if you forgot password"
                }
                else
                {
                    Firebase.auth.sendPasswordResetEmail(mail)
                        .addOnCompleteListener {
                            if (it.isSuccessful)
                            {
                                Toast.makeText(baseContext, "Password reset email sent", Toast.LENGTH_SHORT).show()
                            }
                            else
                            {
                                Toast.makeText(baseContext, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
        spannableString.setSpan(clickableSpan, 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv1.text = originalText
        tv1.append(spannableString)
        tv1.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }
}