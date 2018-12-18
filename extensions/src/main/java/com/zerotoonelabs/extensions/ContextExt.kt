package com.zerotoonelabs.extensions

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.support.annotation.*
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

//add Color
fun Context.getColorCompat(color: Int) = ContextCompat.getColor(this, color)


//find a device width in pixels
inline val Context.displayWidth: Int
    get() = resources.displayMetrics.widthPixels


//find a device height in pixels
inline val Context.displayHeight: Int
    get() = resources.displayMetrics.heightPixels


// get LayoutInflater
inline val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)


// get a new Intent for an Activity class
inline fun <reified T : Any> Context.intent() = Intent(this, T::class.java)


//to startActivity for Context.
inline fun <reified T : Activity> Context?.startActivity() = this?.startActivity(Intent(this, T::class.java))


//to start Service for Context.
inline fun <reified T : Service> Context?.startService() = this?.startService(Intent(this, T::class.java))


//to startActivity with Animation for Context.
inline fun <reified T : Activity> Context.startActivityWithAnimation(enterResId: Int = 0, exitResId: Int = 0) {
    val intent = Intent(this, T::class.java)
    val bundle = ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId).toBundle()
    ContextCompat.startActivity(this, intent, bundle)
}

//show toast for Context.
fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let { Toast.makeText(it, text, duration).show() }

fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_LONG) = this?.let { Toast.makeText(it, textId, duration).show() }

//get Integer resource for Context.
fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)


//get Boolean resource for Context
fun Context.getBoolean(@BoolRes id: Int) = resources.getBoolean(id)

//get Color for resource for Context.
fun Context.getColor(@ColorRes id: Int) = ContextCompat.getColor(this, id)

//get drawable for resource for Context
fun Context.getDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

//inflateLayout
fun Context.inflateLayout(@LayoutRes layoutId: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): View
= LayoutInflater.from(this).inflate(layoutId, parent, attachToRoot)


//get inputManager for Context
inline val Context.inputManager: InputMethodManager?
    get() = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

//get notificationManager for Context
inline val Context.notificationManager: NotificationManager?
    get() = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager


//show notification for Context
inline fun Context.notification(body: NotificationCompat.Builder.() -> Unit): Notification {
    val builder = NotificationCompat.Builder(this)
    builder.body()
    return builder.build()
}


//provide quicker access to the LayoutInflater from Context
fun Context.getLayoutInflater() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


//to dail telephone number for Context
fun Context.dial(tel: String?) = startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel)))
