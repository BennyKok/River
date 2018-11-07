package com.bennyv17.river.highlighter

import android.os.AsyncTask

class AsyncHighLightSpan(private var onHighlightReadyListener: OnHighlightReadyListener) : AsyncTask<String, Void, HashMap<String, ArrayList<Pair<Int, Int>>>>() {

    override fun doInBackground(vararg params: String?): HashMap<String, ArrayList<Pair<Int, Int>>> {
        return SyntaxHighlighter.highlightSpan(params[0]!!)
    }

    override fun onPostExecute(result: HashMap<String, ArrayList<Pair<Int, Int>>>?) {
        onHighlightReadyListener.onBeforeHighlight()

        for (entry in result!!.entries) {
            val posList = entry.value
            for (i in 0 until posList.size) {
                onHighlightReadyListener.onHighlight(entry.key, posList[i].first, posList[i].second)
            }
        }
    }

    interface OnHighlightReadyListener {
        fun onHighlight(type: String, start: Int, end: Int)
        fun onBeforeHighlight()
    }

}