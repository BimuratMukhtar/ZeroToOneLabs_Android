package com.zerotoonelabs.signature

import java.lang.RuntimeException

class IncorrectPasswordException(errorMessage: String? = null): RuntimeException(errorMessage)

class BinNotFoundException(errorMessage: String? = null): RuntimeException(errorMessage)

class NoDataTagException(errorMessage: String? = null) : RuntimeException(errorMessage)
