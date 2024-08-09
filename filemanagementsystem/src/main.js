import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import axios from 'axios';
import App from './App.vue';
import router from './router';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';

axios.defaults.baseURL = 'http://127.0.0.1:8080';

const app = createApp(App);

var jwt = localStorage.getItem("jwt")
// 添加全局前置守卫
router.beforeEach((to, from, next) => {
    if (to.path === '/login' || to.path === '/register') {
        next()
    } else {
        axios.post('/Auth', {jwt: "Bearer " + jwt})
            .then(response => {
                console.log(response.data);
                var code = response.data.code
                console.log(code);
                if (code !== 200) {
                    next('/login');
                } else {
                    localStorage.setItem("username", response.data.username)
                    console.log(localStorage.getItem("username"))
                    next();
                }

            })
            .catch(error => {
                console.error(error);
                next(false);
            });

    }
});

app.config.globalProperties.$http = axios;
app.use(router);
app.mount('#app');
