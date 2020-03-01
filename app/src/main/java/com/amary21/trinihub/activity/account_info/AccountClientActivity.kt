package com.amary21.trinihub.activity.account_info

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import com.amary21.trinihub.R
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_account_client.*
import kotlinx.android.synthetic.main.content_account_client.*


class AccountClientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_client)
        setSupportActionBar(toolbarClientInfo)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getIntentData(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackPressed()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getIntentData(intent: Intent) {

        //Person Data
        supportActionBar?.title = intent.getStringExtra("displayName")
        tvFirstNameClient.text = intent.getStringExtra("firstName")
        tvLastNameClient.text = intent.getStringExtra("lastName")
        tvNicknameClient.text = intent.getStringExtra("nickName")

        //Email Data
        tvFirstEmailClient.text = intent.getStringExtra("firstEmail")
        tvMainEmailClient.text = intent.getStringExtra("mainEmail")

        //Phone Number Data
        tvPhoneClient.text = intent.getStringExtra("phoneNumber")

        //Photo Data
        val mIsPhoto = intent.getBooleanExtra("isPhoto", false)
        if (mIsPhoto){
            val mPhotoProfile = intent.getByteArrayExtra("photoProfile")
            val bitmap = BitmapFactory.decodeByteArray(mPhotoProfile, 0, mPhotoProfile.size)
            imgPhotoClientProfile.setImageBitmap(bitmap)
            Blurry.with(this).from(bitmap).into(imgBannerClientProfile)
            imgPhotoClientProfile.visibility = View.VISIBLE
            rlProfileClient.visibility = View.GONE

            Palette.from(bitmap).generate {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val defaultValue = 0x000000
                    if (it != null) {
                        val color = it.getDominantColor(defaultValue)
                        window.statusBarColor = color
                        toolbar_layoutClient.setContentScrimColor(color)

                        tvBarPerson.setBackgroundColor(color)
                        tvBarEmail.setBackgroundColor(color)
                        tvBarJob.setBackgroundColor(color)
                        tvBarPhone.setBackgroundColor(color)
                    }
                }
            }
        }else{
            val mNamePhotoProfile = intent.getStringExtra("nameProfile")
            imgPhotoClientProfile.visibility = View.GONE
            rlProfileClient.visibility = View.VISIBLE
            tvNameProfileClient.text = mNamePhotoProfile
        }

        //Job Data
        tvJobTitleClient.text = intent.getStringExtra("jobTitle")
        tvCompanyClient.text = intent.getStringExtra("company")
    }
}
