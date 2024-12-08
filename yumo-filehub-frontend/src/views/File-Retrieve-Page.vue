<template>
  <div class="logo-container">
    <img @click="goToGithub()" src="../assets/yumo-filehub/filehub-logo.png" alt="">

    <div class="dropdown-container">
      <el-dropdown>
        <el-button size="large" type="text">
          <div>
            <span>FUNCTION MENU</span>
          </div>
          <n-icon color="#FFFFFF" size="24" :component="ArrowCircleDown48Regular"/>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>

            <el-dropdown-item @click="goToFileUploadPage()">
              <n-icon size="24" :component="BookSearch24Filled"/>
              <span style="font-size: 16px;">Upload new file</span>
            </el-dropdown-item>

            <el-dropdown-item @click="goToGithub()">
              <n-icon size="24" :component="GithubOutlined"/>
              <span style="font-size: 16px">Go to github project page</span>
            </el-dropdown-item>

            <el-dropdown-item @click="goToFAQ()">
              <n-icon size="24" :component="QuestionCircle20Filled"/>
              <span style="font-size: 16px">How to use filehub?</span>
            </el-dropdown-item>

            <el-dropdown-item @click="logoutSystem()">
              <n-icon size="24" :component="IosLogOut"/>
              <span style="font-size: 16px;color: rgb(255,179,253)">Logout system</span>
            </el-dropdown-item>

          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>

  <div class="file-retrieve-main-page">
    <div class="file-retrieve-card-container">
      <el-card class="file-retrieve-card">
        <template #header>
          <div class="card-header">
            <span class="card-title-text">Basic Information</span>
            <div class="card-file-retrieve-button-container">
              <el-button color="rgb(125, 96,253, 0.7)" @click="getAllInfo()">Refresh Data</el-button>
            </div>
          </div>
        </template>

        <div><p>Available Space (KB)</p><span>{{ userAvailableSpace }}</span></div>
        <div><p>Access-Key Valid Time (Minutes) </p><span>{{ userValidTime }}</span></div>
        <div><p>Quantity of Files </p><span>{{ fileListData.length }}</span></div>

      </el-card>
    </div>
  </div>

  <div class="table-content-container">
    <div class="el-table-content">
      <el-table
          :data="fileListDataViewObjectList"
          table-layout="auto"
          highlight-current-row
          fit
          stripe
          @cell-click="handleTableMouseClick"
          class="el-table-file-list"
      >
        <el-table-column prop="originalFileName" label="Original-File-Name"/>
        <el-table-column prop="createTime" label="Create-Time"/>
        <el-table-column prop="fileSize" label="File-Size(KB)" class-name="file-size-column"/>
        <el-table-column label="Operations">
          <template #default="{ row }">
            <div class="operation-button-container">
              <el-button color="rgb(125, 96,253, 0.5)" size="small" @click="showFileDetail(row)">
                Detail
              </el-button>
              <el-button @click="deleteSelectedFile(row)" color="rgb(177.5, 81.6, 81.6, 0.6)" size="small">Delete</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

    </div>

    <el-dialog
        v-model="fileDetailDialogVisible"
        title="File Detail"
        class="el-dialog-file-detial"
    >
      <div class="file-detail-dialog-content">
        <div><p style="color: rgb(155,133,251);font-weight: bold">Original-FIle-Name</p>
          <span>{{ currentFileViewObject.originalFileName }}</span></div>
        <div><p style="color: rgb(155,133,251);font-weight: bold">UUID-FIle-Name</p><span>{{
            currentFileViewObject.uuidFileName
          }}</span></div>
        <div><p style="color: rgb(155,133,251);font-weight: bold">File-Size (KB)</p><span>{{
            currentFileViewObject.fileSize
          }}</span></div>
        <div><p style="color: rgb(155,133,251);font-weight: bold">Create-Time</p><span>{{
            currentFileViewObject.createTime
          }}</span></div>
        <div><p style="color: rgb(155,133,251);font-weight: bold">Valid-Time</p><span>{{
            currentFileViewObject.validTime
          }}</span></div>

        <div style="display: flex; align-items: center;margin-bottom: 8px">
          <p style="color: rgb(155,133,251); font-weight: bold; margin: 0;">File-URL</p>
          <n-icon size="24" :component="AddLinkOutlined" style="margin-left: 5px;"></n-icon>
        </div>
        <span class="text-link-container" style="color: #ffffa6">{{currentFileViewObject.fileUrl}}</span>

      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click.stop="fileDetailDialogVisible = false">Exit</el-button>
          <el-button color="rgb(155,133,251)" @click.stop="handleCopyFileURL">
            COPY URL
          </el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">

import ArrowCircleDown48Regular from "@vicons/fluent/ArrowCircleDown48Regular";
import {BookSearch24Filled, QuestionCircle20Filled} from "@vicons/fluent";
import {GithubOutlined} from "@vicons/antd";
import {IosLogOut} from "@vicons/ionicons4";
import {ElMessage, ElMessageBox} from "element-plus";
import {useTokenStore} from "@/stores/tokenStore";
import router from "@/router";
import {onMounted, ref} from "vue";
import {deleteFileService, fileListService} from "@/api/handleFile";
import {userAvailableSpaceService, userValidTimeService} from "@/api/handleUser";
import {ElLoading} from 'element-plus'
import {AddLinkOutlined} from '@vicons/material'

