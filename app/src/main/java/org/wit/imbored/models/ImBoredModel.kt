package org.wit.imbored.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImBoredModel(
    var id: Long = 0,
    var title: String? = "",
    var description: String? = "",
    var category: String? = "",
    var imagePath: String = "",
    var image: Uri = Uri.EMPTY
) : Parcelable
