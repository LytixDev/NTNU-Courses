import { createRouter, createWebHistory } from 'vue-router'
import CalculatorView from "@/views/CalculatorView.vue"
import FeedbackView from "@/views/FeedbackView.vue"

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
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router