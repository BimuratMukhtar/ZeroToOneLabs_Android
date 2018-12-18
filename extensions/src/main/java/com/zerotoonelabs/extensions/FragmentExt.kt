package com.zerotoonelabs.extensions

import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.widget.Toast

//display toast text for Fragment.
fun Fragment?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let { activity.toast(text, duration) }

//display toast text for Fragment with String resource
fun Fragment?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_LONG) = this?.let { activity.toast(textId, duration) }

