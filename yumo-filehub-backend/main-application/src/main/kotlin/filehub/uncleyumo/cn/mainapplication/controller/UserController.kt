package filehub.uncleyumo.cn.mainapplication.controller

import filehub.uncleyumo.cn.mainapplication.entity.result.ResultInfo
import filehub.uncleyumo.cn.mainapplication.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 *@author uncle_yumo
 *@fileName UserController
 *@proName yumo-filehub-backend
 *@school Wuxi_University
 *@stuNumber 22344131
 *@createTime 2024/12/2
 *@updateTime 2024/12/2
 *@description
 **/


@CrossOrigin("*")
@RestController
class UserController {

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/verify")
    fun verify(@RequestBody accessKey: String): ResultInfo {

        val token = userService.verify(accessKey)

        return ResultInfo.success(data = "success")
    }

}