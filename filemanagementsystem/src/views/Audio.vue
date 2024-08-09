<template>
  <div class="container">
    <h2>音频播放</h2>
    <div class="input-group mb-3">
      <input type="text" v-model="audioPath" class="form-control" placeholder="输入音频路径" aria-label="音频路径">
      <div class="input-group-append">
        <button class="btn btn-primary" @click="confirmPath">确认</button>
      </div>
    </div>
    <audio :src="audioSrc" controls></audio>

  </div>
</template>

<script>
import axios from 'axios';
var jwt = localStorage.getItem("jwt")
export default {
  name: 'AudioPlayer',
  data() {
    return {
      audioPath: '',
      audioSrc: ''
    };
  },
  methods: {
    confirmPath() {
      // 发送请求到后端获取音频文件
      const fullVideoUrl = `${axios.defaults.baseURL}/uploads/` + localStorage.getItem('userid') + `/${this.audioPath}`;

      // 检查视频 URL 是否存在，然后更新 videoSrc
      axios.head(fullVideoUrl).then(response => {
        if (response.status === 200) {
          this.audioSrc = fullVideoUrl;
        } else {
          console.error(`Error fetching video: ${response.status}`);
        }
      }).catch(error => {
        console.error(`Error fetching video: ${error}`);
      });
    },
  },
}
</script>

<style scoped>
/* Add your styles here */
</style>
