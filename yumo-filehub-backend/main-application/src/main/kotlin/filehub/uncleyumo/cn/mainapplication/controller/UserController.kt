package filehub.uncleyumo.cn.mainapplication.controller

import cn.uncleyumo.utils.ColorPrinter
import filehub.uncleyumo.cn.mainapplication.entity.pojo.AccessKeyDTO
import filehub.uncleyumo.cn.mainapplication.entity.pojo.UserDTO
import filehub.uncleyumo.cn.mainapplication.entity.result.ResultInfo
import filehub.uncleyumo.cn.mainapplication.service.UserService
import jakarta.validation.Valid
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

    /*
     * 验证accessKey是否有效 若有效返回token（6h有效期）
     */
    @PostMapping("/user/verify")
    fun verify(@RequestBody accessKeyDTO: AccessKeyDTO): ResultInfo {

        val token = userService.verify(accessKeyDTO.accessKey)
        if (token == "-1") return ResultInfo.unauthorized(message = "Invalid accessKey")

        return ResultInfo.success(data = token)
    }

    @PostMapping("/user/addUser")
    fun addUser(@RequestBody user: UserDTO): ResultInfo {
        userService.addUser(user)
        return ResultInfo.success(data = user)
    }

}