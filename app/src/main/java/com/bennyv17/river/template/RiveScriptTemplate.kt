package com.bennyv17.river.template

object RiveScriptTemplate : Template {

    private val templates: Array<CharSequence> = arrayOf(
            "+ hello bot\n" +
                    "- Hello human!"
            ,
            "// This is the simple trigger again\n" +
                    "+ hello bot    // What the human says\n" +
                    "- Hello human! // How the bot responds"
            ,
            "+ hello bot\n" +
                    "- Hello human!\n" +
                    "- Hello!\n" +
                    "- Hi there!\n" +
                    "- Hey!\n" +
                    "- Hi!"
            ,
            "+ hello bot\n" +
                    "- Hello human!\n" +
                    "- Hello!{weight=5}\n" +
                    "- Hi there!\n" +
                    "- Hey!{weight=5}\n" +
                    "- Hi!{weight=10}"
            ,
            "// Capture the user's name: letters only!\n" +
                    "+ my name is _\n" +
                    "- It's nice to meet you, <star>.\n" +
                    "- <star>, nice to meet you.\n" +
                    "- Pleased to meet you, <star>.\n" +
                    "\n" +
                    "// What if the user says \"my name is 5\"? 5 isn't a real name!\n" +
                    "+ my name is #\n" +
                    "- Nobody has the name of <star>.\n" +
                    "- <star> isn't a real name.\n" +
                    "- Names don't have numbers in them, <star>.\n" +
                    "\n" +
                    "// If they say their name is something that contains both numbers\n" +
                    "// and letters, match this trigger:\n" +
                    "+ my name is *\n" +
                    "- Your name has a number in it?\n" +
                    "\n" +
                    "// See how old the user is\n" +
                    "+ i am # years old\n" +
                    "- A lot of people are <star> years old.\n" +
                    "\n" +
                    "// But don't let them give us their age in words!\n" +
                    "+ i am _ years old\n" +
                    "- Can you say that again using a number?\n" +
                    "\n" +
                    "// Both numbers and letters?\n" +
                    "+ i am * years old\n" +
                    "- You told me numbers and letters? Tell me only numbers.\n" +
                    "\n" +
                    "// Let them tell us where they're from. Numbers and letters are OK!\n" +
                    "+ i am from *\n" +
                    "- What is it like to live in <star>?\n" +
                    "\n" +
                    "// This one has multiple wildcards in it\n" +
                    "+ _ told me to say *\n" +
                    "- So did you say \"<star2>\" because \"<star1>\" told you to?"
            ,
            "+ what is your (home|office|cell) phone number\n" +
                    "- You can call my <star> number at 1 (888) 555-5555.\n" +
                    "\n" +
                    "+ i (can not|cannot) *\n" +
                    "- Have you tried?\n" +
                    "- Why can't you <star2>?\n" +
                    "- Do you really want to <star2>?\n" +
                    "\n" +
                    "+ who (is your master|made you|created you|programmed you)\n" +
                    "- I was developed by a RiveScript coder; you don't need to know his name!\n" +
                    "\n" +
                    "+ (what is your name|who are you|who is this)\n" +
                    "- My name is Aiden, I'm a chatterbot running on RiveScript!\n" +
                    "\n" +
                    "+ (happy|merry) (christmas|xmas|valentines day|thanksgiving)\n" +
                    "- Wow! Is it really <star2> already?"
            ,
            "// Now they don't even need to say the word \"phone\"!\n" +
                    "+ what is your (home|office|cell) [phone] number\n" +
                    "- My <star> number is: 1 (888) 555-5555.\n" +
                    "\n" +
                    "+ i do not have [any] friends\n" +
                    "- Aw. I'll be your friend!\n" +
                    "\n" +
                    "+ am i [a] (boy|guy|male) or [a] (girl|female)\n" +
                    "- I can't tell with any degree of certainty whether you are a <star1> or <star2>."
    )

    private val templatesTitle: ArrayList<CharSequence> = arrayListOf(
            "HelloBot"
            , "Comments"
            , "Random Replies"
            , "weight"
            , "wildcards"
            , "alternations"
            , "options"
    )


    override fun getTemplateTitles(): ArrayList<CharSequence> {
        return templatesTitle
    }

    override fun getTemplate(): Array<CharSequence> {
        return templates
    }

}