package cn.uncleyumo.filehub.mainapplication.controller

import cn.uncleyumo.filehub.mainapplication.entity.pojo.AccessKeyDTO
import cn.uncleyumo.filehub.mainapplication.entity.pojo.UserDTO
import cn.uncleyumo.filehub.mainapplication.entity.result.ResultInfo
import cn.uncleyumo.filehub.mainapplication.service.UserService
import cn.uncleyumo.filehub.mainapplication.utils.AccessKeyUtil
import cn.uncleyumo.utils.ColorPrinter
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
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
@RequestMapping("/user")
class UserController {

    @Autowired
    lateinit var userService: UserService

    /*
     * 验证accessKey是否有效 若有效返回token（6h有效期）
     */
    @PostMapping("/verify")
    fun verify(@RequestBody accessKeyDTO: AccessKeyDTO): ResultInfo {

        if (!AccessKeyUtil.validateFormat(accessKeyDTO.accessKey)) {
            return ResultInfo.unauthorized(message = "AccessKey's format is invalid")
        }

        val token = userService.verify(accessKeyDTO.accessKey)

        return ResultInfo.success(data = token)
    }

    @PostMapping("/addUser")
    fun addUser(@RequestBody user: UserDTO, @RequestHeader("PRIVATE-KEY") privateKey: String): ResultInfo {
//        ColorPrinter.printlnCyanRed("privateKey: $privateKey")
        if (privateKey != "uncleyumo") return ResultInfo.unauthorized(message = "You are not authorized to add user")
        userService.addUser(user)
        return ResultInfo.success(data = user)
    }

}