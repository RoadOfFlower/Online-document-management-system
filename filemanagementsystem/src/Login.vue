<template>
  <div>
    <h2>登录</h2>
    <form @submit.prevent="login">
      <div class="form-group">
        <label for="username">用户名</label>
        <input type="text" class="form-control" id="username" v-model="loginForm.username" required>
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input type="password" class="form-control" id="password" v-model="loginForm.password" required>
      </div>
      <button type="submit" class="btn btn-primary">登录</button>
    </form>
    <p class="mt-3">
      还没有账号？立即
      <router-link to="/Register">注册</router-link>
    </p>
  </div>
</template>
<script>
import axios from 'axios';
import router from '@/router';

export default {
  name: 'Auth',

  data() {
    return {
      loginForm: {
        username: '',
        password: '',
      },
    };
  },
  methods: {
    login() {
      // 构建 POST 请求的数据
      const data = new URLSearchParams();
      data.append('username', this.loginForm.username);
      data.append('password', this.loginForm.password);

      // 发送 POST 请求
      axios.post('/Login', data)
          .then(response => {
            // 请求成功，可以在这里处理响应
            console.log(response.data.token);
            const token = response.data.token;
            const userid = response.data.userid;

            // 将 JWT 存储在本地存储 (LocalStorage) 中
            localStorage.setItem('jwt', token);
            localStorage.setItem('userid', userid);
            console.log(localStorage.getItem('jwt'));
            console.log(localStorage.getItem('userid'));

            // 解码 JWT 获取用户信息（可选）
            // const decoded = jwt_decode(token);
            // const user = decoded.username;

            // 跳转到主页
            router.push({ path: '/' });

            // 清空登录表单
            this.loginForm.username = '';
            this.loginForm.password = '';
          })
          .catch(error => {
            // 请求失败，可以在这里处理错误
            console.log(error);
          });
    },
  },
}
</script>
<style scoped>
/* Add your styles here */
</style>
