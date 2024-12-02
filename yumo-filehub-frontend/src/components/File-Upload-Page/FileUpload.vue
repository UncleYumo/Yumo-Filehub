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

</template>

<script setup lang="ts">
import {UploadFilled} from '@element-plus/icons-vue'
import {ref, computed} from 'vue'
import {UploadFileFilled} from '@vicons/material'
import type {UploadInstance, UploadUserFile} from 'element-plus'
import axios from 'axios'

const uploadRef = ref<UploadInstance>()  // 用于控制上传的引用
const fileName = ref('No Selected File')
const fileObject = ref()

const isFileSelected = computed(() => {
  return fileName.value !== 'No Selected File'
})

const formattedFileName = computed(() => {
  const maxLength = 36; // 设置最大字符长度
  return fileName.value.length > maxLength
      ? fileName.value.slice(0, maxLength) + '...(omitted)'
      : fileName.value;
});

const handFileChange = (file: File) => {
  console.log("File Changed: " + file.name)

  if (file.size > 200 * 1024 * 1024) {
    // TODO: Using Element-Plus's Dialog to show an alert
    alert('File size should be less than 200MB')
    return
  }

  uploadRef?.value?.clearFiles()
  fileName.value = file.name
  fileObject.value = file
}

const submitFile = async () => {
  if (!isFileSelected.value) {
    // TODO: Using Element-Plus's Dialog to show an alert
    alert('Please select a file to upload')
    return
  }

  let formData = new FormData()
  formData.append('file', fileObject.value.raw)
  let utl = 'https://filehub.uncleyumo.cn/file/upload'
  let method = 'post'
  let headers = {
    'Content-Type': 'multipart/form-data',
    'Authorization': 'uncleyumo'
  }
  axios({
    method: method,
    url: utl,
    data: formData,
    headers: headers
  }).then(res => {
    console.log(res)
  }).catch(err => {
    console.log(err)
  })
}

const handleUploadClick = () => {
  uploadRef?.value?.clearFiles()
  fileName.value = 'No Selected File'
  console.log('Upload Ref Cleared')
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

}

</style>