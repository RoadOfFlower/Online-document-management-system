<template>
  <div class="container">
    <h2>上传文件</h2>
    <div class="mb-3">
      <input type="file" ref="fileInput" class="form-control-file" @change="handleFileChange" />
    </div>
    <div class="mb-3">
      <label for="path">路径</label>
      <input type="text" id="path" v-model="path" class="form-control" />
    </div>
    <button class="btn btn-primary" @click="uploadFile">上传</button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'UploadFile',
  data() {
    return {
      path: '', // 添加 path 数据
    };
  },
  methods: {
    handleFileChange() {
      // 处理文件选择逻辑
      const file = this.$refs.fileInput.files[0];
      console.log('选择的文件:', file);
    },
    uploadFile() {
      // 处理文件上传逻辑
      const file = this.$refs.fileInput.files[0];
      console.log('上传文件:', file);
      console.log('路径:', this.path); // 获取路径数据

      // 创建 FormData 对象
      const formData = new FormData();
      formData.append('file', file);
      formData.append('path', this.path);
      formData.append('jwt', "Bearer " + localStorage.getItem("jwt"))

      // 发送 POST 请求
      axios.post('/Upload', formData)
          .then(response => {
            // 处理上传成功的逻辑
            var code = response.data.code
            if (code === 200) {
              alert('上传成功')
            } else {
              alert('上传失败')
            }
          })
          .catch(error => {
            // 处理上传失败的逻辑
            console.error('上传失败:', error);
          });
    },
  },
};
</script>

<style scoped>
/* Add your styles here */
</style>
