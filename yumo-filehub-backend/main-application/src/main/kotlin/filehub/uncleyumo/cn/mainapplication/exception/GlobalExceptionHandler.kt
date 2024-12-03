package filehub.uncleyumo.cn.mainapplication.exception

import cn.uncleyumo.utils.ColorPrinter
import cn.uncleyumo.utils.LogPrinter
import filehub.uncleyumo.cn.mainapplication.entity.result.ResultInfo
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * @author uncle_yumo
 * @fileName GlobalExceptionHandler
 * @createDate 2024/12/3 December
 * @school 无锡学院
 * @studentID 22344131
 * @description The global exception handler for the project
 */

@RestControllerAdvice  // define a global exception handler for the project
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)  // handle all exceptions
    fun handleException(e: Exception): ResultInfo {

        LogPrinter.error("\nGlobalExceptionHandler\t${e.message ?: "Unknown exception occurred"}")
        LogPrinter.error("Exception class\t${e.javaClass.name}\n")
        return ResultInfo.exception(message = e.message?: "Unknown exception occurred")
    }
}