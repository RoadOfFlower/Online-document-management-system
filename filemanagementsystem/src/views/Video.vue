<template>
  <div class="container">
    <h2>视频播放</h2>
    <div class="input-group mb-3">
      <input type="text" v-model="videoPath" class="form-control" placeholder="输入视频路径" aria-label="视频路径">
      <div class="input-group-append">
        <button class="btn btn-primary" @click="confirmPath">确认</button>
      </div>
    </div>
    <div class="embed-responsive full-width">
<!--      <video class="embed-responsive-item" :src="videoPath" controls></video>-->
      <video :src="videoSrc" controls></video>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
var jwt = localStorage.getItem("jwt")

export default {
  name: 'VideoPlayer',
  data() {
    return {
      videoPath: '',
      videoSrc: null,
    };
  },
  methods: {
    // confirmPath() {
    //   // 发送请求到后端获取音频文件
    //   axios.post('/Video', { path: this.videoPath, jwt: "Bearer " + jwt })
    //       .then(response => {
    //         // 根据返回的音频文件设置音频内容
    //         const videoBlob = new Blob([response.data], { type: 'video/*' });
    //         console.log(response.data)
    //         const videoUrl = URL.createObjectURL(videoBlob);
    //         console.log(videoUrl)
    //         // this.$refs.audioPlayer.src = videoUrl;
    //         // this.videoPath = videoUrl;
    //         this.videoSrc = URL.createObjectURL(videoBlob);
    //       })
    //       .catch(error => {
    //         console.error('获取视频文件失败:', error);
    //       });
    // },
    confirmPath() {
      // 发送请求到后端获取音频文件
      const fullVideoUrl = `${axios.defaults.baseURL}/uploads/` + localStorage.getItem('userid') + `/${this.videoPath}`;

      // 检查视频 URL 是否存在，然后更新 videoSrc
      axios.head(fullVideoUrl).then(response => {
        if (response.status === 200) {
          this.videoSrc = fullVideoUrl;
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
.full-width {
  width: 100%;
}
</style>
