package com.beingtechnicalperson.devendrasingh.my_socialsignup.Google

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import com.beingtechnicalperson.devendrasingh.my_socialsignup.R
import kotlinx.android.synthetic.main.activity_google.*

class GoogleActivity : AppCompatActivity(), Google_SignUp.OnClientConnectedListener, View.OnClickListener {


    lateinit var google_SignUp: Google_SignUp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google)
        inIt()
    }

    private fun inIt() {
        google_SignUp = Google_SignUp(this, this)
        google_signUp.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Google_SignUp.RC_SIGN_IN)
            google_SignUp.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(position: View) {
        when (position.id) {
            R.id.google_signUp -> {
                google_SignUp.signIn()
            }

        }
    }

    override fun onClientFailed(msg: String?) {
    }

    override fun onGoogleProfileFetchComplete(id: String?, displayName: String?, email: String?) {
        setValues(id, displayName, email)
    }

    private fun setValues(id: String?, displayName: String?, email: String?) {
        name_TV.setText(displayName)
        email_TV.setText(email)
        socialID_TV.setText(id)
    }
}