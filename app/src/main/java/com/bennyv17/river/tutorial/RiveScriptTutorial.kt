package com.bennyv17.river.tutorial

import com.bennyv17.river.R
import com.bennyv17.river.item.SimpleSubTutorialItem
import com.bennyv17.river.item.SimpleTutorialItem
import com.bennyv17.river.item.TutorialItemActionCallback

object RiveScriptTutorial : Tutorial {

    override fun getTutorialItems(callback: TutorialItemActionCallback): List<SimpleTutorialItem> {
        val ls = System.getProperty("line.separator")
        return arrayListOf(

                SimpleTutorialItem("",R.drawable.ic_section_1, false).withSubItems(arrayListOf(
                        SimpleSubTutorialItem("RiveScript", "RiveScript is an extreme lightweight text-based scripting language which is used for chatbot development developed by Noah Petherbridge from aichaos and maintained as an open source project on GitHub.$ls$ls"
                                + "The core feature of RiveScript is the pattern matching, which makes it very easy to program a human-like chatbot without programming knowledge. With RiveScript interpreter, you can bind RiveScript functionality to most popular programming language, which enable you to integrate a chatbot almost everywhere.")
                        , SimpleSubTutorialItem("River", "River itself is a third-party IDE for the RiveScript, providing a syntax highlighted editor, and bundled with the Java version of the RiveScript interpreter, and let you test out your script instantly. Also with interactive tutorials, and templates.")
                )) as SimpleTutorialItem

                ,

                SimpleTutorialItem("",R.drawable.ic_section_2, false).withSubItems(arrayListOf(
                        SimpleSubTutorialItem(R.string.rs_tutorial_sub_title_triggers,
                                R.string.rs_tutorial_sub_content_triggers)
                        ,
                        SimpleSubTutorialItem(R.string.rs_tutorial_sub_title_replies,
                                R.string.rs_tutorial_sub_content_replies,
                                "+ hello bot$ls- hello human$ls- hello user",
                                callback)
                        ,
                        SimpleSubTutorialItem(
                                "Triggers : catch all",
                                "Use a \"*\", like in example to catch everything that doesn\'t match other triggers.",
                                "+ hi$ls" +
                                        "- I am a bot$ls$ls" +
                                        "+ *$ls" +
                                        "- Who are you",
                                callback)
                        ,
                        SimpleSubTutorialItem(
                                R.string.rs_tutorial_sub_title_triggers_note,
                                R.string.rs_tutorial_sub_content_triggers_note)
                        ,
                        SimpleSubTutorialItem("Weight",
                                "The {weight} tag can specific which random replies will be chosen more frequently. Replies that don't include a weight tag will automatically have a weight of 1.$ls$ls" +
                                        "Further explanation:$ls" +
                                        "In the following example, the probability of each reply being chosen is the reply's weight divided by the sum of all the weights combined (in this example, 20 + 25 + 1 = 46, so each reply has its weight out of 46 chance of being chosen",
                                "+ greetings$ls" +
                                        "- Hi there!{weight=20}$ls" +
                                        "- Hello!{weight=25}$ls" +
                                        "- Yo yo check this out!", callback)
                )) as SimpleTutorialItem

                ,

                SimpleTutorialItem("Line Breaking", false).withSubItems(arrayListOf(
                        SimpleSubTutorialItem("Very Long reply",
                                "Line Breaking is denoted by a \"^\", which let you extend your previous line to a new line in the editor. You can call this \"Continuation\"",
                                "+ show me the long text$ls" +
                                        "- This very long,$ls" +
                                        "^ very long",
                                callback)
                        ,
                        SimpleSubTutorialItem("Add a space",
                                "To add space between the two lines, use \"\\s\"",
                                "+ show me the long text$ls" +
                                        "- This very long,\\s$ls" +
                                        "^ very long",
                                callback)
                        ,
                        SimpleSubTutorialItem("Split the two line",
                                "To split the two lines into a new line, use \"\\n\"",
                                "+ show me the long text$ls" +
                                        "- This very long line.\\n$ls" +
                                        "^ This is a new line.",
                                callback)
                        ,
                        SimpleSubTutorialItem("Advance usage with using \"\\s\" & \"\\n\"",
                                "See the example, it is well commented.",
                                "// Tell the parser to join continuation lines with line breaks$ls" +
                                        "! local concat = newline$ls" +
                                        ls +
                                        "// Now we don't need to explicitly write the \\n characters every time!$ls" +
                                        "+ example1$ls" +
                                        "- Hello,$ls" +
                                        "^ I am fine.$ls" +
                                        "^ Thank you." +
                                        ls +
                                        "// Now change the concat mode to spaces$ls" +
                                        "! local concat = space$ls" +
                                        ls +
                                        "// Here we don't have to use \\s like in the earlier example.$ls" +
                                        "+ example2$ls" +
                                        "- Hello,$ls" +
                                        "^ I am fine.$ls" +
                                        "^ Thank you." +
                                        ls +
                                        "// Go back to the default concatenation mode (which doesn't insert ANY$ls" +
                                        "// character when joining lines)$ls" +
                                        "! local concat = none",
                                callback)
                )) as SimpleTutorialItem

                ,
                SimpleTutorialItem("User Input", true).withSubItems(arrayListOf(
                        SimpleSubTutorialItem("Reply more naturally",
                                "Use a \"*\"(wildcard) in the triggers to capture variable inputs from user and you can include a \"<star>\" tag in the reply to combine the variable input with the rest of the reply to make the reply more contextually relevant.",
                                "+ i am *$ls" +
                                        "- Hello <star>",
                                callback)
                        ,
                        SimpleSubTutorialItem("Reply more naturally 2",
                                "You can use two wildcard, like in the example, to deal with more complex response.",
                                "+ * called me *$ls" +
                                        "- Did <star1> just called you <star2>?",
                                callback)
                        ,
                        SimpleSubTutorialItem("Reply more naturally 3",
                                "You can use wildcard, without a <star> tag in the replies, to act as a generic response.",
                                "+ i feel *$ls" +
                                        "- I feel it too.$ls" +
                                        "- Is it called emotion?$ls" +
                                        "- Sorry, I don\'t understand feelings",
                                callback)
                        ,
                        SimpleSubTutorialItem("Specialized Wildcards usage 1",
                                "There are all together 3 type of specialized wildcards in RiveScript, \"*\"(for everything) \"_\"(only words, no space) \"#\"(only number).",
                                "+ i am # year-old$ls" +
                                        "- Cool you are just <star> year-old.$ls$ls" +
                                        "+ My name is _$ls" +
                                        "- Hello <star>.",
                                callback)
                        ,
                        SimpleSubTutorialItem("Specialized Wildcards usage 2",
                                "A special way to use specialized wildcards -  restrict specific type of value, the bot will seems like even smarter.",
                                "+ i am # years old$ls" +
                                        "- A lot of people are <star> years old.$ls$ls" +
                                        "+ i am _ years old$ls" +
                                        "- Tell me that again but with a number this time.$ls$ls" +
                                        "+ i am * years old$ls" +
                                        "- Can you use a number instead?",
                                callback)

                )) as SimpleTutorialItem

                ,

                SimpleTutorialItem("Substitutions", true).withSubItems(arrayListOf(
                        SimpleSubTutorialItem("Substitutions",
                                "Substitutions enable you to replace words in the reply with substitutions before the RiveScript begin to match the pattern with the triggers.And Of course, you can have many substitutions.",
                                "! sub i'm  = i am$ls$ls" +
                                        "+ i am *$ls" +
                                        "- You are <star>",
                                callback)


                )) as SimpleTutorialItem

                ,

                SimpleTutorialItem("Alternatives", true).withSubItems(arrayListOf(
                        SimpleSubTutorialItem("Deal with varying reply",
                                "With alternatives you can narrow the range of the possible words to give a more precise reply. Alternative can be captured with a <star> tag.",
                                "+ what is your (home|office|cell) number$ls" +
                                        "- You can reach me at: 1 (800) 555-1234.$ls$ls" +
                                        "+ i am (really|very|super) tired$ls" +
                                        "- I'm sorry to hear that you are <star> tired.$ls$ls" +
                                        "+ i (like|love) the color *$ls" +
                                        "- What a coincidence! I <star1> that color too!$ls" +
                                        "- I also have a soft spot for the color <star2>!$ls" +
                                        "- Really? I <star1> the color <star2> too!$ls" +
                                        "- Oh I <star1> <star2> too!",
                                callback)


                )) as SimpleTutorialItem

                ,

                SimpleTutorialItem("Optionals", true).withSubItems(arrayListOf(
                        SimpleSubTutorialItem("Ignoring specific words",
                                "Use a \"[\" and \"]\" to wrap the words that can be optional, with or without the words, it will still be matched to the replies.",
                                "+ how [are] you$ls" +
                                        "- I'm great, you?",
                                callback)

                        ,

                        SimpleSubTutorialItem("Ignore everything",
                                "Use a \"[\" and \"]\" to wrap the *",
                                "+ [*] the bot [*]$ls" +
                                        "- What's wrong with the bot?",
                                callback)

                )) as SimpleTutorialItem

                ,

                SimpleTutorialItem("Redirection", true).withSubItems(arrayListOf(
                        SimpleSubTutorialItem("Redirecting replies",
                                "This is quite straight forward.",
                                "+ hello$ls" +
                                        "- Hi there!$ls" +
                                        "- Hey!$ls" +
                                        "- Howdy!$ls$ls" +
                                        "+ hey$ls" +
                                        "@ hello$ls$ls" +
                                        "+ hi$ls" +
                                        "@ hello",
                                callback)

                        ,

                        SimpleSubTutorialItem("Advance redirection",
                                "The example is well commented",
                                "//If we say \"Are you a bot or something?\"$ls" +
                                        "//The expected result is \"Or something. How did you know I'm a machine?\"$ls$ls" +
                                        "//As we have a higher weight here, this will be matched first\$ls" +
                                        "+ * or something{weight=100}$ls" +
                                        "//After replying \"Or something.\", we redirect the content of * to rematch the pattern$ls" +
                                        "- Or something. {@ <star>}$ls$ls" +
                                        "//So finally \"are you a bot\" will be matched here\n" +
                                        "+ are you a bot$ls" +
                                        "- How did you know I'm a machine?$ls$ls" +
                                        "//Thus the result is the combination of \"Or something.\" and \"How did you know I'm a machine?\"",
                                callback)

                )) as SimpleTutorialItem

                ,

                SimpleTutorialItem("Variable", true).withSubItems(arrayListOf(
                        SimpleSubTutorialItem("Define variable and usage",
                                "We usually define the variable at the beginner of the script",
                                "! var age = undefined$ls$ls" +
                                        "+ my age is *$ls" +
                                        "- <set age=<star>>Ok I got it.$ls$ls" +
                                        "+ tell me my age$ls" +
                                        "- You are <get age> year-old. Am I correct?",
                                callback)

                        ,

                        SimpleSubTutorialItem("Type?",
                                "RiveScript will check it automatically if the variable is text or number, so no need to worry it right now.")

                )) as SimpleTutorialItem

                ,

                SimpleTutorialItem("Array", true).withSubItems(arrayListOf(
                        SimpleSubTutorialItem("Array",
                                "You can define an array",
                                "! array colors = red blue green yellow",
                                callback)

                        ,

                        SimpleSubTutorialItem("Longer Array",
                                "You can define an array",
                                "// A lot of colors!$ls" +
                                        "! array colors = red blue green yellow orange cyan fuchsia magenta$ls" +
                                        "^ light red|dark red|light blue|dark blue|light yellow|dark yellow$ls" +
                                        "^ light orange|dark orange|light cyan|dark cyan|light fuchsia",
                                callback)

                        ,

                        SimpleSubTutorialItem("Arrays in Triggers",
                                "How you can use array.",
                                "// Without parenthesis, the array doesn't go into a <star> tag.$ls" +
                                        "+ i am wearing a (@colors) shirt$ls" +
                                        "- Do you really like <star>?",
                                callback)

                        ,

                        SimpleSubTutorialItem("Arrays in Optional",
                                "Ignore specific words in array.",
                                "+ i just bought a [@colors] *$ls" +
                                        "- Is that your first <star>?",
                                callback)

                )) as SimpleTutorialItem

                ,


                SimpleTutorialItem("Conditionals", true).withSubItems(arrayListOf(

                        SimpleSubTutorialItem("Using donditionals",
                                "In this example we will check if the name was undefined.",
                                "! var name = undefined$ls" +
                                        "+ what is my name$ls" +
                                        "* <get name> == undefined => You never told me your name.$ls" +
                                        "- Your name is <get name>, silly!$ls" +
                                        "- Aren't you <get name>?",
                                callback)

                        ,

                        SimpleSubTutorialItem("Complex usage",
                                "This is a more comple usage.",
                                "+ what is my name$ls" +
                                        "* <get name> == undefined => You never told me your name.$ls" +
                                        "- Your name is <get name>, silly!$ls" +
                                        "- Aren't you <get name>?",
                                callback)

                )) as SimpleTutorialItem

//                ,
//
//                SimpleTutorialItem("Tags", true).withSubItems(arrayListOf(
//
//                        SimpleSubTutorialItem("",
//                                "",
//                                "",
//                                callback)
//
//                )) as SimpleTutorialItem

        )
    }

}