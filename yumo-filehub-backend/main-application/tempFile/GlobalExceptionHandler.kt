package filehub.uncleyumo.cn.mainapplication.exception

import filehub.uncleyumo.cn.mainapplication.entity.result.ResultInfo
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * @author uncle_yumo
 * @fileName GlobalExceptionHandler
 * @createDate 2024/12/3 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

import org.slf4j.LoggerFactory

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResultInfo {
        logger.error("NoSuchElementException: ${ex.message}", ex)
        return ResultInfo.exception(message = ex.message ?: "No such element found")
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResultInfo {
        logger.error("Unknown exception: ${ex.message}", ex)
        return ResultInfo.exception(message = ex.message ?: "Unknown exception occurred")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResultInfo {
        val errors = ex.bindingResult.fieldErrors.map { it.defaultMessage ?: "Invalid field" }
        logger.error("Validation exception: ${errors.toString()}", ex)
        return ResultInfo.exception(message = errors.toString())
    }
}
