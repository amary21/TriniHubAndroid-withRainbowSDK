package com.amary21.trinihub.activity.file_sharing

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.http.GetFileResponse
import com.ale.infra.manager.fileserver.IFileProxy
import com.ale.infra.manager.fileserver.RainbowFileDescriptor
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import com.amary21.trinihub.utils.PermissionsHelper
import kotlinx.android.synthetic.main.activity_file.*

class FileActivity : AppCompatActivity() {
    private val files = mutableListOf<RainbowFileDescriptor>()
    private lateinit var fileAdapter: FileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)

        fileAdapter = FileAdapter(this, files, ::deleteButton, ::downloadButton)
        rvFileShared.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@FileActivity)
            adapter = fileAdapter
        }

        swpRefreshFile.setOnRefreshListener { getAllFile() }

        getAllFile()
        btnBackFile.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun downloadButton(file: RainbowFileDescriptor) {
        if (PermissionsHelper.instance()!!.isExternalStorageAllowed(
                this,
                this
            )
        ) {
            RainbowSdk.instance().fileStorage()
                .downloadFile(file, object : IFileProxy.IDownloadFileListener {
                    override fun onDownloadFailed(p0: Boolean) {

                    }

                    override fun onDownloadInProgress(p0: GetFileResponse?) {

                    }

                    override fun onDownloadSuccess(p0: GetFileResponse?) {
                        runOnUiThread {
                            rvFileShared.adapter?.notifyDataSetChanged()
                            Toast.makeText(
                                this@FileActivity,
                                "File downloaded in /storage/download/Rainbow/" + file.fileName,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                })
        }
    }

    private fun deleteButton(file: RainbowFileDescriptor) {
        RainbowSdk.instance().fileStorage()
            .removeFile(file, object : IFileProxy.IDeleteFileListener {
                override fun onDeletionSuccess() {
                    files.remove(file)
                    if (rvFileShared.adapter != null)
                        runOnUiThread { rvFileShared.adapter?.notifyDataSetChanged() }
                }

                override fun onDeletionError() {
                    runOnUiThread {
                        Toast.makeText(this@FileActivity, "Error deleting file", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            })
    }

    private fun getAllFile() {
        rvFileShared.visibility = View.GONE
        spLoadingFile.visibility = View.VISIBLE
        files.clear()
        RainbowSdk.instance().fileStorage().fetchAllFilesSent(object : IFileProxy.IRefreshListener {
            override fun onRefreshSuccess(p0: MutableList<RainbowFileDescriptor>?) {
                p0?.let { files.addAll(it) }

                RainbowSdk.instance().fileStorage()
                    .fetchAllFilesReceived(object : IFileProxy.IRefreshListener {
                        override fun onRefreshSuccess(p1: MutableList<RainbowFileDescriptor>?) {
                            p1?.let { files.addAll(it) }
                            runOnUiThread {
                                if (rvFileShared != null) {
                                    spLoadingFile.visibility = View.GONE
                                    rvFileShared.visibility = View.VISIBLE
                                    swpRefreshFile.isRefreshing = false
                                    rvFileShared.adapter?.notifyDataSetChanged()
                                }
                            }
                        }

                        override fun onRefreshFailed() {
                            Toast.makeText(
                                this@FileActivity,
                                "Error retrieving files received",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    })
            }

            override fun onRefreshFailed() {
                Toast.makeText(
                    this@FileActivity,
                    "Error retrieving files received",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}
