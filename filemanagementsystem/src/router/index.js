import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        name: 'Home',
        component: () => import(/* webpackChunkName: "home" */ '../views/Home.vue')
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import(/* webpackChunkName: "profile" */ '../views/Profile.vue')
    },
    {
        path: '/files',
        name: 'Files',
        component: () => import(/* webpackChunkName: "files" */ '../views/Files.vue')
    },
    {
        path: '/upload',
        name: 'Upload',
        component: () => import(/* webpackChunkName: "files" */ '../views/Upload.vue')
    },
    {
        path: '/edit',
        name: 'Edit',
        component: () => import(/* webpackChunkName: "files" */ '../views/Edit.vue')
    },
    {
        path: '/audio',
        name: 'Audio',
        component: () => import(/* webpackChunkName: "files" */ '../views/Audio.vue')
    },
    {
        path: '/video',
        name: 'Video',
        component: () => import(/* webpackChunkName: "files" */ '../views/Video.vue')
    },
    {
        path: '/log',
        name: 'Log',
        component: () => import(/* webpackChunkName: "files" */ '../views/Log.vue')
    },
    {
        path: '/settings',
        name: 'Settings',
        component: () => import(/* webpackChunkName: "files" */ '../views/Settings.vue')
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import(/* webpackChunkName: "files" */ '../Login.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import(/* webpackChunkName: "files" */ '../Register.vue')
    },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

export default router
