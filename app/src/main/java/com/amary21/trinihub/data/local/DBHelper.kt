package com.amary21.trinihub.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DBHelper(context: Context) : ManagedSQLiteOpenHelper(context, "Trini.db", null, 1) {

    companion object{
        private var instance : DBHelper? = null

        @Synchronized
        fun getInstance(context: Context) : DBHelper{
            if (instance == null){
                instance = DBHelper(context.applicationContext)
            }

            return instance as DBHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(Account.TABLE_ACCOUNT, true,
            Account.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            Account.ID_USER to TEXT + UNIQUE,
            Account.TOKEN to TEXT,
            Account.FIRST_NAME to TEXT,
            Account.LAST_NAME to TEXT,
            Account.DISPLAY_NAME to TEXT,
            Account.JOB_TITLE to TEXT,
            Account.WORK_EMAIL to TEXT,
            Account.HOME_EMAIL to TEXT,
            Account.PHONE to TEXT,
            Account.LANGUAGE to TEXT,
            Account.LOCATION to TEXT,
            Account.LOGIN_EMAIL to TEXT,
            Account.PASSWORD to TEXT,
            Account.NICKNAME to TEXT,
            Account.COMPANY to TEXT)

        db.createTable(GuestList.TABLE_ACCOUNT, true,
            GuestList.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            GuestList.ID_MEET to TEXT,
            GuestList.ID_USER to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(Account.TABLE_ACCOUNT, true)
        db.dropTable(GuestList.TABLE_ACCOUNT, true)
    }
}

val Context.database : DBHelper
    get() = DBHelper.getInstance(applicationContext)