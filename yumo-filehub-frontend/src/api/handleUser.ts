import request from './request'
// import {ref} from "vue";

export const userAvailableSpaceService = () => {
    return request.get('/user/availableSpace')
}

export const userValidTimeService = () => {
    return request.get('/user/validTime')
}