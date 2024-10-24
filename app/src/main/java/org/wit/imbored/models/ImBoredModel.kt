package org.wit.imbored.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImBoredModel(
    var id: Long = 0,
    var title: String? = "",
    var description: String? = ""
) : Parcelable
