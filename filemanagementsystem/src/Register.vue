<template>
  <div>
    <h2>注册</h2>
    <form @submit.prevent="register">
      <div class="form-group">
        <label for="newUsername">用户名</label>
        <input type="text" class="form-control" id="newUsername" v-model="registerForm.username" required>
      </div>
      <div class="form-group">
        <label for="newPassword">密码</label>
        <input type="password" class="form-control" id="newPassword" v-model="registerForm.password" required>
      </div>
      <button type="submit" class="btn btn-primary">注册</button>
    </form>
    <p class="mt-3">
      <router-link to="/Login">登录</router-link>
    </p>
  </div>
</template>

<script>
import axios from "axios";
import router from "@/router";

export default {
  name: 'Auth',
  data() {
    return {
      registerForm: {
        username: '',
        password: '',
      },
    };
  },
  methods: {
    register() {
      const data = new URLSearchParams();
      data.append('username', this.registerForm.username);
      data.append('password', this.registerForm.password);
      // 处理注册逻辑
      console.log('注册信息:', this.registerForm);
      // 执行注册操作，例如调用后端API进行用户创建
      axios.post('/Register', data)
          .then(response => {
            console.log(response.data);
            var code = response.data.code
            console.log(code);
            if (code === 200) {
              alert('注册成功')
              // 跳转到主页
              this.$router.push({ path: '/Login' });
            } else {
              alert('注册失败')
            }

            // 清空登录表单
            this.loginForm.username = '';
            this.loginForm.password = '';
          })
          .catch(error => {
            // 请求失败，可以在这里处理错误
            console.log(error);
          });

      // 清空注册表单
      this.registerForm.username = '';
      this.registerForm.password = '';
    },
  },
}
</script>

<style scoped>
/* Add your styles here */
</style>
