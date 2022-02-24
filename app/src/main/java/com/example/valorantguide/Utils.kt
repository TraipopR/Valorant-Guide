package com.example.valorantguide

import android.widget.ImageView
import com.squareup.picasso.Picasso
import java.util.regex.Pattern

class Utils {
    companion object {
        private val uuidRegexPattern: Pattern =
            Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$")

        fun isValidUUID(str: String?): Boolean = if (str == null) false else uuidRegexPattern.matcher(str).matches()

        fun loadImage(url: String?, imageView: ImageView) = Picasso.get().load(url).into(imageView)
    }
}

