package com.zerotoonelabs.extensions

import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView

//set underLine for TextView
fun TextView.underLine() {
    paint.flags = paint.flags or Paint.UNDERLINE_TEXT_FLAG
    paint.isAntiAlias = true
}

//set deleteLine for TextView
fun TextView.deleteLine() {
    paint.flags = paint.flags or Paint.STRIKE_THRU_TEXT_FLAG
    paint.isAntiAlias = true
}

//set bold for TextView
fun TextView.bold() {
    paint.isFakeBoldText = true
    paint.isAntiAlias = true
}


//set different color for substring TextView
fun TextView.setColorOfSubstring(substring: String, color: Int) {
    try {
        val spannable = android.text.SpannableString(text)
        val start = text.indexOf(substring)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), start, start + substring.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text = spannable
    } catch (e: Exception) {
        Log.d("ViewExtensions",  "exception in setColorOfSubstring, text=$text, substring=$substring", e)
    }
}

//set a drawable to the left of a TextView
fun TextView.setDrawableLeft(drawable: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
}

