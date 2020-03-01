package com.amary21.trinihub.activity.channel

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.manager.channel.Channel
import com.ale.infra.proxy.channel.IChannelProxy
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import kotlinx.android.synthetic.main.activity_create_channel.*

class CreateChannelActivity : AppCompatActivity() {

    private val channelListener = object : IChannelProxy.IChannelCreateListener{
        override fun onCreateSuccess(p0: Channel?) {
            runOnUiThread {
                spLoadingChannelCreate.visibility = View.INVISIBLE
                Toast.makeText(this@CreateChannelActivity, "New Channel Created", Toast.LENGTH_SHORT).show()
                onBackPressed()
                finish()
            }
        }

        override fun onCreateFailed(p0: RainbowServiceException?) {
            runOnUiThread {
                spLoadingChannelCreate.visibility = View.INVISIBLE
                Toast.makeText(this@CreateChannelActivity, "New Channel Creation Failed", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_channel)

        edtCategoryChannel.setOnClickListener {
            PopupMenu(this, edtCategoryChannel).apply {
                menuInflater.inflate(R.menu.menu_category_channel, menu)
                window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                setOnMenuItemClickListener {
                    edtCategoryChannel.setText(it.title)
                    true
                }
                show()
            }
        }

        btnCreateChannel.setOnClickListener {
            createChannel()
        }

        btnBackCreateChannel.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun createChannel() {
        spLoadingChannelCreate.visibility = View.VISIBLE

        when{
            edtNameChannel.text.isEmpty()->{
                edtNameChannel.error = "data is still empty"
                spLoadingChannelCreate.visibility = View.INVISIBLE
            }
            edtCategoryChannel.text.isEmpty()->{
                edtCategoryChannel.error = "data is still empty"
                spLoadingChannelCreate.visibility = View.INVISIBLE
            }
            edtDesChannel.text.isEmpty()->{
                edtDesChannel.error ="data is still empty"
                spLoadingChannelCreate.visibility = View.INVISIBLE
            }
            else ->{
                val nameChannel = edtNameChannel.text.toString()
                val description = edtDesChannel.text.toString()
                val category = edtCategoryChannel.text.toString()
                val channelMode = rgChannelMode.checkedRadioButtonId

                if (channelMode == R.id.rbChannelClosed){
                    val autoProvioning = cbAutoprovisioning.isChecked
                    channelClosed(nameChannel, description, category, autoProvioning)
                }else{
                    channelPublic(nameChannel, description, category)
                }
            }
        }
    }

    private fun channelPublic(nameChannel: String, description: String, category: String) {

        RainbowSdk.instance().channels().createPublicChannel(nameChannel,description, category, 100, channelListener)
    }

    private fun channelClosed(
        nameChannel: String,
        description: String,
        category: String,
        autoProvioning: Boolean
    ) {
        RainbowSdk.instance().channels().createClosedChannel(nameChannel,description, category, autoProvioning,100, channelListener)
    }
}
