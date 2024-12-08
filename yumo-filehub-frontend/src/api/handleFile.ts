import request from './request'
import {ref} from "vue";

export const fileUploadService = (formData: FormData) => {
    return request.post('/file/upload', formData, )
}

export const fileListService = () => {
    return request.get('/file/fileList')
}

export const deleteFileService = (deleteFileData: any) => {
    const params = new URLSearchParams()
    for (let key in deleteFileData) {
        params.append(key, deleteFileData[key])
    }
    return request.delete('/file/delete', {params})
}

export const markdownInstructionService = () => {
    return request.get('/file/markdownInstruction')
}