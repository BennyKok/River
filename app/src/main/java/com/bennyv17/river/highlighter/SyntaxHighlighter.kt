package com.bennyv17.river.highlighter

import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

object SyntaxHighlighter {

    var highlightLang: Language? = null

    fun highlightSpan(raw: String): HashMap<String, ArrayList<Pair<Int, Int>>> {
        val result = HashMap<String, ArrayList<Pair<Int, Int>>>()

        if (highlightLang == null) throw IllegalStateException("Haven't init the highlight lang.")

        for (entry in highlightLang!!.patterns) {
            val list = ArrayList<Pair<Int, Int>>()
            val matcher = entry.value.matcher(raw)

            while (matcher.find()) {
                list.add(Pair(matcher.start(), matcher.end()))
            }

            result.put(entry.key, list)
        }

        return result
    }

    class Language(val name: String) {
        lateinit var patterns: HashMap<String, Pattern>
    }
}
