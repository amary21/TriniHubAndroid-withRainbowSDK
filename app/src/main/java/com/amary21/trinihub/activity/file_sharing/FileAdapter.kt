package com.amary21.trinihub.activity.file_sharing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.fileserver.RainbowFileDescriptor
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import kotlinx.android.synthetic.main.item_file.view.*

class FileAdapter(
    val context: Context,
    var filesList: List<RainbowFileDescriptor>,
    val clickDeleteListener: ((RainbowFileDescriptor) -> Unit)? = null,
    val clickDownloadListener: ((RainbowFileDescriptor) -> Unit)? = null
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_file,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        if (position < filesList.size)
            holder.bind(filesList[position])
    }

    inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(file: RainbowFileDescriptor) {
            val nameFile = file.fileName

            itemView.txtNameFile.text = nameFile

            val filePossessor = RainbowSdk.instance().contacts().getContactFromId(file.ownerId)

            val isSentByMe =
                filePossessor != null && RainbowSdk.instance().myProfile().connectedUser.jid ==
                        filePossessor.jid

            if (isSentByMe) {
                itemView.btnDeleteFile.visibility = View.VISIBLE
            } else {
                itemView.btnDeleteFile.visibility = View.INVISIBLE
            }

            if (RainbowSdk.instance().fileStorage().isDownloaded(file)) {
                itemView.btnDownloadFile.visibility = View.INVISIBLE
            } else {
                itemView.btnDownloadFile.visibility = View.VISIBLE
            }

            itemView.btnDownloadFile.setOnClickListener {
                clickDownloadListener?.invoke(file)
            }
            itemView.btnDeleteFile.setOnClickListener {
                clickDeleteListener?.invoke(file)
            }

        }

//        private fun downloadFile(file: RainbowFileDescriptor) {
//            if (PermissionsHelper.instance()!!.isExternalStorageAllowed(
//                    context,
//                    context as Activity
//                )
//            ) {
//                RainbowSdk.instance().fileStorage()
//                    .downloadFile(file, object : IFileProxy.IDownloadFileListener {
//                        override fun onDownloadFailed(p0: Boolean) {
//
//                        }
//
//                        override fun onDownloadInProgress(p0: GetFileResponse?) {
//
//                        }
//
//                        override fun onDownloadSuccess(p0: GetFileResponse?) {
//                            context.runOnUiThread {
//                                Toast.makeText(
//                                    context,
//                                    "File downloaded in /storage/download/Rainbow/",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//
//                    })
//            }
//        }
    }

}