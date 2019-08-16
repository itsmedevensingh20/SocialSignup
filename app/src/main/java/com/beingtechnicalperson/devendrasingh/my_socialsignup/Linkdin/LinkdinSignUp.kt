package com.beingtechnicalperson.devendrasingh.my_socialsignup.Linkdin

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.beingtechnicalperson.devendrasingh.my_socialsignup.R
import kotlinx.android.synthetic.main.activity_linkdin_sign_up.*

class LinkdinSignUp : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linkdin_sign_up)
        inIt()
    }

    private fun inIt() {
        linkdin_signUp.setOnClickListener(this)
    }

    override fun onClick(position: View) {
        when (position.id) {
            R.id.linkdin_signUp -> {
                startActivityForResult(Intent(this, NewLinkedInIntegration::class.java), 900)

            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
         if (requestCode == 900 && resultCode == Activity.RESULT_OK) {
             getAllLinkdinData(data)

         }  
    }

    private fun getAllLinkdinData(data: Intent?) {
        val intent = data
        val socialName = intent?.getStringExtra("email")
        val socialEmail = intent?.getStringExtra("firstname")
        val socialId = intent?.getStringExtra("id")
    }
}
