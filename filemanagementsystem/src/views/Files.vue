<template>
  <div className="container">
    <h2>浏览文件</h2>

    <ul className="list-group">
      <FileItem v-for="file in files" :key="file.id" :file="file"/>
    </ul>
  </div>
</template>

<script>
import axios from 'axios';

var jwt = localStorage.getItem("jwt")

const FileItem = {
  name: 'FileItem',
  props: ['file'],
  template: `
    <li class="list-group-item">
    <span @click="handleClick(file)" :class="{ 'font-weight-bold': file.isDirectory }">
        {{ file.name }}
      </span>
    <ul v-if="file.isDirectory" class="list-group mt-2">
      <FileItem v-for="childFile in file.children" :key="childFile.id" :file="childFile"/>
    </ul>
    </li>
  `,
  methods: {
    handleClick(file) {
      console.log(`你选择了: ${file.name}`);
      // 执行一些逻辑
    },
  },
};

export default {
  name: 'Files',
  components: {
    FileItem
  },
  data() {
    return {
      files: [],
    };
  },
  mounted() {
    axios.post('/Files', {jwt: "Bearer " + jwt})
        .then(response => {
          const res = response.data;
          const root = {
            id: 0,
            name: '根目录',
            isDirectory: true,
            children: [],
          };

          for (const filePath of res) {
            const fileParts = filePath.split('/');
            let currentDir = root;

            for (let i = 0; i < fileParts.length - 1; i++) {
              const directoryName = fileParts[i];
              let directory = currentDir.children.find(file => file.name === directoryName);

              if (!directory) {
                directory = {
                  id: currentDir.children.length + 1,
                  name: directoryName,
                  isDirectory: true,
                  children: [],
                };
                currentDir.children.push(directory);
              }

              currentDir = directory;
            }

            const fileName = fileParts[fileParts.length - 1];
            currentDir.children.push({
              id: currentDir.children.length + 1,
              name: fileName,
              isDirectory: false,
            });
          }

          this.files = root.children;
        })
        .catch(error => {
          console.error('获取数据失败:', error);
        });
  },
};
</script>

<style scoped>
.font-weight-bold {
  font-weight: bold;
}
</style>
