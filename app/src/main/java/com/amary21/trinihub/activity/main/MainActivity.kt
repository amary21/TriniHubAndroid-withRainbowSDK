package com.amary21.trinihub.activity.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.main.account.AccountFragment
import com.amary21.trinihub.activity.main.channel.ChannelFragment
import com.amary21.trinihub.activity.main.chat.ChatFragment
import com.amary21.trinihub.activity.main.home.HomeFragment
import com.amary21.trinihub.activity.main.meet.MeetFragment
import com.amary21.trinihub.data.local.Account
import com.amary21.trinihub.data.local.database
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class MainActivity : AppCompatActivity() {

    private val selectedMenuBottom = "com.amary21.trinihub.Menu"
    private lateinit var navView : BottomNavigationView

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId){
                R.id.btm_home ->{
                    val fragment = HomeFragment.newInstance()
                    changeFragment(fragment, HomeFragment::class.java.simpleName)
                    tvTitleMain.text = getString(R.string.home)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.btm_meeting ->{
                    val fragment = MeetFragment.newInstance()
                    changeFragment(fragment, MeetFragment::class.java.simpleName)
                    tvTitleMain.text = getString(R.string.meeting)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.btm_chat ->{
                    val fragment = ChatFragment.newInstance()
                    changeFragment(fragment, ChatFragment::class.java.simpleName)
                    tvTitleMain.text = getString(R.string.chat)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.btm_channel ->{
                    val fragment = ChannelFragment.newInstance()
                    changeFragment(fragment, ChannelFragment::class.java.simpleName)
                    tvTitleMain.text = getString(R.string.channel)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.btm_account ->{
                    val fragment = AccountFragment.newInstance()
                    changeFragment(fragment, AccountFragment::class.java.simpleName)
                    tvTitleMain.text = getString(R.string.account)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    @SuppressLint("PrivateResource")
    private fun changeFragment(fragment: Fragment, simpleName: String) {

        val mFragmentManager = supportFragmentManager
        val fragmentTransaction = mFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.design_bottom_sheet_slide_in,
                R.anim.design_bottom_sheet_slide_out
            )

        val currentFragment = mFragmentManager.primaryNavigationFragment
        if (currentFragment != null){
            fragmentTransaction.hide(currentFragment)
        }

        var fragmentTemp = mFragmentManager.findFragmentByTag(simpleName)
        if (fragmentTemp == null){
            fragmentTemp = fragment
            fragmentTransaction.add(R.id.nav_host_fragment, fragment, simpleName)
        }else{
            fragmentTransaction.show(fragmentTemp)
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        navView = findViewById(R.id.btm_menu)

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (savedInstanceState != null){
            savedInstanceState.getInt(selectedMenuBottom)
        }else{
            val fragment = HomeFragment.newInstance()
            tvTitleMain.text = getString(R.string.home)
            changeFragment(fragment, HomeFragment::class.java.simpleName)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(selectedMenuBottom, navView.selectedItemId)
    }
}
