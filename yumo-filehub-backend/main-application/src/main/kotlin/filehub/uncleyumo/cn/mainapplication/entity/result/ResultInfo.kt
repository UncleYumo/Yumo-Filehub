package filehub.uncleyumo.cn.mainapplication.entity.result

/**
 *@author uncle_yumo
 *@fileName ResultInfo
 *@proName yumo-filehub-backend
 *@school Wuxi_University
 *@stuNumber 22344131
 *@createTime 2024/12/2
 *@updateTime 2024/12/2
 *@description
 **/

class ResultInfo (
    val code: Int,
    val message: String,
    var data: Any? = null
) {

    companion object {
        private const val SUCCESS_CODE = 200
        private const val ERROR_CODE = 400
        private const val EXCEPTION_CODE = 500
        private const val UNAUTHORIZED_CODE = 401

        fun success(data: Any? = null, message: String = "success"): ResultInfo {
            return createResult(SUCCESS_CODE, message, data)
        }

        fun error(data: Any? = null, message: String = "error"): ResultInfo {
            return createResult(ERROR_CODE, message, data)
        }

        fun exception(data: Any? = null, message: String = "exception"): ResultInfo {
            return createResult(EXCEPTION_CODE, message, data)
        }

        fun unauthorized(data: Any? = null, message: String = "unauthorized"): ResultInfo {
            return createResult(UNAUTHORIZED_CODE, message, data)
        }

        private fun createResult(code: Int, message: String?, data: Any?): ResultInfo {
            validateMessage(code, message)
            return ResultInfo(
                code = code,
                message = message ?: "unknown error", // 默认错误信息
                data = data
            )
        }

        private fun validateMessage(code: Int, message: String?) {
            if (code >= 400 && message.isNullOrBlank()) {
                throw IllegalArgumentException("错误码为${code}时，错误信息不能为空或空白。")
            }
        }
    }
}
