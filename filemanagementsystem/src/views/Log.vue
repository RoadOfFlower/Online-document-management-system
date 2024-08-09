<template>
  <div class="container">
    <h2>查看日志</h2>

    <ul class="list-group">
      <li v-for="file in files" :key="file.id" class="list-group-item" @click="handleClick(file)">
        {{ file.name }}
      </li>
    </ul>
  </div>
</template>

<script>
import axios from 'axios';
var jwt = localStorage.getItem("jwt")
export default {
  name: 'Log',
  data() {
    return {
      files: [
        {
          id: 1,
          name: '文件1',
        },
        {
          id: 2,
          name: '文件2',
        },
        {
          id: 3,
          name: '文件3',
        },
      ],
    };
  },
  mounted() {
    // 发送 HTTP 请求，获取后端返回的 JSON 数据
    axios.post('/Log', {jwt: "Bearer " + jwt})
        .then(response => {
          // 在前端接收并解析 JSON 数据
          var start = 1;
          var res = response.data;
          var arr = []
          for (var element of res) {
            arr.push({id: start, name: element})
            start += 1;
          }
          for (var element of res) {
            console.log(element)
          }
          console.log(response.data)
          this.files = arr;

        })
        .catch(error => {
          console.error('获取数据失败:', error);
        });
  },
  methods: {
    handleClick(file) {
      console.log(`你选择了: ${file.name}`);
      // 执行一些逻辑
    },
  },
}
</script>

<style scoped>
/* Add your styles here */
</style>
