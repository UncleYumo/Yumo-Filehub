const baseURL: string = 'http://139.224.195.43/filehub'
// const baseURL: string = 'https://uncleyumo.cn/big-event/'

// 请求拦截器
const httpInterceptor = {
    invoke(options: UniApp.RequestOptions) {
        // 确保 URL 以斜杠开始，避免相对路径的问题
        let url = options.url.startsWith('/') ? options.url : `/${options.url}`;
        options.url = `${baseURL}${url}`; // 拼接 baseURL 和 API 路径
        options.timeout = 10000;
        options.header = {
            ...options.header,  // 合并请求头
            'PRIVATE-KEY': '这里保密哦'
        };
    }
};

uni.addInterceptor('request', httpInterceptor)
// uni.addInterceptor('uploadFile', httpInterceptor)

interface Data<T> {
    code: number
    message: string
    data: T
}

export const http = <T> (options: UniApp.RequestOptions) => {
    return new Promise<Data<T>>((resolve, reject)=>{
        uni.request({
            ...options,  // 合并请求参数
            success(res) {
                if (res.statusCode >= 200 && res.statusCode < 300) {
					if ((res.data as Data<T>).code === 200) {
						resolve(res.data as Data<T>)
					} else {
						uni.showToast({
						    icon: 'none',
						    title: (res.data as Data<T>).message as string || '请求失败，请稍后再试'
						})
						reject(res)
					}
				} else {
					// 其他错误
					uni.showToast({
					    icon: 'none',
					    title: '请求失败，请稍后再试'
					})
					reject(res)
				}
            },
            fail(err) {
                uni.showToast({
                    icon: 'none',
                    title: '网络错误，请稍后再试'
                })
                reject(err)
            }
        })
    })
}
