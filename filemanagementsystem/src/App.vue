<template>
  <div class="container-fluid">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <a class="navbar-brand" href="#">文件管理系统</a>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav ml-auto">
          <li class="nav-item">
            <span class="navbar-text">
              {{ username }}
            </span>
          </li>
        </ul>
      </div>
    </nav>

    <div class="row">
      <div class="col-3">
        <nav class="navbar navbar-expand-lg navbar-light bg-light flex-column h-100">
          <div class="collapse navbar-collapse" id="navbarNavSide">
            <ul class="navbar-nav flex-column">
              <li class="nav-item">
                <router-link class="nav-link" to="/profile">个人信息</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/files">浏览文件</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/upload">上传文件</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/edit">在线编辑</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/audio">音频播放</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/video">视频播放</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/log">日志</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/settings">设置</router-link>
              </li>
            </ul>
          </div>
        </nav>
      </div>
      <div class="col-9">
        <router-view/>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
      data() {
    return {
      username: localStorage.getItem("username"), // 替换为实际的用户名
    };
  },
}
</script>

<style scoped>
.h-100 {
  height: calc(100vh - 56px);
}
</style>
