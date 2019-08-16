package com.beingtechnicalperson.devendrasingh.my_socialsignup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.beingtechnicalperson.devendrasingh.my_socialsignup.Facebook.FacebookActivity
import com.beingtechnicalperson.devendrasingh.my_socialsignup.Google.GoogleActivity
import com.beingtechnicalperson.devendrasingh.my_socialsignup.Linkdin.LinkdinSignUp
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inIt()
    }

    private fun inIt() {
        google.setOnClickListener(this)
        facebook.setOnClickListener(this)
        linkdin.setOnClickListener(this)
        twiter.setOnClickListener(this)
    }
    override fun onClick(position: View) {
        when(position.id)
        {
            R.id.google ->
            {
              startActivity(Intent(this,GoogleActivity::class.java))
            }
            R.id.facebook ->
            {
                startActivity(Intent(this,FacebookActivity::class.java))
            }
            R.id.linkdin ->
            {
                startActivity(Intent(this,LinkdinSignUp::class.java))
            }
            R.id.twiter ->
            {
                Toast.makeText(this,"Will be Done Soon",Toast.LENGTH_SHORT).show()
            }
        }
    }

}
