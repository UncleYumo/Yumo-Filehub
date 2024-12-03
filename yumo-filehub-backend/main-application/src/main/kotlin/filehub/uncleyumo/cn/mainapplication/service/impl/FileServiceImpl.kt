package filehub.uncleyumo.cn.mainapplication.service.impl

import filehub.uncleyumo.cn.mainapplication.service.FileService
import filehub.uncleyumo.cn.mainapplication.utils.FileManipulationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

/**
 *@author uncle_yumo
 *@fileName FileServiceImpl
 *@proName yumo-filehub-backend
 *@school Wuxi_University
 *@stuNumber 22344131
 *@createTime 2024/12/3
 *@updateTime 2024/12/3
 *@description
 **/

@Service
class FileServiceImpl: FileService {

    @Autowired
    private lateinit var fileManipulationUtil: FileManipulationUtil


    override fun uploadFile(
        file: MultipartFile,
        validTime: Int
    ): String {
        fileManipulationUtil.saveFile(/*TODO: AccessKey*/, file)
    }


}