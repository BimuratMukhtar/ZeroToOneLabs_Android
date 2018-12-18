package com.zerotoonelabs.extensions

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

// get md5 string
fun String.md5() = encrypt(this, "MD5")

//get sha1 string
fun String.sha1() = encrypt(this, "SHA-1")


//check if String is Phone Number
fun String.isPhone(): Boolean {
    val p = "^1([34578])\\d{9}\$".toRegex()
    return matches(p)
}

//check if String is Email
fun String.isEmail(): Boolean {
    val p = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)\$".toRegex()
    return matches(p)
}


//check if String is Number
fun String.isNumeric(): Boolean {
    val p = "^[0-9]+$".toRegex()
    return matches(p)
}

//check String equalsIgnoreCase
fun String.equalsIgnoreCase(other: String) = this.toLowerCase().contentEquals(other.toLowerCase())


//get encrypted string.
private fun encrypt(string: String?, type: String): String {
    if (string.isNullOrEmpty()) {
        return ""
    }
    val md5: MessageDigest
    return try {
        md5 = MessageDigest.getInstance(type)
        val bytes = md5.digest(string!!.toByteArray())
        bytes2Hex(bytes)
    } catch (e: NoSuchAlgorithmException) {
        ""
    }
}

//convert byteArray to String
private fun bytes2Hex(bts: ByteArray): String {
    var des = ""
    var tmp: String
    for (i in bts.indices) {
        tmp = Integer.toHexString(bts[i].toInt() and 0xFF)
        if (tmp.length == 1) {
            des += "0"
        }
        des += tmp
    }
    return des
}