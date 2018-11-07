package com.bennyv17.river.template

interface Template {
    fun getTemplateTitles(): ArrayList<String>
    fun getTemplate(): Array<CharSequence>
}