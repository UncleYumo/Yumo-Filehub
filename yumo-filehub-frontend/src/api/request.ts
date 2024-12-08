import axios from "axios";  // 引入axios库的类型定义
import type {AxiosInstance, AxiosRequestConfig, AxiosResponse} from "axios"
import {useTokenStore} from "@/stores/tokenStore";
import { ElMessage } from 'element-plus'
import router from "@/router";

const VUE_DEV_BASE_URL: string = 'http://localhost:49153'
const VUE_RELEASE_BASE_URL: string = 'http://139.224.195.43/filehub'
const baseURL = VUE_RELEASE_BASE_URL

const instance: AxiosInstance = axios.create({baseURL})

// 请求拦截器
instance.interceptors.request.use(
    (config) => {
        // 添加token
        const tokenStore = useTokenStore()
        if ( tokenStore.token !== '' && tokenStore.token !== null ) {
            // console.log('Add token:', tokenStore.token)
            config.headers.Authorization = `Bearer ${tokenStore.token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
instance.interceptors.response.use(
    async result => {
        let code = result.data.code
        if (code === 200) {  // 成功
            return result.data
        } else if (code === 401) {  // 未登录

            ElMessage({
                showClose: true,
                message: 'Authorization failed, please verify your Access-Key',
                type: 'error',
            })

            // 清空本地token
            const tokenStore = useTokenStore()
            tokenStore.removeToken()
            await router.push('/authorize')

        } else if (code === 400) {
            ElMessage({
                showClose: true,
                message: result.data.message,
                type: 'error',
            })
            return Promise.reject(result.data)
        } else if (code === 500) {
            ElMessage({
                showClose: true,
                message: result.data.message,
                type: 'error',
            })
            return Promise.reject(result.data)
        }
    },
    error => {
        ElMessage({
            showClose: true,
            message: 'Network Error',
            type: 'error',
        })
        return Promise.reject(error)
    }
)

export default instance