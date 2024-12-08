<!--Author: Uncle Yumo-->
<template>
  <div class="markdown-instruction-page">
    <div class="markdown-container">
      <v-md-preview :text="markdownInstruction"></v-md-preview>
    </div>
  </div>
</template>

<script setup lang="ts" >
import {markdownInstructionService} from "@/api/handleFile";
import {onMounted, ref} from "vue";

const markdownInstruction = ref<string>("")

const getMarkdownInstruction = async () => {
  let markdownInstructionData = await markdownInstructionService()
  markdownInstruction.value = markdownInstructionData.data
}

onMounted(() => {
  getMarkdownInstruction()
})
</script>

<style lang="scss" scoped>

.markdown-instruction-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  .markdown-container {
     width: 80%;
    margin: 20px auto;
  }
}

</style>
