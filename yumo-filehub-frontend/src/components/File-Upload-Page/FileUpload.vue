<!--Author: Uncle Yumo-->
<template>

  <div class="file-upload-container">
    <div class="file-upload-content">
      <el-upload
          class="upload-component"
          drag
          method="post"
          ref="uploadRef"
          :show-file-list="false"
          :auto-upload="false"
          :limit="1"
          @click="handleUploadClick"
          @change="handFileChange"
      >
        <el-icon class="el-icon--upload">
          <upload-filled color="#fff"/>
        </el-icon>
        <div class="el-upload__text">
          Drop file here or <em>click to upload</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            File size should be less than 200MB <span class="el-icon-upload-filled">{{ formattedFileName }}</span>
          </div>
        </template>
      </el-upload>
    </div>

    <div class="file-upload-button-container">
      <div class="file-upload-button-content">
        <div class="file-upload-button-content-inner">
          <div class="file-upload-button" @click="submitFile">
            <n-icon size="32px" :component="UploadFileFilled"></n-icon>
            <span class="el-icon-upload-filled">UPLOAD</span>
          </div>
        </div>
      </div>
    </div>
  </div>

  <el-dialog v-model="dialogFormVisible" title="File Upload Confirmation" width="400">
    <el-row justify="center" align="middle">
      <el-col :span="6">
        <el-statistic title="Available(KB)" :value="availableSpace"/>
      </el-col>
      <el-col :span="6">
        <el-statistic title="Max Time(M)" :value="userValidTime ? userValidTime : '∞'"/>
      </el-col>
      <el-col :span="6">
        <el-statistic title="File Size(KB)" :value="currentFileSize"/>
      </el-col>
    </el-row>
    <div style="margin-top: 10px;"></div>
    <div class="slider-demo-block">
      <span class="demonstration">File Valid Time</span>
      <el-slider :min="sliderFileValidTimeMinValue" :max="sliderFileValidTimeMaxValue" show-input v-model="fileValidTime"/>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleUpload">
          Confirm
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="dialogFileURLVisible" title="File URL" width="500" center>
    <span style="margin-bottom: 16px; color: #ffffa6">
      {{fileURL}}
    </span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFileURLVisible = false">Cancel</el-button>
        <el-button type="primary" @click="copyFileURL">
          COPY URL
        </el-button>
      </div>
    </template>
  </el-dialog>

</template>

<script setup lang="ts">
import {UploadFilled} from '@element-plus/icons-vue'
import {ref, computed} from 'vue'
import {UploadFileFilled} from '@vicons/material'

import type {UploadInstance, UploadUserFile} from 'element-plus'
import {ElMessage} from "element-plus"
import {ElLoading} from 'element-plus'

import {fileUploadService} from "@/api/handleFile"
import {userAvailableSpaceService, userValidTimeService} from "@/api/handleUser";
import router from "@/router"
import {useTokenStore} from "@/stores/tokenStore"

const uploadRef = ref<UploadInstance>()  // 用于控制上传的引用
const fileName = ref('No Selected File')
const fileObject = ref()
const fileValidTime = ref(1)  // 有效时间，单位为分钟
const dialogFormVisible = ref(false)
const availableSpace = ref(0)
const currentFileSize = ref(0)
const userValidTime = ref(0)
const fileURL = ref('')
const dialogFileURLVisible = ref(false)

const sliderFileValidTimeMinValue = computed(() => {
  if (userValidTime.value === 0) {
    return -1 // 当用户没有有效时间时，最小值设为 -1
  }
  return 0
});

const sliderFileValidTimeMaxValue = computed(() => {
  if (userValidTime.value === 0) {
    return 99999; // 当用户没有有效时间时，最大值设为很大的数
  }

  if (userValidTime.value < 0 && userValidTime.value !== -1) {
    ElMessage({
      message: 'Current Access-Key has no more valid time left',
      type: 'error',
      plain: true,
    });
    const tokenStore = useTokenStore()
    tokenStore.removeToken()
    router.push('/authorize')
  }
  return userValidTime.value; // 返回用户有效时间
});

