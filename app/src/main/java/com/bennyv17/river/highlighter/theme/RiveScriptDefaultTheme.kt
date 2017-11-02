package com.bennyv17.river.highlighter.theme

import android.graphics.Color

object RiveScriptDefaultTheme : SyntaxTheme {
    override fun getThemeColors(): HashMap<String, Int> {
        return hashMapOf(
                "characters" to Color.parseColor("#CC7832"),

                "comments" to Color.parseColor("#629755"),

//                "constants_numeric" to Color.parseColor("#6897BB"),
//                "constants1" to Color.BLACK,
//                "constants2" to Color.BLACK,

                "tags" to Color.parseColor("#CC7832"),
                "tags" to Color.parseColor("#CC7832"),

                "defines" to Color.parseColor("#CC7832"),

                "labels0" to Color.parseColor("#CC7832"),
                "labels1" to Color.parseColor("#CC7832"),
                "labels2" to Color.parseColor("#CC7832"),

                "alternates" to Color.parseColor("#CC7832"),

                "optionals" to Color.parseColor("#CC7832"),

                "triggers" to Color.parseColor("#B42E2A"),

                "replies" to Color.parseColor("#B42E2A"),

                "conditions" to Color.parseColor("#FFC66D")
        )
    }

    override fun getThemeName(): String {
        return "Default"
    }
}