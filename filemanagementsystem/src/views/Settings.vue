<template>
  <div class="container">
    <h2>设置</h2>
<!--    <div class="mb-3">-->
<!--      <label for="currentPassword" class="form-label">当前密码</label>-->
<!--      <input id="currentPassword" type="password" class="form-control" v-model="currentPassword">-->
<!--    </div>-->
<!--    <div class="mb-3">-->
<!--      <label for="newPassword" class="form-label">新密码</label>-->
<!--      <input id="newPassword" type="password" class="form-control" v-model="newPassword">-->
<!--    </div>-->
<!--    <div class="mb-3">-->
<!--      <label for="confirmPassword" class="form-label">确认密码</label>-->
<!--      <input id="confirmPassword" type="password" class="form-control" v-model="confirmPassword">-->
<!--    </div>-->
    <div class="row">
<!--      <div class="col">-->
<!--        <button class="btn btn-primary" @click="changePassword">修改密码</button>-->
<!--      </div>-->
      <div class="col">
        <button class="btn btn-danger" @click="logout">退出登录</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Settings',
  data() {
    return {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    };
  },
  methods: {
    changePassword() {
      // 处理修改密码逻辑
      console.log('当前密码:', this.currentPassword);
      console.log('新密码:', this.newPassword);
      console.log('确认密码:', this.confirmPassword);
      // 执行密码修改操作，你可以在这里调用后端API或其他逻辑
    },
    logout() {
      // 处理退出登录逻辑
      console.log('退出登录');
      // 执行退出登录操作，例如清除用户登录状态、跳转到登录页面等

      // 删除 localStorage 中的 jwt
      localStorage.removeItem('jwt');

      alert('退出成功')

      // 跳转到登录页面（示例代码，根据实际情况修改）
      this.$router.push('/Login');
    },
  },
}
</script>

<style scoped>
/* Add your styles here */
</style>
