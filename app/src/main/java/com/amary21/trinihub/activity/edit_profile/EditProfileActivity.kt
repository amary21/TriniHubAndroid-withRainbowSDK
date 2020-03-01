package com.amary21.trinihub.activity.edit_profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.proxy.avatar.IAvatarProxy
import com.ale.infra.proxy.users.IUserProxy
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import com.amary21.trinihub.data.local.Account
import com.amary21.trinihub.data.local.database
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vincent.filepicker.Constant
import com.vincent.filepicker.Constant.MAX_NUMBER
import com.vincent.filepicker.Constant.REQUEST_CODE_TAKE_IMAGE
import com.vincent.filepicker.activity.ImagePickActivity
import com.vincent.filepicker.filter.entity.ImageFile
import kotlinx.android.synthetic.main.activity_edit_profile.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EditProfileActivity : AppCompatActivity() {

    private lateinit var btmCreateDialog: BottomSheetDialog
    private lateinit var btmCreateDialogView: View
    private var idUser: String? = null
    private var pickedImage: String? = null

    private val userListener = object : IUserProxy.IUsersListener {
        override fun onSuccess() {
            runOnUiThread {
                loadAccountInfo()
                btmCreateDialog.cancel()
                Log.e("SUKSES", "UPDATE")
            }
        }

        override fun onFailure(p0: RainbowServiceException?) {

        }
    }

    private val avatatarListener = object : IAvatarProxy.IAvatarListener {
        override fun onAvatarFailure(p0: RainbowServiceException?) {
            Log.e("ERROR DATA PHOTO", p0.toString())
        }

        override fun onAvatarSuccess(p0: Bitmap?) {
            runOnUiThread {
                Glide.with(this@EditProfileActivity).load(pickedImage).into(imgViewProfile)
            }
        }

    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        btmCreateDialog = BottomSheetDialog(this)
//        getIntentData(intent)

        loadAccountInfo()

        btnBackEditProfile.setOnClickListener {
            onBackPressed()
            finish()
        }

        btnEditProfilePerson.setOnClickListener { editPerson() }

        btnEditProfilePhone.setOnClickListener { editPhone() }

        btnEditJobPerson.setOnClickListener { editJob() }

        btnAddPhotoEditProfile.setOnClickListener { addEditPhoto() }
    }


    @SuppressLint("SetTextI18n")
    private fun loadAccountInfo() {
        val getPhotoProfile = RainbowSdk.instance().myProfile().connectedUser.photo
        try {
            database.use {
                val result = select(Account.TABLE_ACCOUNT)
                val account = result.parseList(classParser<Account>())

                //id
                idUser = account[0].idUser

                //Person data
                tvFirstNameProfile.text = account[0].firstName
                tvLastNameProfile.text = account[0].lastName
                tvNicknameProfile.text = account[0].nickName

                //Phone Number Data
                tvPhoneProfile.text = account[0].phone

                //Job Data
                tvJobTitleProfile.text = account[0].jobTitle

                val firstName = account[0].firstName
                val lastName = account[0].lastName

                if (getPhotoProfile != null) {
                    imgViewProfile.visibility = View.VISIBLE
                    tvNameProfileEdit.visibility = View.INVISIBLE
                    imgViewProfile.setImageBitmap(getPhotoProfile)
                } else {
                    if (firstName != null && lastName != null) {
                        tvNameProfileEdit.visibility = View.VISIBLE
                        imgViewProfile.visibility = View.INVISIBLE
                        tvNameProfileEdit.text =
                            firstName[0].toString() + "" + lastName[0].toString()
                    } else {
                        tvNameProfileEdit.visibility = View.INVISIBLE
                        imgViewProfile.visibility = View.VISIBLE
                        imgViewProfile.setImageDrawable(getDrawable(R.drawable.ic_bottom_user_br))
                    }
                }
            }
        } catch (e: SQLiteConstraintException) {
            Log.e("ERROR SQL", e.message.toString())
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_TAKE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            pickedImage =
                data.getParcelableArrayListExtra<ImageFile>(Constant.RESULT_PICK_IMAGE)[0]?.path
            if (pickedImage != null) {
                val fileImage = File(pickedImage)
                RainbowSdk.instance().myProfile().updatePhoto(fileImage, avatatarListener)
            }
        }

    }


    private fun addEditPhoto() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val intent = Intent(this, ImagePickActivity::class.java)
            intent.putExtra(MAX_NUMBER, 1)
            startActivityForResult(intent, REQUEST_CODE_TAKE_IMAGE)
        } else {
            EasyPermissions.requestPermissions(
                this, "This application need your permission to access photo gallery.", 991,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            addEditPhoto()
        }

    }

    private fun editJob() {
        btmCreateDialogView = layoutInflater.inflate(R.layout.bottom_job, null)
        btmCreateDialog.setContentView(btmCreateDialogView)
        btmCreateDialog.show()

        //edit email
        val edtJobTitle = btmCreateDialogView.findViewById<EditText>(R.id.edtJobTitleEdit)
        edtJobTitle.setText(tvJobTitleProfile.text)


        val btnCancelJob = btmCreateDialogView.findViewById<Button>(R.id.btnBottomCancelJob)
        btnCancelJob.setOnClickListener {
            btmCreateDialog.cancel()
        }

        val btnSaveJob = btmCreateDialogView.findViewById<Button>(R.id.btnBottomEditJob)
        btnSaveJob.setOnClickListener {
            idUser?.let { idUser -> saveEditJob(idUser, edtJobTitle.text.toString()) }
        }
    }

    private fun editPhone() {
        btmCreateDialogView = layoutInflater.inflate(R.layout.bottom_phone, null)
        btmCreateDialog.setContentView(btmCreateDialogView)
        btmCreateDialog.show()

        //edit phone
        val edtPhone = btmCreateDialogView.findViewById<EditText>(R.id.edtPhoneEdit)
        edtPhone.setText(tvPhoneProfile.text)

        val btnCancelPhone = btmCreateDialogView.findViewById<Button>(R.id.btnBottomCancelPhone)
        btnCancelPhone.setOnClickListener {
            btmCreateDialog.cancel()
        }

        val btnSavePhone = btmCreateDialogView.findViewById<Button>(R.id.btnBottomEditPhone)
        btnSavePhone.setOnClickListener {
            idUser?.let { idUser -> saveEditPhone(idUser, edtPhone.text.toString()) }
        }
    }


    private fun editPerson() {
        btmCreateDialogView = layoutInflater.inflate(R.layout.bottom_person, null)
        btmCreateDialog.setContentView(btmCreateDialogView)
        btmCreateDialog.show()

        //edit person
        val edtFirstName = btmCreateDialogView.findViewById<EditText>(R.id.edtFirstNameEdit)
        val edtLastName = btmCreateDialogView.findViewById<EditText>(R.id.edtLastNameEdit)
        val edtNickName = btmCreateDialogView.findViewById<EditText>(R.id.edtNicknameEdit)

        edtFirstName.setText(tvFirstNameProfile.text)
        edtLastName.setText(tvLastNameProfile.text)
        edtNickName.setText(tvNicknameProfile.text)

        val btnCancelPerson = btmCreateDialogView.findViewById<Button>(R.id.btnBottomCancelPerson)
        btnCancelPerson.setOnClickListener {
            btmCreateDialog.cancel()
        }

        val btnSavePerson = btmCreateDialogView.findViewById<Button>(R.id.btnBottomEditPerson)
        btnSavePerson.setOnClickListener {
            idUser?.let { idUser ->
                saveEditPerson(
                    idUser,
                    edtFirstName.text.toString(),
                    edtLastName.text.toString(),
                    edtNickName.text.toString()
                )
            }
        }

    }

    private fun saveEditPerson(id: String, firstName: String, lastName: String, nickName: String) {
        Log.e("ID UPDATE", id)
        try {
            database.use {
                update(
                    Account.TABLE_ACCOUNT,
                    Account.FIRST_NAME to firstName,
                    Account.LAST_NAME to lastName,
                    Account.DISPLAY_NAME to "$firstName $lastName",
                    Account.NICKNAME to nickName
                )
                    .whereArgs(
                        "${Account.ID_USER} = {id}",
                        "id" to id
                    ).exec()
            }

            RainbowSdk.instance().myProfile().updateFirstName(firstName, userListener)
            RainbowSdk.instance().myProfile().updateLastName(lastName, userListener)
            RainbowSdk.instance().myProfile().updateNickName(nickName, userListener)

        } catch (e: SQLiteConstraintException) {
            Log.e("ERROR SQL", e.message.toString())
        }
    }


    private fun saveEditPhone(id: String, phone: String) {
        Log.e("ID UPDATE", id)
        try {
            database.use {
                update(
                    Account.TABLE_ACCOUNT,
                    Account.PHONE to phone
                )
                    .whereArgs(
                        "${Account.ID_USER} = {id}",
                        "id" to id
                    ).exec()
            }

            RainbowSdk.instance().myProfile().updateOfficePhoneNumber(phone, "IDN", userListener)

        } catch (e: SQLiteConstraintException) {
            Log.e("ERROR SQL", e.message.toString())
        }
    }


    private fun saveEditJob(id: String, jobTitle: String) {
        Log.e("ID UPDATE", id)
        try {
            database.use {
                update(
                    Account.TABLE_ACCOUNT,
                    Account.JOB_TITLE to jobTitle
                )
                    .whereArgs(
                        "${Account.ID_USER} = {id}",
                        "id" to id
                    ).exec()
            }

            RainbowSdk.instance().myProfile().updateJobTitle(jobTitle, userListener)

        } catch (e: SQLiteConstraintException) {
            Log.e("ERROR SQL", e.message.toString())
        }
    }
}
