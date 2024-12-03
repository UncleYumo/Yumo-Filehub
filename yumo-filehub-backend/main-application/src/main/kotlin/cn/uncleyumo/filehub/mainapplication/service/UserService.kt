package cn.uncleyumo.filehub.mainapplication.service

import cn.uncleyumo.filehub.mainapplication.entity.pojo.UserDTO
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
    fun verify(accessKey: String): String

    fun addUser(user: UserDTO)
}