const userAvailableSpace = ref(0);
const userValidTime = ref(0);
const fileListData = ref([]);
const fileListDataViewObjectList = ref<FileListDataViewObject[]>([]); // 明确指定类型
const fileDetailDialogVisible = ref(false);

// 定义文件视图对象的类型
interface FileListDataViewObject {
  originalFileName: string;
  uuidFileName: string;
  fileUrl: string;
  fileSize: number;
  createTime: string;
  validTime: number;
}

const currentFileViewObject = ref<FileListDataViewObject>({
  originalFileName: '',
  uuidFileName: '',
  fileUrl: '',
  fileSize: -1,
  createTime: '',
  validTime: 0
})

const transformFileListData = () => {
  const currentTime = new Date().getTime(); // 获取当前时间的时间戳

  // 清空 dataViewObjectList
  fileListDataViewObjectList.value = [];

  // 遍历 fileListData 来转换数据
  fileListData.value.forEach((file: any) => {
    const validTimeInMinutes = Math.max(
        0,
        file.validTime - Math.floor((currentTime - new Date(file.createTime).getTime()) / 60000)
    ); // 计算有效时间
    const formattedCreateTime = file.createTime.replace('T', ' ').slice(0, 19); // 格式化创建时间

    // 创建转换后的对象
    const fileViewObject: FileListDataViewObject = {
      originalFileName: file.originalFileName,
      uuidFileName: file.uuidFileName,
      fileUrl: file.fileUrl,
      fileSize: file.fileSize,
      createTime: formattedCreateTime,
      validTime: validTimeInMinutes, // 使用计算后的有效时间
    };

    // 将转换后的对象添加到 dataViewObjectList
    fileListDataViewObjectList.value.push(fileViewObject);
  });
};

const goToGithub = () => {
  window.open('https://github.com/UncleYumo/Yumo-FileHub', '_blank');
};

const logoutSystem = () => {

  ElMessageBox.confirm(
      'Are you sure to log out the system?',
      'Warning',
      {
        confirmButtonText: 'Yes',
        cancelButtonText: 'Cancel',
        type: 'warning',
      })
      .then(async () => {
        ElMessage({
          type: 'warning',
          message: 'Confirm logout',
        })
        const tokenStore = useTokenStore()
        tokenStore.removeToken()
        await router.push('/authorize')
      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: 'Logout canceled',
        })
      })
}

const goToFileUploadPage = () => {
  router.push('/')
}

// 在 getAllInfo 获取到数据后调用此函数
const getAllInfo = async () => {
  const loadingInstance = ElLoading.service({
    text: 'Loading Data...',
    background: 'rgba(0, 0, 0, 0.7)'
  });

  let fileListResultData = await fileListService();
  let userAvailAbleSpaceData = await userAvailableSpaceService();
  let userValidTimeData = await userValidTimeService();

  fileListData.value = fileListResultData.data;
  userValidTime.value = userValidTimeData.data;
  userAvailableSpace.value = userAvailAbleSpaceData.data;

  // 调用数据转换函数
  transformFileListData();

  loadingInstance.close();
};

const handleTableMouseClick = (row: FileListDataViewObject, column: any, event: any) => {
  // showFileDetail(row)
}

const showFileDetail = (fileViewObject: FileListDataViewObject) => {
  // 打开文件详情页面
  fileDetailDialogVisible.value = true
  currentFileViewObject.value = fileViewObject
}

const deleteSelectedFile = async (fileViewObject: FileListDataViewObject) => {
  let result = await deleteFileService({
    "uuidFileName": fileViewObject.uuidFileName
  })

  ElMessage({
    message: 'File deleted successfully',
    type:'success',
    plain: true
  })

  await getAllInfo()
}

const handleCopyFileURL = async () => {
  const el = document.createElement('textarea');
  el.value = currentFileViewObject.value.fileUrl;
  document.body.appendChild(el);
  el.select();
  document.execCommand('copy');
  document.body.removeChild(el);

  ElMessage({
    message: 'File URL copied successfully',
    type:'success',
    plain: true
  })
}

const goToFAQ = () => {
  router.push('/instruction')
}

onMounted(async () => {
  await getAllInfo()
})

</script>

