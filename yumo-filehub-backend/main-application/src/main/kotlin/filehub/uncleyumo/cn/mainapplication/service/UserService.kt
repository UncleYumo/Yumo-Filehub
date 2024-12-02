package filehub.uncleyumo.cn.mainapplication.service

import org.springframework.stereotype.Service

/**
 *@author uncle_yumo
 *@fileName UserService
 *@proName yumo-filehub-backend
 *@school Wuxi_University
 *@stuNumber 22344131
 *@createTime 2024/12/2
 *@updateTime 2024/12/2
 *@description
 **/

@Service
interface UserService {
    fun verify(accessKey: String): String {

        return TODO("Provide the return value")
    }
}