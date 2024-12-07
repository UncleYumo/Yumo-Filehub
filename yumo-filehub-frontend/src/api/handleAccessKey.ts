import request from './request'
import {ref} from "vue";

export const verifyAccessKeyService = (accessKey: string) => {
    let verifyData = ref({
        accessKey: accessKey
    })
    return request.post('/user/verify', verifyData.value)
}