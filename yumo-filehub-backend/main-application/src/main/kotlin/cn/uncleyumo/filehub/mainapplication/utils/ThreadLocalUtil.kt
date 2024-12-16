package cn.uncleyumo.filehub.mainapplication.utils

/**
 * @author uncle_yumo
 * @fileName ThreadLocalUtil
 * @createDate 2024/12/4 December
 * @school 无锡学院
 * @studentID 22344131
 * @description ThreadLocal工具类
 */

class ThreadLocalUtil {
    companion object {
        private val THREAD_LOCAL = ThreadLocal<Map<String, *>>()

        fun set(obj: Map<String, *>) {
            THREAD_LOCAL.set(obj)
        }

        fun get(): Map<String, *>? {
            return THREAD_LOCAL.get()
        }

        fun remove() {
            THREAD_LOCAL.remove()
        }
    }
}