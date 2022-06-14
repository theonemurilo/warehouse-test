package com.murilo.test.warehouse.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
open class BaseException(message: String) : Exception(message)