import { createRouter, createWebHistory } from 'vue-router'
import CalculatorView from "@/views/CalculatorView.vue"
import FeedbackView from "@/views/FeedbackView.vue"
import LoginView from "@/views/LoginView.vue"

const routes = [
  {
    path: '/',
    name: 'Calculator',
    component: CalculatorView
  },
  {
    path: '/feedback',
    name: 'Feedback',
    component: FeedbackView
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router