<style scoped lang="scss">
// Phone Screen's Sizes and Below
@media screen and (max-width: 768px) {
  .logo-container {
    //border: 2px solid #b7f0b8;
    padding: 4px;
    display: flex;
    justify-content: space-between;
    align-items: center; // Center the logo vertically

    img {
      width: auto;
      height: 50px;
      border-radius: 8px;
    }

    img:hover {
      cursor: pointer;
    }


    .dropdown-container {
      border: 2px solid #ffffff;
      border-radius: 8px;
      display: flex;
      margin-right: 8px;

      div {

        margin: 1px;

        span {
          margin-left: 8px;
          color: #7D60FD;
          font-weight: bold;
          font-size: 16px;
        }
      }
    }
  }

  .file-retrieve-main-page {
    //border: #d51d1d solid 2px;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;

    .file-retrieve-card-container {
      //border: #7D60FD solid 2px;
      width: 300px;
      margin-bottom: 16px;

      .file-retrieve-card {

        margin-bottom: 8px;

        div {
          padding-left: 4px;
          padding-right: 4px;
          display: flex;
          justify-content: space-between;
          align-items: center;

          p {
            font-size: 12px;
          }
        }

        .card-file-retrieve-button-container {
          display: flex;
          justify-content: center;
          align-items: center;
        }

        .card-header {
          .card-title-text {
            font-size: 16px;
            font-weight: bold;
            color: #7d60fd;
          }
        }
      }
    }
  }

  .table-content-container {
    //border: #7D60FD solid 2px;
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin-bottom: 16px;
  }

  .file-detail-dialog-content {
    display: flex;
    flex-direction: column;

    div {
      display: flex;
      justify-content: space-between;
      align-items: center;

      p {
        font-size: 8px;
      }

      span {
        font-size: 8px;
      }
    }
    .text-link-container {
      font-size: 10px;
    }
  }

  .operation-button-container {
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;

    .el-button {
      font-size: 8px;
      margin: 8px auto;
    }
  }


  .dialog-footer {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;

    .el-button {
      margin-bottom: 8px;
      font-size: 12px;
    }
  }

  .el-table-file-list {
    font-size: 10px; // 调整表格字体大小
  }

  .el-table-column {
    font-size: 10px; // 可以在表格列上也设置字体大小
  }


}

// Tablet Screen's Sizes and Below
@media screen and (min-width: 768px) and (max-width: 1024px) {
  .logo-container {
    //border: 2px solid #b7f0b8;
    padding: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center; // Center the logo vertically

    img {
      width: auto;
      height: 80px;
      border-radius: 16px;
    }

    img:hover {
      cursor: pointer;
    }


    .dropdown-container {
      border: 4px solid #ffffff;
      border-radius: 8px;
      display: flex;
      margin-right: 24px;

      div {

        margin: 8px;

        span {
          margin-left: 18px;
          color: #7D60FD;
          font-weight: bold;
          font-size: 32px;
        }
      }
    }
  }

  .file-retrieve-main-page {
    //border: #a7ffad solid 2px;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;

    .file-retrieve-card-container {
      //border: #7D60FD solid 2px;
      width: 600px;
      margin-bottom: 24px;

      .file-retrieve-card {

        margin-bottom: 16px;

        div {
          padding-left: 16px;
          padding-right: 16px;
          display: flex;
          justify-content: space-between;
          align-items: center;

          p {
            font-size: 16px;
          }
        }

        .card-file-retrieve-button-container {
          display: flex;
          justify-content: center;
          align-items: center;
        }

        .card-header {
          .card-title-text {
            font-size: 24px;
            font-weight: bold;
            color: #7d60fd;
          }
        }
      }
    }
  }

  .table-content-container {
    //border: #7D60FD solid 2px;
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin-bottom: 32px;

    .el-table-content {
      width: 100%;
      margin: 0 auto;
    }
  }

  .file-detail-dialog-content {
    display: flex;
    flex-direction: column;

    div {
      display: flex;
      justify-content: space-between;
      align-items: center;

      p {
        font-size: 10px;
      }

      span {
        font-size: 10px;
      }
    }
    .text-link-container {
      font-size: 10px;
    }

  }

}

// Laptop Screen's Sizes
@media screen and (min-width: 1024px) {

  .logo-container {
    //border: 2px solid #b7f0b8;
    padding: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center; // Center the logo vertically

    img {
      width: auto;
      height: 120px;
      border-radius: 24px;
    }

    img:hover {
      cursor: pointer;
    }


    .dropdown-container {
      border: 4px solid #ffffff;
      border-radius: 8px;
      display: flex;
      margin-right: 24px;

      div {

        margin: 8px;

        span {
          margin-left: 18px;
          color: #7D60FD;
          font-weight: bold;
          font-size: 32px;
        }
      }
    }
  }

  .file-retrieve-main-page {
    //border: #ffff5e solid 2px;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;

    .file-retrieve-card-container {
      //border: #7D60FD solid 2px;
      width: 800px;
      margin-bottom: 24px;

      .file-retrieve-card {

        margin-bottom: 16px;

        div {
          padding-left: 16px;
          padding-right: 16px;
          display: flex;
          justify-content: space-between;
          align-items: center;

          p {
            font-size: 16px;
          }
        }

        .card-file-retrieve-button-container {
          display: flex;
          justify-content: center;
          align-items: center;
        }

        .card-header {
          .card-title-text {
            font-size: 24px;
            font-weight: bold;
            color: #7d60fd;
          }
        }
      }
    }
  }

  .table-content-container {
    //border: #7D60FD solid 2px;
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin-bottom: 32px;

    .el-table-content {
      width: 80%;
      margin: 0 auto;
    }
  }

  .file-detail-dialog-content {
    display: flex;
    flex-direction: column;

    div {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}

</style>