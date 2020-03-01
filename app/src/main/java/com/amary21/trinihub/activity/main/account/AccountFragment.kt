package com.amary21.trinihub.activity.main.account


import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.edit_profile.EditProfileActivity
import com.amary21.trinihub.activity.file_sharing.FileActivity
import com.amary21.trinihub.activity.splash.SplashActivity
import com.amary21.trinihub.data.local.Account
import com.amary21.trinihub.data.local.database
import com.amary21.trinihub.data.network.ApiClient
import com.amary21.trinihub.utils.UserPreferences
import kotlinx.android.synthetic.main.fragment_account.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.select
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {

    companion object {
        fun newInstance() = AccountFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        activity?.runOnUiThread {
            loadAccountInfo()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_setting, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mn_app_cloud){
            startActivity(Intent(context, FileActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        activity?.runOnUiThread {
            loadAccountInfo()
        }
    }

    private fun loadAccountInfo() {
        val getPhotoProfile = RainbowSdk.instance().myProfile().connectedUser.photo
        try {
            context?.database?.use {
                val result = select(Account.TABLE_ACCOUNT)
                val account = result.parseList(classParser<Account>())
                itemView(account, getPhotoProfile)
            }
        } catch (e: SQLiteConstraintException) {
            Log.e("ERROR SQL", e.message.toString())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun itemView(
        account: List<Account>,
        photoProfile: Bitmap?
    ) {
        spLoadingAccount.visibility = View.INVISIBLE

        val fullName = account[0].displayName
        val firstName = account[0].firstName
        val lastName = account[0].lastName
        val language = account[0].language
        val location = account[0].location
        val jobTitle = account[0].jobTitle
        val company = account[0].company
        val workEmail = account[0].workEmail
        val homeEmail = account[0].homeEmail
        val phone = account[0].phone

        tvFullName.text = fullName
        tvLanguage.text = language
        tvLocation.text = location
        tvJobTitle.text = jobTitle
        tvPhone.text = phone
        tvWorkEmail.text = workEmail
        tvHomeEmail.text = homeEmail
        tvCompany.text = company

        Log.e("DATA PHOTO", photoProfile.toString())
        if (photoProfile != null) {
            imgDummyProfile.visibility = View.VISIBLE
            tvNameBadge.visibility = View.INVISIBLE
            imgDummyProfile.setImageBitmap(photoProfile)
        } else {
            if (firstName != null && lastName != null) {
                imgDummyProfile.visibility = View.GONE
                tvNameBadge.text = firstName[0].toString() + "" + lastName[0].toString()
            } else {
                imgDummyProfile.visibility = View.VISIBLE
            }
        }


        btnLogout.setOnClickListener {
            logoutAplication(account[0].token.toString(), account[0].idUser.toString())
        }

        btnEdit.setOnClickListener {
            context?.startActivity(Intent(context, EditProfileActivity::class.java))
        }
    }

    private fun logoutAplication(token: String, idUser: String) {

        spLoadingAccount.visibility = View.VISIBLE
        ApiClient.getClient().logout("Bearer $token")
            .enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {

                    Log.e("OnFailure", "onFailure: " + t.localizedMessage)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        spLoadingAccount.visibility = View.INVISIBLE
                        deleteAccountDB(idUser)
                        UserPreferences.logout(activity)
                    }
                }

            })
    }

    private fun deleteAccountDB(idUser: String) {
        try {
            context?.database?.use {
                delete(
                    Account.TABLE_ACCOUNT,
                    "(ID_USER = {idUser})",
                    "idUser" to idUser
                )

                activity?.startActivity(Intent(activity, SplashActivity::class.java))
                activity?.finish()
            }
        } catch (e: SQLiteConstraintException) {
            Log.e("ERROR SQL", e.message.toString())
        }
    }


//    private fun loadUserInfo(userId: String?, token: String?) {
//        ApiClient.getClient()
//            .getUser("Bearer $token", userId)
//            .enqueue(object : Callback<UserResponse>{
//                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
//                    Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
//                    Log.e("ERROR", t.message.toString())
//                }
//
//                @SuppressLint("SetTextI18n")
//                override fun onResponse(
//                    call: Call<UserResponse>,
//                    response: Response<UserResponse>
//                ) {
//                    if (response.isSuccessful){
//                        spLoadingAccount.visibility = View.INVISIBLE
//                        imgDummyProfile.visibility = View.GONE
//                        val user = response.body()?.data
//
//                        tvFullName.text = user?.displayName
//                        tvLanguage.text = user?.language
//                        tvLocation.text = user?.country
//                        tvJobTitle.text = user?.jobTitle
//
//                        tvNameBadge.text = user?.firstName?.get(0).toString() + "" + user?.lastName?.get(0).toString()
//
//
//                        if (user?.phoneNumbers != null){
//                            for (iPhone in user.phoneNumbers){
//                                if (iPhone.type.equals("home") && iPhone.deviceType.equals("mobile")){
//                                    tvPhone.text = iPhone.number
//                                }
//                            }
//                        }
//
//                        if (user?.emails != null){
//                            for (iEmail in user.emails){
//                                if (iEmail.type == "work"){
//                                    tvWorkEmail.text = iEmail.email
//                                }
//
//                                if (iEmail.type == "home"){
//                                    tvHomeEmail.text = iEmail.email
//                                }
//                            }
//                        }
//
//                    }
//                }
//
//            })
//    }


}
