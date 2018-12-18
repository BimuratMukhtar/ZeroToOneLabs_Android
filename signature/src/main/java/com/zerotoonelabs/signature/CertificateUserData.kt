package com.zerotoonelabs.signature

import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException
import java.security.cert.X509Certificate
import java.util.*

const val IIN_NUMBER_LENGTH = 12

class CertificateUserData(private val certificate: X509Certificate) {

    val iin: String
    val bin: String

    init {
        val generalInfo = certificate.subjectDN.toString()
        val iinStart = generalInfo.indexOf("IIN") + 3
        val binStart = generalInfo.indexOf("BIN") + 3
        if (iinStart == 2) {
            throw IncorrectPasswordException()
        }
        if (binStart == 2) {
            throw BinNotFoundException()
        }
        iin = generalInfo.substring(iinStart, iinStart + IIN_NUMBER_LENGTH)
        var binEndIndex = generalInfo.indexOf(',', binStart)
        if (binEndIndex < 0) { // when bin at the last of string without ','
            binEndIndex = generalInfo.length
        }
        bin = generalInfo.substring(binStart, binEndIndex)
        if (iin.isEmpty()) {
            throw IncorrectPasswordException()
        }
        if (bin.isEmpty()) {
            throw BinNotFoundException()
        }
    }

    @Throws(CertificateExpiredException::class, CertificateNotYetValidException::class)
    fun checkValidity(serverDate: Long) {return certificate.checkValidity(Date(serverDate))
    }

    override fun toString(): String {
        return certificate.toString()
    }
}