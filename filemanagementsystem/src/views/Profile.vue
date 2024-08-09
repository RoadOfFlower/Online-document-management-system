<template>
  <div class="profile">
    <h2>个人信息</h2>
    <form @submit.prevent="saveProfile">
      <div class="form-group">
        <label for="name">姓名</label>
        <input type="text" id="name" class="form-control" v-model="name" />
      </div>
      <div class="form-group">
        <label for="email">电子邮件</label>
        <input type="email" id="email" class="form-control" v-model="email" />
      </div>
      <div class="form-group">
        <label for="phone">电话号码</label>
        <input type="tel" id="phone" class="form-control" v-model="tel" />
      </div>
      <button type="submit" class="btn btn-primary">保存</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Profile',
  data() {
    return {
      name: '',
      email: '',
      tel: '',
    };
  },
  mounted() {
    this.getProfile();
  },
  methods: {
    getProfile() {
      // 构建 POST 请求的数据
      const data = {
        jwt: "Bearer " + localStorage.getItem("jwt"), // 替换为实际的 JWT 值
      };

      // 发起 POST 请求获取个人信息数据，并填充表单
      axios.post('/Profile', data)
          .then(response => {
            const data = response.data;
            this.name = data.username;
            this.email = data.email;
            this.tel = data.tel;
            console.log(this.name)
          })
          .catch(error => {
            console.error(error);
          });
    },
    saveProfile() {
      // 构建 POST 请求的数据
      const data = {
        name: this.name,
        email: this.email,
        tel: this.tel,
        jwt: "Bearer " + localStorage.getItem("jwt"), // 替换为实际的 JWT 值
      };

      // 发起 POST 请求保存个人信息数据
      axios.post('/Profile', data)
          .then(response => {
            // 处理保存成功的逻辑
            console.log('保存成功');
            // 更新文本框的内容
            this.name = response.data.name;
            this.email = response.data.email;
            this.tel = response.data.tel;
          })
          .catch(error => {
            // 处理保存失败的逻辑
            console.error(error);
          });
    },
  },
};
</script>

<style scoped>
/* Add your styles here */
</style>
