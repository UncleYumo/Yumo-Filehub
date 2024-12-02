import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { ResultData } from './types';

const service: AxiosInstance = axios.create({
    baseURL: 'http://localhost:49153/api',
    timeout: 5000,
});

// 请求拦截器
service.interceptors.request.use(
    (config: AxiosRequestConfig): AxiosRequestConfig => {
        const token = userStore.token;
        if (token) {
            config.headers!['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        console.error('请求错误:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    (response: AxiosResponse<ResultData<any>>) => response.data,
    (error) => {
        if (error.response) {
            if (error.response.status === 401) {
                // 假设你有路由功能，例如使用vue-router
                router.push('/authorize');
            } else {
                console.error('响应错误:', error.response.status, error.response.data);
            }
        } else {
            console.error('网络错误:', error);
        }
        return Promise.reject(error);
    }
);

export default service;
