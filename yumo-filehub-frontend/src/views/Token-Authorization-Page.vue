<!--Author: Uncle Yumo-->
<template>

  <div>
    <img class="logo-img"
         src="../assets/yumo-filehub/filehub-logo.png"
         alt="filehub-logo"/>
  </div>

  <div class="main-container">

    <div class="logo-text-container">
      <h1 class="logo-text"> Please enter your <span style="color: #7D60FD;">Access-Key</span></h1>
    </div>

    <div class="input-container">
      <el-input v-model="accessKey"
                placeholder="Access-Key"
                class="access-key-input"
                clearable
                type="text"
                @keyup.enter="validateAccessKey">
      </el-input>

      <div class="button-div" @click="validateAccessKey">
        <span class="access-key-button-text">VALIDATE</span>
      </div>
    </div>

  </div>

</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {useTokenStore} from "@/stores/tokenStore";

import {ElMessage} from "element-plus";
import { ElLoading } from 'element-plus'

import router from "@/router";
import {verifyAccessKeyService} from "@/api/handleAccessKey";

const tokenStore = useTokenStore();
const accessKey = ref('');

const validateAccessKey = async () => {
  if (accessKey.value.length === 0) {
    ElMessage({
      message: 'Please enter your Access-Key first!',
      type: 'warning',
      plain: true,
    })
    return;
  }

  const loadingInstance = ElLoading.service({
    text: 'Verifying Access-Key...',
    background: 'rgba(0, 0, 0, 0.7)',
    target: '.main-container',
  })

  try {
    let resultData = await verifyAccessKeyService(accessKey.value)
    loadingInstance.close()
    const tokenStore = useTokenStore();
    tokenStore.setToken(resultData.data);
    ElMessage({
      message: 'Access-Key verified successfully!',
      type:'success',
      duration: 1000,
      plain: true,
    })
    await router.push('/');
  } catch (error) {
    loadingInstance.close()
  }

}

onMounted(() => {
  ElMessage.warning('Access-Key needs to be verified by the server.')
})

</script>

<style lang="scss" scoped>

// Phone Screen's Sizes and Below
@media screen and (max-width: 768px) {

  .logo-img {
    width: 20%;
    height: auto;
    border-radius: 16px;
    margin: 24px auto 0;
    display: block;
  }

  .main-container {
    width: 90%;
    margin: 0 auto;
    padding-top: 16px;

    .logo-text-container {
      text-align: center;
      padding-bottom: 16px;

      .logo-text {
        font-size: 24px; // 调整字体大小
      }
    }

    .input-container {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      padding: 24px 0;

      .access-key-input {
        width: 80%; // 占据整个宽度
        height: 56px;
        border: 3px solid #7D60FD;
        border-radius: 6px;
        background-color: rgba(125, 96, 253, 0.2);
        font-size: 18px;
        margin-bottom: 48px; // 增加间距
      }

      .button-div {
        width: 78%; // 占据整个宽度
        height: 50px;
        background-color: rgba(125, 96, 253, 0.4);
        border: 4px solid #7D60FD;
        border-radius: 8px;
        display: flex;
        justify-content: center;
        align-items: center;

        :hover {
          cursor: pointer;
        }

        .access-key-button-text {
          font-size: 18px;
          font-weight: bold;

          :hover {
            font-size: 20px; // 轻微增大字体
            color: #ffff5e;
          }
        }
      }
    }
  }
}


// Tablet Screen's Sizes and Below
@media screen and (min-width: 768px) and (max-width: 1024px) {

  .logo-img {
    width: 160px;
    height: auto;
    border-radius: 12px;
    margin-top: 16px;
    margin-left: 16px;
  }

  .main-container {
    width: 100%;
    // border: #d51d1d solid 3px;

    .logo-text-container {
      width: 600px;
      margin: 0 auto;

      .logo-text {
        text-align: center;
        font-size: 32px;
      }
    }

    .input-container {
      // border: #ffb3fd solid 2px;
      margin: 0 auto;
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      padding-top: 20px;
      padding-bottom: 20px;

      .access-key-input {
        width: 280px;
        height: 48px;
        border: #7D60FD solid 3px;
        border-radius: 4px;
        background-color: rgba(125, 96, 253, 0.2);
        font-size: 18px;
        margin-right: 32px;
      }

      .button-div {
        width: 160px;
        height: 40px;
        background-color: rgba(125, 96, 253, 0.4);
        border: #7D60FD solid 4px;
        border-radius: 8px;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-left: 32px;

        :hover {
          cursor: pointer;
        }

        .access-key-button-text {
          font-size: 18px;
          font-weight: bold;
        }
      }
    }
  }
}

// Laptop Screen's Sizes
@media screen and (min-width: 1024px) {
  .logo-img {
    width: 240px;
    height: auto;
    border-radius: 16px;
    margin-top: 24px;
    margin-left: 24px;
  }

  .main-container {
    width: 100%;
    // border: #d51d1d solid 3px;

    .logo-text-container {
      width: 800px;
      margin: 0 auto;
      text-align: center;

      .logo-text {
        font-size: 48px;
      }
    }

    .input-container {
      // border: #ffb3fd solid 2px;
      margin: 0 auto;
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      padding-top: 24px;
      padding-bottom: 24px;

      .access-key-input {
        width: 320px;
        height: 64px;
        border: #7D60FD solid 3px;
        border-radius: 6px;
        background-color: rgba(125, 96, 253, 0.2);
        font-size: 24px;
        margin-right: 48px;
      }

      .button-div {
        width: 160px;
        height: 48px;
        background-color: rgba(125, 96, 253, 0.4);
        border: #7D60FD solid 4px;
        border-radius: 8px;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-left: 48px;

        :hover {
          cursor: pointer;
        }

        .access-key-button-text {
          font-size: 24px;
          font-weight: bold;
        }
      }
    }
  }
}

</style>