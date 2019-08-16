package com.beingtechnicalperson.devendrasingh.my_socialsignup.Facebook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.beingtechnicalperson.devendrasingh.my_socialsignup.R
import com.trainerkit.facebook.OnFacebookLoginListener
import kotlinx.android.synthetic.main.activity_facebook.*

class FacebookActivity : AppCompatActivity(), View.OnClickListener, OnFacebookLoginListener {

    lateinit var facebookSignup:FacebookManagerTemp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook)
        inIt()
    }

    private fun inIt() {
        facebook_signUp.setOnClickListener(this)
        facebookSignup = FacebookManagerTemp(this)
        facebookSignup.setOnLoginListener(this)

    }

    override fun onClick(position: View) {
        when (position.id) {
            R.id.facebook_signUp -> {
               facebookSignup.doLogin()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookSignup.onActivityResult(requestCode,resultCode,data)
    }

    override fun onFacebookLogin(name: String, email: String, id: String, imageUrl: String, gender: String) {
        setValues(name,email,id)
    }

    private fun setValues(name: String, email: String, id: String) {
        nameFace_TV.setText(name)
        emailFace_TV.setText(email)
        socialIDFace_TV.setText(id)
    }

    override fun onFacebookError(message: String) {
    }

}