const isFileSelected = computed(() => {
  return fileName.value !== 'No Selected File'
})

const formattedFileName = computed(() => {
  const maxLength = 36 // 设置最大字符长度
  return fileName.value.length > maxLength
      ? fileName.value.slice(0, maxLength) + '...(omitted)'
      : fileName.value
});

const handFileChange = (file: File) => {
  ElMessage({
    message: 'File Selected',
    type: 'success',
    plain: true,
  })

  if (file.size > 200 * 1024 * 1024) {
    ElMessage({
      message: 'File size should be less than 200MB',
      type: 'warning',
      plain: true,
    })
    return
  }

  uploadRef?.value?.clearFiles()
  fileName.value = file.name
  fileObject.value = file
}

const submitFile = async () => {
  if (!isFileSelected.value) {
    ElMessage({
      message: 'Please select a file to upload',
      type: 'warning',
      plain: true,
    })
    return
  }

  let result = await userAvailableSpaceService()
  availableSpace.value = result.data // KB
  currentFileSize.value = fileObject.value.size / 1024 // KB

  let userValidTimeResult = await userValidTimeService()
  userValidTime.value = userValidTimeResult.data

  dialogFormVisible.value = true
}

const handleUploadClick = () => {
  uploadRef?.value?.clearFiles()
  fileName.value = 'No Selected File'
}

const handleUpload = async () => {
  let formData = new FormData()
  formData.append('file', fileObject.value.raw)
  formData.append('validTime', fileValidTime.value.toString())

  const loadingInstance = ElLoading.service({
    text: 'Uploading file...',
    background: 'rgba(0, 0, 0, 0.7)',
    target: '.file-upload-container',
  })

  dialogFormVisible.value = false

  try {
    let response = await fileUploadService(formData)
    ElMessage({
      message: "File uploaded successfully",
      type: 'success',
      plain: true,
    })
    loadingInstance.close()
    fileURL.value = response.data
    dialogFileURLVisible.value = true
  } catch (error) {
    loadingInstance.close()
  }
}


const copyFileURL = async () => {

  const el = document.createElement('textarea')
  el.value = fileURL.value
  document.body.appendChild(el)
  el.select()
  document.execCommand('copy')
  document.body.removeChild(el)

  ElMessage({
    message: 'File URL copied to clipboard',
    type: 'success',
    plain: true,
  })
}

</script>


<style lang="scss" scoped>

// Phone Screen's Sizes and Below
@media screen and (max-width: 768px) {
  .file-upload-container {
    width: 100%;
    display: flex;
    flex-direction: column;

    .file-upload-content {
      border-radius: 6px;
      background-color: rgba(69, 49, 158, 0.3);
      width: 90%;
      margin: 0 auto;

      .el-upload__text {
        color: #fff;
        font-size: 16px; // Adjusted for smaller screens
        text-align: center; // Center align text for better mobile view
      }

      .el-upload__tip {
        color: rgba(226, 174, 255, 0.8);
        font-size: 12px; // Slightly reduced font size
        text-align: center; // Center align tip
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        padding-bottom: 16px;

        .el-icon-upload-filled {
          margin-left: 4px; // Reduce icon margin
          color: #ffff5e;
        }
      }
    }

    .file-upload-button-container {
      width: 100%;
      margin-top: 16px; // Reduced margin to fit content better on small screens

      .file-upload-button-content {
        width: 100%; // Full width for button container
        padding: 0; // Remove padding to avoid unnecessary space
        justify-content: center;
        //border: rgba(76, 175, 80, 0.5) solid 2px;

        .file-upload-button-content-inner {
          width: 200px; // Adjusted width for smaller screens
          height: 48px; // Slightly reduced height
          border: #ffffff solid 3px; // Reduced border width
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 auto;


          .file-upload-button {
            //border: #d31919 solid 2px;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 90%;
            margin: 0 auto;

            .el-icon-upload-filled {
              color: #9f8bfe;
              font-size: 16px; // Reduced icon size
              margin-left: 4px; // Reduced icon margin
            }

            .el-icon-upload-filled:hover {
              color: #fff09b;
              font-size: 24px; // Increased hover size
              margin-left: 4px; // Consistent with non-hover state
            }
          }
        }
      }
    }
  }
  .el-col {
    text-align: center;
  }
}

