package com.amary21.trinihub.activity.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.proxy.users.IUserProxy
import com.ale.listener.SigninResponseListener
import com.ale.listener.StartResponseListener
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.main.MainActivity
import com.amary21.trinihub.data.local.Account
import com.amary21.trinihub.data.local.database
import com.amary21.trinihub.data.network.APP_ID
import com.amary21.trinihub.data.network.APP_SECRET_KEY
import com.amary21.trinihub.data.network.ApiClient
import com.amary21.trinihub.data.network.model.AuthorizationResponse
import com.amary21.trinihub.data.network.model.InsertHubResponse
import com.amary21.trinihub.data.network.model.UserResponse
import com.amary21.trinihub.utils.ConvertEncode
import com.amary21.trinihub.utils.UserPreferences
import com.amary21.trinihub.utils.UtilsForm
import kotlinx.android.synthetic.main.activity_splash.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.anko.db.insert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (UserPreferences.isLogin(this)) {
            startRainbowConnectionWithoutDB(
                UserPreferences.getEmailId(this),
                UserPreferences.getPasswordId(this)
            )
        } else {
            Handler().postDelayed({
                showLogin()
                formLogin()
            }, 1000)
        }

    }

    private fun formLogin() {
        val email = edtEmailLogin.text
        val password = edtPasswordLogin.text

        btnLogin.setOnClickListener {
            when {
                edtEmailLogin.text.isEmpty() -> {
                    edtEmailLogin.error = "Email is empty"
                    tvErrorLogin.visibility = View.GONE
                    spLoadingLogin.visibility = View.INVISIBLE
                    return@setOnClickListener
                }
                edtPasswordLogin.text.isEmpty() -> {
                    edtPasswordLogin.error = "Password is empty"
                    tvErrorLogin.visibility = View.GONE
                    spLoadingLogin.visibility = View.INVISIBLE
                    return@setOnClickListener
                }
                else -> {
                    doLogin(email, password)
                }
            }
        }
    }

    private fun doLogin(email: Editable, password: Editable) {
        tvErrorLogin.visibility = View.GONE
        spLoadingLogin.visibility = View.VISIBLE

        edtEmailLogin.isEnabled = false
        edtPasswordLogin.isEnabled = false
        btnLogin.isEnabled = false

        val authorization =
            "Basic " + ConvertEncode.base64Encode(email.toString(), password.toString())
        val appAuth = "Basic " + ConvertEncode.base64Encode(
            APP_ID, ConvertEncode.toHexString(
                ConvertEncode.getSHA(
                    APP_SECRET_KEY + password.toString()
                )
            ).toString()
        )

        Log.e("DATA AUTH", authorization)
        Log.e(
            "DATA HASH", ConvertEncode.base64Encode(
                APP_ID, ConvertEncode.toHexString(
                    ConvertEncode.getSHA(
                        APP_SECRET_KEY + password.toString()
                    )
                ).toString()
            )
        )

        ApiClient
            .getClient()
            .login(authorization, appAuth)
            .enqueue(object : Callback<AuthorizationResponse> {
                override fun onFailure(call: Call<AuthorizationResponse>, t: Throwable) {
                    Toast.makeText(this@SplashActivity, "Something Wrong!", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("onFailure", "onFailure: " + t.message.toString())

                    spLoadingLogin.visibility = View.VISIBLE

                    edtEmailLogin.isEnabled = true
                    edtPasswordLogin.isEnabled = true
                    btnLogin.isEnabled = true
                }

                override fun onResponse(
                    call: Call<AuthorizationResponse>,
                    response: Response<AuthorizationResponse>
                ) {
                    if (response.isSuccessful) {
                        val auth = response.body()

                        loadUserInfo(
                            auth?.loggedInUser?.id,
                            auth?.token,
                            email.toString(),
                            password.toString()
                        )

                    } else {
                        Log.e("onResponse", "onResponse: " + response.message())
                        tvErrorLogin.text = getString(R.string.failed_login)

                        spLoadingLogin.visibility = View.GONE
                        tvErrorLogin.visibility = View.VISIBLE

                        edtEmailLogin.isEnabled = true
                        edtPasswordLogin.isEnabled = true
                        btnLogin.isEnabled = true
                    }
                }

            })
    }

    private fun showLogin() {

        val transitionLogin = Fade()
        val transitionTitle = Fade()
        val transitionLoading = Fade()

        transitionLogin.duration = 800
        transitionLogin.addTarget(cvLayoutLogin)

        transitionTitle.duration = 1000
        transitionTitle.addTarget(txtTitle)

        transitionLoading.duration = 1000
        transitionLoading.addTarget(spLoadingSplash)

        TransitionManager.beginDelayedTransition(layoutSplash, transitionLogin)
        TransitionManager.beginDelayedTransition(layoutSplash, transitionTitle)
        TransitionManager.beginDelayedTransition(layoutSplash, transitionLoading)

        cvLayoutLogin.visibility = View.VISIBLE
        txtTitle.visibility = View.GONE
        spLoadingSplash.visibility = View.GONE
    }

    private fun loadUserInfo(userId: String?, token: String?, email: String?, password: String?) {
        ApiClient.getClient()
            .getUser("Bearer $token", userId)
            .enqueue(object : Callback<UserResponse> {
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@SplashActivity, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    Log.e("ERROR USER INFO", t.message.toString())
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful) {

                        val user = response.body()?.data
                        var emailWork: String? = null
                        var emailHome: String? = null
                        var phone: String? = null

                        if (user != null) {

                            if (user.emails != null) {
                                for (iEmail in user.emails) {
                                    if (iEmail.type == "work") {
                                        emailWork = iEmail.email
                                    }

                                    if (iEmail.type == "home") {
                                        emailHome = iEmail.email
                                    }
                                }
                            }

                            if (user.phoneNumbers != null) {
                                for (iPhone in user.phoneNumbers) {
                                    if (iPhone.type.equals("work")) {
                                        Log.e("DATA PHONEEEE", iPhone.type.toString())
                                        phone = iPhone.number.toString()

                                        if (RainbowSdk.instance().myProfile().connectedUser.firstOfficePhoneNumber != null) {
                                            RainbowSdk.instance().myProfile()
                                                .updateOfficePhoneNumber(
                                                    phone,
                                                    "IDN",
                                                    object : IUserProxy.IUsersListener {
                                                        override fun onSuccess() {
                                                            Log.e("UPDATE PHONE", "SUKSES$phone")
                                                        }

                                                        override fun onFailure(p0: RainbowServiceException?) {
                                                            Log.e("UPDATE PHONE", p0.toString())
                                                        }

                                                    })
                                        }
                                    }
                                }
                            }

                            insertUserHub(
                                email.toString(),
                                password.toString(),
                                user.id,
                                token,
                                user.firstName,
                                user.lastName,
                                user.displayName,
                                user.jobTitle,
                                emailHome,
                                emailWork,
                                phone,
                                user.language,
                                user.country,
                                user.nickName,
                                user.companyName
                            )
                        }
                    } else {
                        val profileRainbow = RainbowSdk.instance().myProfile().connectedUser

                        insertUserHub(
                            email.toString(),
                            password.toString(),
                            userId,
                            token,
                            profileRainbow.firstName,
                            profileRainbow.lastName,
                            profileRainbow.getDisplayName("Unknown"),
                            profileRainbow.jobTitle,
                            profileRainbow.firstEmailAddress,
                            profileRainbow.mainEmailAddress,
                            if (profileRainbow.firstOfficePhoneNumber != null) profileRainbow.firstOfficePhoneNumber.phoneNumberValue else null,
                            null,
                            null,
                            profileRainbow.nickName,
                            profileRainbow.companyName
                        )

                    }
                }

            })
    }



    private fun insertUserHub(
        email: String,
        password: String,
        id: String?,
        token: String?,
        firstName: String?,
        lastName: String?,
        displayName: String?,
        jobTitle: String?,
        emailHome: String?,
        emailWork: String?,
        phone: String?,
        language: String?,
        country: String?,
        nickName: String?,
        companyName: String?
    ) {

        val mId = id ?: "-"
        val mName = displayName ?: "-"
        val mPhone = phone ?: "-"
        val mJobTitle = jobTitle ?: "-"

        val map: HashMap<String, RequestBody?> = HashMap()
        map["idUser"] = UtilsForm.createFormString(mId)
        map["name"] = UtilsForm.createFormString(mName)
        map["email"] = UtilsForm.createFormString(email)
        map["phone"] = UtilsForm.createFormString(mPhone)
        map["jobTitle"] = UtilsForm.createFormString(mJobTitle)

        ApiClient.getClientHub().insertUserHub(map)
            .enqueue(object : Callback<InsertHubResponse> {
                override fun onFailure(call: Call<InsertHubResponse>, t: Throwable) {
                    Log.e("ERROR USER INFO HUB", t.message.toString())
                }

                override fun onResponse(
                    call: Call<InsertHubResponse>,
                    response: Response<InsertHubResponse>
                ) {
                    if (response.isSuccessful) {
                        val message = response.body()?.message
                        if (message == getString(R.string.success) || message == getString(R.string.available)) {
                            startRainbowConnection(
                                email,
                                password,
                                id,
                                token,
                                firstName,
                                lastName,
                                displayName,
                                jobTitle,
                                emailHome,
                                emailWork,
                                phone,
                                language,
                                country,
                                nickName,
                                companyName
                            )
                        } else {
                            Log.e("ERROR USER INFO HUB", message.toString() + response.message())

                        }
                    }
                }

            })
    }

    private fun startRainbowConnection(
        emailId: String?,
        passwordId: String?,
        id: String?,
        token: String?,
        firstName: String?,
        lastName: String?,
        displayName: String?,
        jobTitle: String?,
        emailHome: String?,
        emailWork: String?,
        phone: String?,
        language: String?,
        country: String?,
        nickName: String?,
        companyName: String?
    ) {

        RainbowSdk.instance().connection().start(object : StartResponseListener() {
            override fun onRequestFailed(p0: RainbowSdk.ErrorCode?, p1: String?) {
                Log.e("CONECTION", p0.toString())
            }

            override fun onStartSucceeded() {
                RainbowSdk.instance().connection().signin(emailId,
                    passwordId, "sandbox.openrainbow.com", object : SigninResponseListener() {
                        override fun onRequestFailed(p0: RainbowSdk.ErrorCode?, p1: String?) {
                            Log.e("LOGIN", p0.toString())

                        }

                        override fun onSigninSucceeded() {
                            try {
                                database.use {
                                    insert(
                                        Account.TABLE_ACCOUNT,
                                        Account.ID_USER to id,
                                        Account.TOKEN to token,
                                        Account.FIRST_NAME to firstName,
                                        Account.LAST_NAME to lastName,
                                        Account.DISPLAY_NAME to displayName,
                                        Account.JOB_TITLE to jobTitle,
                                        Account.HOME_EMAIL to emailHome,
                                        Account.WORK_EMAIL to emailWork,
                                        Account.PHONE to phone,
                                        Account.LANGUAGE to language,
                                        Account.LOCATION to country,
                                        Account.LOGIN_EMAIL to emailId,
                                        Account.PASSWORD to passwordId,
                                        Account.NICKNAME to nickName,
                                        Account.COMPANY to companyName
                                    )
                                }
                                UserPreferences.setTokenUser(this@SplashActivity, token)
                                UserPreferences.setUserId(this@SplashActivity, id)
                                emailId?.let { UserPreferences.setEmailId(this@SplashActivity, it) }
                                passwordId?.let {
                                    UserPreferences.setPasswordId(
                                        this@SplashActivity,
                                        it
                                    )
                                }

                                UserPreferences.hasLogin(this@SplashActivity)

                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))

                            } catch (e: SQLiteConstraintException) {
                                Log.e("ERROR SQL", e.message.toString())
                            }

                        }

                    })
            }

        })


    }


    private fun startRainbowConnectionWithoutDB(emailId: String?, passwordId: String?) {
        RainbowSdk.instance().connection().start(object : StartResponseListener() {
            override fun onRequestFailed(p0: RainbowSdk.ErrorCode?, p1: String?) {
                Log.e("CONECTION", p0.toString())
            }

            override fun onStartSucceeded() {
                RainbowSdk.instance().connection().signin(emailId,
                    passwordId, "sandbox.openrainbow.com", object : SigninResponseListener() {
                        override fun onRequestFailed(p0: RainbowSdk.ErrorCode?, p1: String?) {
                            Log.e("LOGIN", p0.toString())

                        }

                        override fun onSigninSucceeded() {
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))

                        }

                    })
            }

        })
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
