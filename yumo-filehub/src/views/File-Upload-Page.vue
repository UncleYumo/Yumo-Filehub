<template>
  <div class="logo-container">
    <img @click="goToGithub()" src="../assets/yumo-filehub/filehub-logo.png" alt="">

    <div class="dropdown-container">
      <el-dropdown>
        <el-button size="large" type="text">
          <div>
            <span>FUNCTION MENU</span>
          </div>
          <n-icon color="#FFFFFF" size="28" :component="ArrowCircleDown48Regular"/>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>

            <el-dropdown-item @click="goToFileRetrieve()">
              <n-icon size="28" :component="BookSearch24Filled"/>
              <span style="font-size: 20px;">Retrieve History Files</span>
            </el-dropdown-item>

            <el-dropdown-item @click="goToGithub()">
              <n-icon size="28" :component="GithubOutlined"/>
              <span style="font-size: 20px">To the Github Project Page</span>
            </el-dropdown-item>

            <el-dropdown-item>
              <n-icon size="28" :component="HelpCenterFilled"/>
              <span style="font-size: 20px">How To Use Yumo FileHub</span>
            </el-dropdown-item>

          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>

  <div class="slogan-container">
    <div class="slogan-content">
      <h1 class="h1-slogan">Make <span style="color: #7D60FD;">File Sharing</span> {{ typing_slogan }}</h1>
    </div>

  </div>

  <FileUpload/>
</template>


<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { GithubOutlined } from '@vicons/antd';
import { BookSearch24Filled } from '@vicons/fluent';
import { HelpCenterFilled } from '@vicons/material';

import FileUpload from '../components/File-Upload-Page/FileUpload.vue'
import ArrowCircleDown48Regular from '@vicons/fluent/ArrowCircleDown48Regular';

const typing_slogan = ref(''); // 初始值

const typingEffect = async () => {
  const slogans = ["Easily", "Great Again"]; // 目标字符串数组
  for (const slogan of slogans) {
    const oldSlogan = typing_slogan.value; // 获取当前字符串

    // 删除旧的文本
    for (let i = oldSlogan.length; i >= 0; i--) {
      typing_slogan.value = oldSlogan.substring(0, i);
      await new Promise(resolve => setTimeout(resolve, 180));
    }

    // 替换为新的文本
    for (let j = 0; j < slogan.length; j++) {
      typing_slogan.value = slogan.substring(0, j + 1);
      await new Promise(resolve => setTimeout(resolve, 80));
    }
  }
};

onMounted(() => {
  typingEffect();
});

const goToGithub = () => {
  window.open('https://github.com/UncleYumo/Yumo-FileHub', '_blank');
};

const goToFileRetrieve = () => {
  window.location.href = '/retrieve';
};

</script>

<style lang="scss" scoped>
// Phone Screen's Sizes and Below
@media screen and (max-width: 768px) {
  .logo-container {
    //border: 2px solid #e8fbdd;
    padding: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center; // Center the logo vertically

    img {
      width: auto;
      height: 40px;
      border-radius: 8px;
    }

    img:hover {
      cursor: pointer;
    }


    .dropdown-container {
      border: 2px solid #ffffff;
      border-radius: 4px;
      display: flex;

      div {
        span {
          margin-left: 14px;
          color: #7D60FD;
          font-weight: bold;
          font-size: 14px;
        }
      }
    }
  }

  .slogan-container {
    //border: 2px solid #d51d1d;

    .slogan-content {
      padding: 8px;

      //background-color: #d51d1d;
      .h1-slogan {
        width: 72%;
        text-align: center;
        font-size: 16px;
        color: white;
        margin: 0 auto;
      }
    }
  }
}

// Tablet Screen's Sizes and Below
@media screen and (min-width: 768px) and (max-width: 1024px) {

  .logo-container {
    //border: 2px solid #d51d1d;

    padding: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center; // Center the logo vertically

    img {
      width: auto;
      height: 100px;
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

  .slogan-container {
    //border: 2px solid #d51d1d;

    .slogan-content {
      padding: 8px;

      //background-color: #d51d1d;
      .h1-slogan {
        width: 72%;
        text-align: center;
        font-size: 32px;
        color: white;
        margin: 0 auto;
      }
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

  .slogan-container {
    //border: 2px solid #d51d1d;

    .slogan-content {
      padding: 24px;
      //background-color: #d51d1d;
      .h1-slogan {
        width: 76%;
        text-align: center;
        font-size: 48px;
        color: white;
        margin: 0 auto;
      }
    }

  }
}

</style>