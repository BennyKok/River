package com.bennyv17.river.highlighter.syntax

import java.util.regex.Pattern

object RiveScriptSyntax : Syntax {

    override fun getName(): String {
        return "RiveScript"
    }

    override fun getPatterns(): LinkedHashMap<String, Pattern> {
        return linkedMapOf(
//                "constants_numeric" to Pattern.compile("(\\d+\\.\\d+)|(\\d+)"),
//                "constants1" to Pattern.compile("([A-Za-z0-9_]*)"),
//                "constants2" to Pattern.compile("(')(.*)(')"),

                "tags" to Pattern.compile("([<>])"),
                "tags" to Pattern.compile("([<{])(/?[A-Za-z0-9_]+)(.*?)([>}])", Pattern.DOTALL),

                "defines" to Pattern.compile("^(\\s*!\\s(?:version|global|var|sub|person|array|local))", Pattern.MULTILINE),

                "labels0" to Pattern.compile("^(\\s*[<>]\\s+begin)", Pattern.MULTILINE),
                "labels1" to Pattern.compile("^(\\s*[<>]\\s+topic\\s*)([A-Za-z0-9\\-_ ]*)", Pattern.MULTILINE),
                "labels2" to Pattern.compile("^(\\s*[<>]\\s+object\\s+)(A-Za-z0-9-_]+)?(\\s+[A-Za-z0-9\\-_]+)?", Pattern.MULTILINE),

                "alternates" to Pattern.compile("(\\()([A-Za-z0-9|\\s\\-]*)(\\))"),

                "optionals" to Pattern.compile("(\\s\\[)([A-Za-z0-9\\s\\-]*)(])"),

                "triggers" to Pattern.compile("^(\\s*[+%@]\\s)", Pattern.MULTILINE),

                "replies" to Pattern.compile("^(\\s*[\\-^@]\\s)", Pattern.MULTILINE),

                "conditions" to Pattern.compile("^(\\s*\\*\\s)(.+)(\\s+(?:eq|ne|==|<>|<=|>=|<|>)\\s+)(.+)(=>)", Pattern.MULTILINE),

                "characters" to Pattern.compile("([=<>*{}#_@()])"),

                "comments" to Pattern.compile("//.*$", Pattern.MULTILINE)
        )
    }
}