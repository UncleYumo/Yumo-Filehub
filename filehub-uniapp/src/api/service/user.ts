import { http } from '../http'
import type { AddUserParams, DeleteUserParams, userData } from '../types/type'

export const getUserListService = () => {
	return http<userData[]>({
		method: "GET",
		url: "/user/userList"
	})
}

// 定义 addUserService 函数
export const addUserService = (params: AddUserParams) => {
    return http({
        method: 'POST',
        url: '/user/addUser',
        data: params, // 直接传递 params 对象，不设置默认值
    });
};

export const deleteUserService = (params: DeleteUserParams) => {
	return http({
	    method: 'DELETE',
	    url: '/user/deleteUser',
	    data: params, // 直接传递 params 对象，不设置默认值
	});
}