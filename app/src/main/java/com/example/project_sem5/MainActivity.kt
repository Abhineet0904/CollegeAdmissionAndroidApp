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
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.project_sem5.Utils.STUDENT_USER
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var tv1 : TextView
    private lateinit var tv2 : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        tv1 = findViewById(R.id.textView1)
        tv2 = findViewById(R.id.textView2)


        if (Firebase.auth.currentUser!=null)
        {
            tv1.visibility = View.GONE
            tv2.visibility = View.GONE

            val mail = Firebase.auth.currentUser!!.email

            Firebase.firestore.collection(STUDENT_USER)
                .whereEqualTo("mail",mail).get()
                .addOnSuccessListener { querySnapshot1 ->

                    if (querySnapshot1.isEmpty == false)
                    {
                        val intent1 = Intent(this, Student_homescreen::class.java)
                        startActivity(intent1)
                        finish()
                    }
                    else
                    {
                        val intent2 = Intent(this, College_homescreen::class.java)
                        startActivity(intent2)
                        finish()
                    }
                }
        }
        else
        {
            val originalText1 = tv1.text
            val appendedText = "Click here"
            val spannableString1 = SpannableString(appendedText)
            spannableString1.setSpan(StyleSpan(Typeface.BOLD), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString1.setSpan(UnderlineSpan(), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString1.setSpan(ForegroundColorSpan(Color.parseColor("#003CA2")), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val clickableSpan1 = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent1 = Intent(this@MainActivity,Student_signup::class.java)
                    startActivity(intent1)
                }
            }
            spannableString1.setSpan(clickableSpan1, 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tv1.text = originalText1
            tv1.append(spannableString1)
            tv1.movementMethod = android.text.method.LinkMovementMethod.getInstance()


            val originalText2 = tv2.text
            val spannableString2 = SpannableString(appendedText)
            spannableString2.setSpan(StyleSpan(Typeface.BOLD), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString2.setSpan(UnderlineSpan(), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString2.setSpan(ForegroundColorSpan(Color.parseColor("#003CA2")), 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val clickableSpan2 = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent2 = Intent(this@MainActivity,College_signup::class.java)
                    startActivity(intent2)
                }
            }
            spannableString2.setSpan(clickableSpan2, 0, appendedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tv2.text = originalText2
            tv2.append(spannableString2)
            tv2.movementMethod = android.text.method.LinkMovementMethod.getInstance()
        }
    }
}