// Tablet Screen's Sizes and Below
@media screen and (min-width: 768px) and (max-width: 1024px) {
  .file-upload-container {
    width: 100%;
    display: flex;
    flex-direction: column;

    .file-upload-content {
      border-radius: 8px;
      background-color: rgba(69, 49, 158, 0.3);
      width: 80%; // Slightly increased from 64% to use more available space
      margin: 0 auto 24px; // Increased bottom margin for spacing

      .el-upload__text {
        color: #fff;
        font-size: 24px; // Slightly bigger than phone, but not as large as laptop
        text-align: center; // Keep center alignment
      }

      .el-upload__tip {
        color: rgba(226, 174, 255, 0.8);
        font-size: 16px; // Maintain original size
        margin: 8px 16px 0; // Keep same margins
        text-align: center; // Center align tip

        .el-icon-upload-filled {
          margin-left: 6px; // Slightly adjusted
          color: #ffff5e;
        }
      }
    }

    .file-upload-button-container {
      width: 100%;
      margin-top: 24px; // Adjusted margin

      .file-upload-button-content {
        width: 80%; // Match the width of the upload content
        margin: 0 auto;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 16px;

        .file-upload-button-content-inner {
          width: 300px; // Kept the same as desktop
          height: 50px; // Kept the same as desktop
          border: #ffffff solid 4px; // Kept the same as desktop

          .file-upload-button {
            .el-icon-upload-filled {
              color: #9f8bfe;
              font-size: 28px; // Kept the same as desktop
              margin-left: 8px; // Kept the same as desktop
            }

            .el-icon-upload-filled:hover {
              color: #fff09b;
              font-size: 32px; // Kept the same as desktop
              margin-left: 8px; // Kept the same as desktop
            }
          }
        }
      }
    }
  }
  .el-col {
    text-align: center;
  }
}

// Laptop Screen's Sizes
@media screen and (min-width: 1024px) {

  .file-upload-container {
    width: 100%;
    display: flex;
    flex-direction: column;
    //border: 1px solid #ff0000;

    .file-upload-content {
      border-radius: 8px;
      //background-color: rgba(124, 95, 251, 0.3);
      background-color: rgba(69, 49, 158, 0.3);

      width: 64%;
      margin: 0 auto;

      .el-upload__text {
        color: #fff;
        font-size: 28px;
      }

      .el-upload__tip {
        color: rgba(226, 174, 255, 0.8);
        font-size: 16px;
        margin-left: 16px;

        .el-icon-upload-filled {
          margin-left: 8px;
          color: #ffff5e;
        }
      }
    }

    .file-upload-button-container {
      //border-top: 1px solid #ff0000;
      width: 100%;
      margin-top: 32px;

      .file-upload-button-content {
        //border: #ff2c26 solid 1px;
        width: 64%;
        margin: 0 auto;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 16px;

        .file-upload-button-content-inner {
          width: 300px;
          height: 50px;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: #191a1a;
          border-radius: 8px;
          border: #ffffff solid 4px;

          .file-upload-button {
            //border: #ffff5e solid 2px;
            display: flex;
            align-items: center;
            justify-content: space-between;

            :hover {
              cursor: pointer;
            }

            .el-icon-upload-filled {
              color: #9f8bfe;
              font-size: 28px;
              font-weight: bold;
              margin-left: 8px;
            }

            .el-icon-upload-filled:hover {
              color: #fff09b;
              font-size: 32px;
              margin-left: 8px;
              //cursor: pointer;
            }
          }


        }

      }
    }
  }
  .el-col {
    text-align: center;
  }
}

</style>