package com.amary21.trinihub.activity.meeting

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.amary21.trinihub.R
import com.amary21.trinihub.data.local.Account
import com.amary21.trinihub.data.local.database
import com.amary21.trinihub.data.network.ApiClient
import com.amary21.trinihub.data.network.model.InsertHubResponse
import com.amary21.trinihub.utils.UtilsForm
import com.bumptech.glide.Glide
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.vincent.filepicker.Constant
import com.vincent.filepicker.Constant.REQUEST_CODE_PICK_IMAGE
import com.vincent.filepicker.activity.ImagePickActivity
import com.vincent.filepicker.filter.entity.ImageFile
import kotlinx.android.synthetic.main.activity_create_meet.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Suppress(
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class CreateMeetActivity : AppCompatActivity() {
    private var image: MultipartBody.Part? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateMeet: String
    private lateinit var idUser: String
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meet)
        getDataLocalDB()

        btnBackCreateMeeting.setOnClickListener {
            onBackPressed()
            finish()
        }

        imgPhotoAddMeet.setOnClickListener {
            getInsertImage()
        }

        edtCategoryMeet.setOnClickListener {
            PopupMenu(this, edtCategoryMeet).apply {
                menuInflater.inflate(R.menu.menu_category_meet, menu)
                setOnMenuItemClickListener {
                    edtCategoryMeet.setText(it.title)
                    true
                }
                show()
            }
        }

        edtDateMeet.setOnClickListener {
            getInsertDate()
        }

        edtStartTimeMeet.setOnClickListener {
            getInsertTime(edtStartTimeMeet)
        }

        edtEndTimeMeet.setOnClickListener {
            getInsertTime(edtEndTimeMeet)
        }

        bindProgressButton(btnCreateMeeting)
        btnCreateMeeting.attachTextChangeAnimator()
        btnCreateMeeting.setOnClickListener {
            btnCreateMeeting.background = null
            btnCreateMeeting.isEnabled = false
            btnCreateMeeting.showProgress {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    progressColor = getColor(R.color.colorPrimary)
                }
            }

            validateInput()
        }
    }

    private fun getDataLocalDB() {
        try {
            database.use {
                val result = select(Account.TABLE_ACCOUNT)
                val account = result.parseList(classParser<Account>())
                idUser = account[0].idUser.toString()
            }
        } catch (e: SQLiteConstraintException) {
            Log.e("ERROR SQL", e.message.toString())
        }
    }

    private fun getInsertTime(timeMeet: EditText) {
        TimePickerDialog(
            this,
            R.style.DialogTheme,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                val mFormatTime = "HH:mm"
                val sdfTime = SimpleDateFormat(mFormatTime, Locale.getDefault())
                timeMeet.setText(sdfTime.format(cal.time))
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun getInsertDate() {
        DatePickerDialog(
            this,
            R.style.DialogTheme,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val mFormatDate = "EEEE, dd MMM yyyy"
                val sdfDate = SimpleDateFormat(mFormatDate, Locale.getDefault())
                edtDateMeet.setText(sdfDate.format(cal.time))

                val mFormatDate2 = "yyyy-MM-dd"
                val sdfDate2 = SimpleDateFormat(mFormatDate2, Locale.getDefault())
                dateMeet = sdfDate2.format(cal.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateInput() {
        when {
            edtTitleMeet.text.isEmpty() -> {
                edtTitleMeet.error = getString(R.string.error_input)
                edtTitleMeet.requestFocus()
                setEnableBtnCreate()
            }
            edtDesMeet.text.isEmpty() -> {
                edtDesMeet.error = getString(R.string.error_input)
                edtDesMeet.requestFocus()
                setEnableBtnCreate()
            }
            edtDateMeet.text.isEmpty() -> {
                edtDateMeet.error = getString(R.string.error_input)
                edtDateMeet.requestFocus()
                setEnableBtnCreate()
            }
            edtStartTimeMeet.text.isEmpty() -> {
                edtStartTimeMeet.error = getString(R.string.error_input)
                edtStartTimeMeet.requestFocus()
                setEnableBtnCreate()
            }
            edtCategoryMeet.text.isEmpty() -> {
                edtCategoryMeet.error = getString(R.string.error_input)
                edtCategoryMeet.requestFocus()
                setEnableBtnCreate()
            }
            edtPlaceMeet.text.isEmpty() -> {
                edtPlaceMeet.error = getString(R.string.error_input)
                edtPlaceMeet.requestFocus()
                setEnableBtnCreate()
            }
            edtAddressMeet.text.isEmpty() -> {
                edtAddressMeet.error = getString(R.string.error_input)
                edtAddressMeet.requestFocus()
                setEnableBtnCreate()
            }
            else -> {
                uploadMeet()
            }
        }
    }

    private fun uploadMeet() {
        val invitation = (1..STRING_LENGTH)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

        val map: HashMap<String, RequestBody> = HashMap()
        map["nameMeet"] = UtilsForm.createFormString(edtTitleMeet.text.toString())
        map["des"] = UtilsForm.createFormString(edtDesMeet.text.toString())
        map["date"] = UtilsForm.createFormString(dateMeet)
        map["timeStart"] = UtilsForm.createFormString(edtStartTimeMeet.text.toString())
        map["timeEnd"] = UtilsForm.createFormString(edtEndTimeMeet.text.toString())
        map["placeName"] = UtilsForm.createFormString(edtPlaceMeet.text.toString())
        map["address"] = UtilsForm.createFormString(edtAddressMeet.text.toString())
        map["invitation"] = UtilsForm.createFormString(invitation)
        map["category"] = UtilsForm.createFormString(edtCategoryMeet.text.toString())
        map["idUser"] = UtilsForm.createFormString(idUser)

        image?.let {
            ApiClient.getClientHub().insertMeetHub(it, map)
                .enqueue(object : Callback<InsertHubResponse> {
                    override fun onFailure(call: Call<InsertHubResponse>, t: Throwable) {
                        Snackbar.make(
                            layoutCreateMeet,
                            getString(R.string.insert_meet_error),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    override fun onResponse(
                        call: Call<InsertHubResponse>,
                        response: Response<InsertHubResponse>
                    ) {
                        val message = response.body()?.message
                        if (response.isSuccessful){
                            if (message == getString(R.string.success) || message == getString(R.string.available)){
                                onBackPressed()
                                finish()
                            }else{
                                setEnableBtnCreate()
                                message?.let { it1 ->
                                    Snackbar.make(
                                        layoutCreateMeet,
                                        it1,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }

                })
        }


    }

    private fun setEnableBtnCreate() {
        btnCreateMeeting.background = getDrawable(R.drawable.ic_check)
        btnCreateMeeting.isEnabled = true
        btnCreateMeeting.hideProgress()
    }

    private fun getInsertImage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val intent = Intent(this, ImagePickActivity::class.java)
            intent.putExtra(Constant.MAX_NUMBER, 1)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.permission_image), 991,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            getInsertImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imgAddFotoMeeting.visibility = View.GONE
            imgPhotoMeeting.visibility = View.VISIBLE

            val pickedImage =
                data.getParcelableArrayListExtra<ImageFile>(Constant.RESULT_PICK_IMAGE)[0]?.path

            val requestBody = File(pickedImage).asRequestBody("multipart".toMediaTypeOrNull())
            image = MultipartBody.Part.createFormData("image", File(pickedImage).name, requestBody)

            Glide.with(this).load(pickedImage).into(imgPhotoMeeting)
        }
    }

    companion object {
        const val STRING_LENGTH = 40
    }
}
