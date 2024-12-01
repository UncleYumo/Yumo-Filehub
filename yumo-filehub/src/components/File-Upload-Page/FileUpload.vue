<!--Author: Uncle Yumo-->
<template>

  <div class="file-upload-container">
    <div class="file-upload-content">
      <el-upload
          ref="uploadRef"
          class="upload-component"
          drag
          :show-file-list="false"
          :auto-upload="false"
          :action="fileUploadURL"
          :limit="1"
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
          <div class="file-upload-button" @click="handleUploadClick">
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
import type { UploadInstance, UploadProps, UploadRawFile } from 'element-plus'

const uploadRef = ref<UploadInstance>()  // 用于控制上传的引用
const fileUploadURL = ref('http://localhost/yumo-fllehub/file-upload')
const fileName = ref('No Selected File')

const formattedFileName = computed(() => {
  const maxLength = 30; // 设置最大字符长度
  return fileName.value.length > maxLength
      ? fileName.value.slice(0, maxLength) + '...(omitted)'
      : fileName.value;
});


const handFileChange = (file: UploadRawFile) => {
  uploadRef?.value?.clearFiles()
  fileName.value = file.name

  if (file.size > 200 * 1024 * 1024) {
    alert('File size should be less than 200MB')
    return
  }
}

const handleUploadClick = () => {
  alert('Upload Clicked')
}
</script>

<style lang="scss" scoped>

// Phone Screen's Sizes and Below
@media screen and (max-width: 768px) {


}

// Tablet Screen's Sizes and Below
@media screen and (min-width: 768px) and (max-width: 1024px) {


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
        border: #ff2c26 solid 1px;
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