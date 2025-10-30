<template>
    <v-card-title class="pa-6" style="border-bottom: 1px solid rgba(0, 0, 0, 0.08);">
      <div class="text-h4 text-grey-darken-3 font-weight-medium">
        {{ greeting }}, {{ username }}
      </div>
      <div class="text-subtitle-1 text-grey-darken-2 mt-2">
        {{ currentDate }}
      </div>
    </v-card-title>
    <v-card-text class="pa-6">
      <v-card
        class="elevation-2 rounded-lg"
        style="background: linear-gradient(135deg, #ffeef8 0%, #ffb3d9 100%); box-shadow: 0 8px 32px rgba(255, 179, 217, 0.4);">
        <v-card-text class="pa-8">
          <div class="text-subtitle-2 text-grey-darken-1 mb-2">
            Total assets
          </div>
          <div class="text-h3 font-weight-bold mb-6 text-grey-darken-3">
            ¥{{ formattedBalance }}
          </div>
          <v-divider class="my-4 opacity-30"></v-divider>
          <v-row class="mt-4">
            <v-col cols="6">
              <div class="text-center">
                <div class="text-caption text-grey-darken-2 mb-1">Today's income</div>
                <div class="text-h6 font-weight-medium text-grey-darken-3">¥{{ formattedTodayIncome }}</div>
              </div>
            </v-col>
            <v-col cols="6">
              <div class="text-center">
                <div class="text-caption text-grey-darken-2 mb-1">Today's spending</div>
                <div class="text-h6 font-weight-medium text-grey-darken-3">¥{{ formattedTodayExpense }}</div>
              </div>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
      <div class="mt-8">
        <div class="d-flex justify-space-between align-center mb-4">
          <div class="text-h6 font-weight-medium text-grey-darken-3">Billing Summary</div>
          <v-btn variant="text" size="small" color="primary" to="/billings">View All</v-btn>
        </div>
        <v-row>
          <v-col cols="12" md="4">
            <v-card class="pa-4" color="success" variant="tonal">
              <div class="text-caption mb-1">Total Income</div>
              <div class="text-h5 font-weight-bold">¥{{ totalIncome.toFixed(2) }}</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="4">
            <v-card class="pa-4" color="error" variant="tonal">
              <div class="text-caption mb-1">Total Expense</div>
              <div class="text-h5 font-weight-bold">¥{{ totalExpense.toFixed(2) }}</div>
            </v-card>
          </v-col>
          <v-col cols="12" md="4">
            <v-card class="pa-4" color="info" variant="tonal">
              <div class="text-caption mb-1">Total Billings</div>
              <div class="text-h5 font-weight-bold">{{ totalBillingsCount }}</div>
            </v-card>
          </v-col>
        </v-row>
      </div>
      <div class="mt-8">
        <div class="d-flex justify-space-between align-center mb-4">
          <div class="text-h6 font-weight-medium text-grey-darken-3">Recent Transactions</div>
          <v-btn variant="text" size="small" color="primary" to="/billings">View All</v-btn>
        </div>
        <v-card class="rounded-lg" outlined style="overflow: hidden;">
          <v-list lines="two">
            <v-list-item
              v-for="(transaction, index) in recentTransactions"
              :key="index"
              class="px-4"
            >
              <template v-slot:prepend>
                <v-avatar :color="getCategoryColor(transaction.category)" class="mr-3">
                  <v-icon :icon="getCategoryIcon(transaction.category)" color="white"></v-icon>
                </v-avatar>
              </template>
              <v-list-item-title class="font-weight-medium">
                {{ transaction.category || 'Uncategorized' }}
              </v-list-item-title>
              <v-list-item-subtitle>
                {{ formatDate(transaction.time) }}
              </v-list-item-subtitle>
              <template v-slot:append>
                <div :class="transaction.type === 'in' ? 'text-green-darken-1' : 'text-red-darken-1'" class="text-h6 font-weight-medium">
                  {{ transaction.type === 'in' ? '+' : '-' }}¥{{ transaction.amount }}
                </div>
              </template>
            </v-list-item>
            <v-list-item v-if="recentTransactions.length === 0" class="text-center py-8">
              <v-list-item-title class="text-grey">
                No transaction records
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>
      </div>
    </v-card-text>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserdataStore } from '@/stores/userdata'
import axios from 'axios'

const user = useUserdataStore()
const username = computed(() => user.name)
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 7) return 'Good Night'
  if (hour < 12) return 'Good Morning'
  if (hour < 17) return 'Good Afternoon'
  if (hour < 22) return 'Good Evening'
  return 'Good Night'
})

const currentDate = computed(() => {
  const now = new Date()
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  return now.toLocaleDateString('en', options)
})

const formattedBalance = computed(() => {
  return user.balance.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
})

const todayIncome = ref(0)
const todayExpense = ref(0)
const recentTransactions = ref([])
const allBills = ref([])

const formattedTodayIncome = computed(() => {
  return todayIncome.value.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
})

const formattedTodayExpense = computed(() => {
  return todayExpense.value.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
})

// 账单统计计算属性
const totalIncome = computed(() => {
  return allBills.value
    .filter(b => b.type === 'in')
    .reduce((sum, b) => sum + parseFloat(b.amount), 0)
})

const totalExpense = computed(() => {
  return allBills.value
    .filter(b => b.type === 'out')
    .reduce((sum, b) => sum + parseFloat(b.amount), 0)
})

const totalBillingsCount = computed(() => allBills.value.length)

const getCategoryIcon = (category) => {
  const iconMap = {
    '餐饮美食': 'mdi-silverware-fork-knife',
    '购物': 'mdi-cart',
    '交通': 'mdi-car',
    '娱乐': 'mdi-movie',
    '医疗': 'mdi-medical-bag',
    '教育': 'mdi-school',
    '住房': 'mdi-home',
    '通讯': 'mdi-phone',
    '其他': 'mdi-dots-horizontal',
    '工资': 'mdi-cash',
    '奖金': 'mdi-gift',
    '投资': 'mdi-chart-line',
  }
  return iconMap[category] || 'mdi-cash-multiple'
}

const getCategoryColor = (category) => {
  const colorMap = {
    '餐饮美食': 'orange',
    '购物': 'pink',
    '交通': 'blue',
    '娱乐': 'purple',
    '医疗': 'red',
    '教育': 'green',
    '住房': 'brown',
    '通讯': 'cyan',
    '其他': 'grey',
    '工资': 'green',
    '奖金': 'amber',
    '投资': 'indigo',
  }
  return colorMap[category] || 'primary'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const fetchDashboardData = async () => {
  try {
    const response = await axios.get('/api/dashboard', {
      headers: {
        'Authorization': `Bearer ${user.token}`
      }
    })

    if (response.data.success) {
      const data = response.data.data
      // 更新store中的用户余额
      user.balance = data.totalAssets
      todayIncome.value = data.todayIncome
      todayExpense.value = data.todayExpense
      recentTransactions.value = data.recentBills
    }
  } catch (error) {
    console.error('Failed to fetch dashboard data:', error)
  }
}

const fetchAllBills = async () => {
  try {
    const response = await axios.get('/api/bills', {
      headers: {
        'Authorization': `Bearer ${user.token}`
      }
    })

    if (response.data.success) {
      allBills.value = response.data.data
    }
  } catch (error) {
    console.error('Failed to fetch all bills:', error)
  }
}

onMounted(() => {
  fetchDashboardData()
  fetchAllBills()
})
</script>
