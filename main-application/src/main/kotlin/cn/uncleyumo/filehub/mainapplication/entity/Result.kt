package cn.uncleyumo.filehub.mainapplication.entity

/**
 * @author uncle_yumo
 * @fileName Result
 * @createDate 2024/11/28 November
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

// 定义一个枚举类，表示不同的返回状态
private enum class ResultStatus(val code: Int, val message: String) {
    SUCCESS(200, "请求成功"),
    UNAUTHORIZED(401, "未授权"),
    ERROR(500, "请求失败"),
}

// 定义一个数据类，表示统一请求返回对象
data class Result(
    val code: Int,
    val message: String,
    val data: Any? = null
) {
    companion object {
        // 提供常用的预设返回对象
        fun success(data: Any? = null): Result {
            return Result(ResultStatus.SUCCESS.code, ResultStatus.SUCCESS.message, data)
        }

        fun error(message: String = ResultStatus.ERROR.message, data: Any? = null): Result {
            return Result(ResultStatus.ERROR.code, message, data)
        }

        fun unauthorized(message: String = ResultStatus.UNAUTHORIZED.message, data: Any? = null): Result {
            return Result(ResultStatus.UNAUTHORIZED.code, message, data)
        }
    }
}