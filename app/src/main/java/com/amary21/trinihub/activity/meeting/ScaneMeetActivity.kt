package com.amary21.trinihub.activity.meeting

import android.Manifest
import android.app.ProgressDialog
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amary21.trinihub.R
import com.amary21.trinihub.data.local.Account
import com.amary21.trinihub.data.local.GuestList
import com.amary21.trinihub.data.local.database
import com.amary21.trinihub.data.network.ApiClient
import com.amary21.trinihub.data.network.model.InsertHubResponse
import com.amary21.trinihub.data.network.model.MeetHub
import com.budiyev.android.codescanner.CodeScanner
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_scane_meet.*
import okhttp3.MultipartBody
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("DEPRECATION")
class ScaneMeetActivity : AppCompatActivity() {
    private var mCodeScanner: CodeScanner? = null
    private var dataScane: MeetHub? = null
    private var idUser: String? = null
    private var idMeet: String? = null

    companion object {
        const val SCANE_INVITE_CODE = "com.amary21.trinihub.activity.meeting.Get_INVITE_CODE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scane_meet)
        btnCheckInvitation.isEnabled = false

        dataScane = intent.getParcelableExtra(SCANE_INVITE_CODE)
        idUser = getUserId()
        idMeet = dataScane?.id_meet

        mCodeScanner = CodeScanner(this, scannerView)
        mCodeScanner!!.setDecodeCallback {
            runOnUiThread {
                getInvitationMeeting(it.text)
            }
        }

        checkCameraPermission()
    }

    private fun getInvitationMeeting(text: String?) {
        if (text != null) {
            spLoadingInvite.visibility = View.GONE
            btnCheckInvitation.isEnabled = true
            btnCheckInvitation.setOnClickListener {
                if (text == dataScane?.invitation) {
                    setGuestList()
                } else {
                    val builderGuest = AlertDialog.Builder(this)
                    builderGuest.apply {
                        setMessage("data is in trouble")
                        setCancelable(true)
                        setPositiveButton(
                            "Scan Again"
                        ) { dialog, _ ->
                            dialog?.cancel()
                            mCodeScanner?.startPreview()
                        }

                        setNegativeButton(
                            "Cancel"
                        ) { dialog, _ ->
                            dialog.cancel()
                        }
                    }

                    val alertDialogGuest = builderGuest.create()
                    alertDialogGuest.show()
                }
            }
        }
    }

    private fun setGuestList() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()

        val builderGuest = AlertDialog.Builder(this)
        val requestBody =
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("idUser", idUser!!)
                .addFormDataPart("idMeet", idMeet!!)
                .build()

        ApiClient.getClientHub().insertGuestList(requestBody)
            .enqueue(object : Callback<InsertHubResponse> {
                override fun onFailure(call: Call<InsertHubResponse>, t: Throwable) {
                    progressDialog.cancel()
                    builderGuest.apply {
                        setMessage("data is in trouble")
                        setCancelable(true)
                        setPositiveButton(
                            "Scan Again"
                        ) { dialog, _ ->
                            dialog?.cancel()
                            mCodeScanner?.startPreview()
                        }

                        setNegativeButton(
                            "Cancel"
                        ) { dialog, _ ->
                            dialog.cancel()
                        }
                    }
                }

                override fun onResponse(
                    call: Call<InsertHubResponse>,
                    response: Response<InsertHubResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.message == getString(R.string.success)) {
                            enterMeeting(progressDialog, builderGuest, idUser!!, idMeet!!)

                        } else {
                            progressDialog.cancel()
                            builderGuest.apply {
                                setMessage("data is in trouble")
                                setCancelable(true)
                                setPositiveButton(
                                    "Scan Again"
                                ) { dialog, _ ->
                                    dialog?.cancel()
                                    mCodeScanner?.startPreview()
                                }

                                setNegativeButton(
                                    "Cancel"
                                ) { dialog, _ ->
                                    dialog.cancel()
                                }
                            }
                        }
                    }
                }
            })

        val alertDialogGuest = builderGuest.create()
        alertDialogGuest.show()
    }

    private fun enterMeeting(
        progressDialog: ProgressDialog,
        builderGuest: AlertDialog.Builder,
        idUser: String,
        idMeet: String
    ) {
        try {
            database.use {
                insert(
                    GuestList.TABLE_ACCOUNT,
                    GuestList.ID_USER to idUser,
                    GuestList.ID_MEET to idMeet
                )
            }
            progressDialog.dismiss()
            builderGuest.apply {
                setMessage(getString(R.string.have_entered))
                setCancelable(true)
                setPositiveButton(
                    "Ok"
                ) { dialog, _ ->
                    dialog?.cancel()
                    onBackPressed()
                    finish()
                }
            }
        } catch (e: SQLiteConstraintException) {
            Log.e("ERROR SQL", e.message.toString())
        }
    }

    private fun getUserId(): String {
        var idUser: String? = null
        try {
            database.use {
                val result = select(Account.TABLE_ACCOUNT)
                val account = result.parseList(classParser<Account>())
                idUser = account[0].idUser.toString()
            }
        } catch (e: SQLiteConstraintException) {
            idUser = null
        }

        return idUser.toString()
    }

    private fun checkCameraPermission() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    mCodeScanner?.startPreview()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                }

            }).check()
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }

    override fun onPause() {
        mCodeScanner?.releaseResources()
        super.onPause()
    }
}
