// src/api/types/type.ts
export interface userData {
	accessKey : string;
	createTime : string;
	validTime : number;
	availableSpace : number;
}

export interface AddUserParams {
	accessKey : string;
	validTime ?: number; // 可选参数，默认5min, 一个月43200min
	availableSpace ?: number; // 可选参数，默认1G即1048576字节
}

export interface DeleteUserParams {
	accessKey: string
}