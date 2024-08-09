<template>
  <div class="container">
    <h2>在线编辑</h2>
    <div class="input-group mb-3">
      <input type="text" v-model="filePath" class="form-control" placeholder="输入文件路径" aria-label="文件路径">
      <div class="input-group-append">
        <button class="btn btn-primary" @click="confirmPath">确认</button>
      </div>
    </div>
    <textarea v-model="fileContent" class="form-control" rows="10"></textarea>
    <button class="btn btn-primary mt-3" @click="saveFile">保存</button>
  </div>
</template>

<script>

import axios from 'axios';
var jwt = localStorage.getItem("jwt")
export default {
  name: 'OnlineEditor',
  data() {
    return {
      filePath: '',
      fileContent: '',
    };
  },
  methods: {
    confirmPath() {
      // 处理路径确认逻辑
      axios.post('/FileContent', { path: this.filePath, jwt: "Bearer " + jwt  , userid: localStorage.getItem('userid')})
          .then(response => {
            // 获取文件内容并更新到文本框中
            this.fileContent = response.data;
          })
          .catch(error => {
            console.error('获取文件内容失败:', error);
          });
    },
    saveFile() {
      axios.post('/SaveFile', { path: this.filePath, content: this.fileContent, jwt: "Bearer " + jwt , userid: localStorage.getItem('userid')})
          .then(response => {
            alert('文件保存成功');
            // 处理保存成功的逻辑
          })
          .catch(error => {
            console.error('保存文件失败:', error);
            // 处理保存失败的逻辑
          });
    },
  },
}
</script>

<style scoped>
/* Add your styles here */
</style>
