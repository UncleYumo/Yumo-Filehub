const testURL = 'https://uncleyumo.cn/big-event/user/test'
let file = null;

document.getElementById("btn-github")
    .addEventListener('click', () => {
        window.open("https://github.com/UncleYumo/Yumo-Filehub")
    })

document.getElementById("copy-link-div").addEventListener('click',  async () => {
    let fileURL = document.getElementById("file-url-span").textContent
    await navigator.clipboard.writeText(fileURL)
    alert('Link copied to clipboard! | 链接已复制到剪贴板！')
})

document.getElementById("file-upload-input")
    .addEventListener('change', (event) => {
        file = event.target.files[0]
        if (file) {
            document.getElementById('selected-file-name').textContent = 'Selected file: ' + file.name;
        } else {
            document.getElementById('selected-file-name').textContent = ''
        }
    })

document.getElementById("btn-submit-file")
    .addEventListener('click',  () => {
        if (!file) {
            alert('Please select a file to upload! | 请选择要上传的文件！')
            return
        }

        let accessKey = document
            .getElementById('input-access-key')
            .value.trim()  // remove leading/trailing spaces

        if (accessKey === '' || accessKey.length === 0) {
            alert('Please enter your access key! | 请输入您的访问密钥！')
            return
        }

        let formData = new FormData()
        formData.append('file', file, file.name)
        // formData.append('accessKey', accessKey)

        let xhr = new XMLHttpRequest()
        xhr.open('POST', '/file/upload', true)
        // xhr.setRequestHeader('Content-Type', 'application/multipart/form-data')
        xhr.setRequestHeader('Access-Key', accessKey)
        xhr.onreadystatechange = async () => {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    if (JSON.parse(xhr.responseText).code === 200) {
                        let fileURL = JSON.parse(xhr.responseText).data
                        document.getElementById("file-url-span").textContent = fileURL
                        alert('File uploaded successfully! | 文件上传成功！')

                    } else if (JSON.parse(xhr.responseText).code === 401) {
                        alert('Invalid access key! | 无效的访问密钥！')
                    } else if (JSON.parse(xhr.responseText).code === 500) {
                        alert('Server error, please contact the administrator! | 服务器错误，请联系管理员！')
                    }
                } else {
                    alert('System error, please try again later! | 系统错误，请稍后再试！')
                }
            }
        }
        xhr.send(formData)
    })
