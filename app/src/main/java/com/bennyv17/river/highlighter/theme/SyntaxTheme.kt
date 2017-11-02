package com.bennyv17.river.highlighter.theme

interface SyntaxTheme {
    fun getThemeName(): String
    fun getThemeColors(): HashMap<String, Int>
}