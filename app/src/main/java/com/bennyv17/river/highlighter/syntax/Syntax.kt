package com.bennyv17.river.highlighter.syntax

import java.util.regex.Pattern

/**
 * Created by BennyKok on 10/17/2017.
 */
interface Syntax {
    fun getName(): String
    fun getPatterns(): LinkedHashMap<String, Pattern>
}