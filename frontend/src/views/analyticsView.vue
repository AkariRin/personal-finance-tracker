<template>
  <v-card-title class="text-h4 mb-6 text-grey-darken-3">
    <v-icon icon="mdi-chart-line" class="mr-8"></v-icon>
    Analytics
  </v-card-title>
  <v-divider></v-divider>
  <v-card-text class="pa-6">
    <v-row v-if="loading" class="justify-center my-12">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
        <div class="mt-4 text-h6">Loading analytics data...</div>
      </v-col>
    </v-row>
    <v-alert v-if="error" type="error" class="mb-6" closable @click:close="error = null">
      {{ error }}
    </v-alert>
    <div v-if="!loading && analyticsData">
      <v-row class="mb-6">
        <v-col cols="12" md="3">
          <v-card class="pa-4" color="success" variant="tonal">
            <div class="d-flex align-center">
              <v-icon size="40" class="mr-3">mdi-arrow-down-circle</v-icon>
              <div>
                <div class="text-caption">Total Income</div>
                <div class="text-h5 font-weight-bold">¥{{ formatNumber(analyticsData.totalIncome) }}</div>
              </div>
            </div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card class="pa-4" color="error" variant="tonal">
            <div class="d-flex align-center">
              <v-icon size="40" class="mr-3">mdi-arrow-up-circle</v-icon>
              <div>
                <div class="text-caption">Total Expense</div>
                <div class="text-h5 font-weight-bold">¥{{ formatNumber(analyticsData.totalExpense) }}</div>
              </div>
            </div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card class="pa-4" :color="analyticsData.balance >= 0 ? 'primary' : 'warning'" variant="tonal">
            <div class="d-flex align-center">
              <v-icon size="40" class="mr-3">mdi-wallet</v-icon>
              <div>
                <div class="text-caption">Balance</div>
                <div class="text-h5 font-weight-bold">¥{{ formatNumber(analyticsData.balance) }}</div>
              </div>
            </div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card class="pa-4" color="info" variant="tonal">
            <div class="d-flex align-center">
              <v-icon size="40" class="mr-3">mdi-receipt-text</v-icon>
              <div>
                <div class="text-caption">Total Bills</div>
                <div class="text-h5 font-weight-bold">{{ analyticsData.totalBills }}</div>
              </div>
            </div>
          </v-card>
        </v-col>
      </v-row>
      <v-row class="mb-4">
        <v-col cols="12" md="4">
          <v-select
            v-model="selectedMonths"
            :items="monthOptions"
            label="Time Period"
            variant="underlined"
            density="comfortable"
            @update:modelValue="loadAnalytics"
          ></v-select>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12">
          <v-card class="mb-6">
            <v-card-title class="text-h6">
              <v-icon icon="mdi-calendar-month" class="mr-2"></v-icon>
              Monthly Income & Expense Report
            </v-card-title>
            <v-divider></v-divider>
            <v-card-text>
              <v-table>
                <thead>
                  <tr>
                    <th class="text-left">Month</th>
                    <th class="text-right">Income</th>
                    <th class="text-right">Expense</th>
                    <th class="text-right">Balance</th>
                    <th class="text-center">Bills Count</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="stat in analyticsData.monthlyStats" :key="stat.month">
                    <td class="font-weight-medium">{{ stat.month }}</td>
                    <td class="text-right text-success">¥{{ formatNumber(stat.income) }}</td>
                    <td class="text-right text-error">¥{{ formatNumber(stat.expense) }}</td>
                    <td class="text-right" :class="stat.balance >= 0 ? 'text-primary' : 'text-warning'">
                      ¥{{ formatNumber(stat.balance) }}
                    </td>
                    <td class="text-center">
                      <v-chip size="small" color="primary" variant="tonal">
                        {{ stat.incomeCount }} / {{ stat.expenseCount }}
                      </v-chip>
                    </td>
                  </tr>
                </tbody>
              </v-table>
              <div class="mt-6">
                <div v-for="stat in analyticsData.monthlyStats" :key="stat.month" class="mb-4">
                  <div class="text-subtitle-2 mb-2">{{ stat.month }}</div>
                  <div class="d-flex align-center">
                    <div style="width: 60px" class="text-caption">Income:</div>
                    <v-progress-linear
                      :model-value="getPercentage(stat.income, maxMonthlyAmount)"
                      color="success"
                      height="20"
                      class="flex-grow-1"
                    >
                      <template v-slot:default>
                        <strong class="text-white text-caption">¥{{ formatNumber(stat.income) }}</strong>
                      </template>
                    </v-progress-linear>
                  </div>
                  <div class="d-flex align-center mt-1">
                    <div style="width: 60px" class="text-caption">Expense:</div>
                    <v-progress-linear
                      :model-value="getPercentage(stat.expense, maxMonthlyAmount)"
                      color="error"
                      height="20"
                      class="flex-grow-1"
                    >
                      <template v-slot:default>
                        <strong class="text-white text-caption">¥{{ formatNumber(stat.expense) }}</strong>
                      </template>
                    </v-progress-linear>
                  </div>
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" md="6">
          <v-card>
            <v-card-title class="text-h6">
              <v-icon icon="mdi-credit-card-multiple" class="mr-2"></v-icon>
              Payment Method Statistics
            </v-card-title>
            <v-divider></v-divider>
            <v-card-text>
              <div v-if="analyticsData.paymentMethodStats.length === 0" class="text-center py-8 text-grey">
                No payment method data available
              </div>
              <div v-else>
                <v-list>
                  <v-list-item
                    v-for="method in analyticsData.paymentMethodStats"
                    :key="method.method"
                    class="px-0"
                  >
                    <template v-slot:prepend>
                      <v-icon :icon="getMethodIcon(method.method)" class="mr-3"></v-icon>
                    </template>
                    <v-list-item-title>
                      <div class="d-flex justify-space-between align-center">
                        <span class="font-weight-medium">{{ getMethodName(method.method) }}</span>
                        <span class="text-primary">¥{{ formatNumber(method.totalAmount) }}</span>
                      </div>
                    </v-list-item-title>
                    <v-list-item-subtitle>
                      <v-progress-linear
                        :model-value="method.percentage"
                        color="primary"
                        height="8"
                        class="my-2"
                      ></v-progress-linear>
                      <div class="d-flex justify-space-between text-caption">
                        <span>{{ method.count }} transactions</span>
                        <span>{{ method.percentage }}%</span>
                      </div>
                    </v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
        <v-col cols="12" md="6">
          <v-card>
            <v-card-title class="text-h6">
              <v-icon icon="mdi-shape" class="mr-2"></v-icon>
              Expense Category Statistics
            </v-card-title>
            <v-divider></v-divider>
            <v-card-text>
              <div v-if="analyticsData.categoryStats.length === 0" class="text-center py-8 text-grey">
                No category data available
              </div>
              <div v-else>
                <v-list>
                  <v-list-item
                    v-for="category in analyticsData.categoryStats"
                    :key="category.category"
                    class="px-0"
                  >
                    <template v-slot:prepend>
                      <v-icon :icon="getCategoryIcon(category.category)" class="mr-3"></v-icon>
                    </template>
                    <v-list-item-title>
                      <div class="d-flex justify-space-between align-center">
                        <span class="font-weight-medium">{{ getCategoryName(category.category) }}</span>
                        <span class="text-error">¥{{ formatNumber(category.totalAmount) }}</span>
                      </div>
                    </v-list-item-title>
                    <v-list-item-subtitle>
                      <v-progress-linear
                        :model-value="category.percentage"
                        color="error"
                        height="8"
                        class="my-2"
                      ></v-progress-linear>
                      <div class="d-flex justify-space-between text-caption">
                        <span>{{ category.count }} transactions</span>
                        <span>{{ category.percentage }}%</span>
                      </div>
                    </v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </v-card-text>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useUserdataStore } from '@/stores/userdata'
import { useRouter } from 'vue-router'

const userStore = useUserdataStore()
const router = useRouter()

const loading = ref(false)
const error = ref(null)
const analyticsData = ref(null)
const selectedMonths = ref(6)

const monthOptions = [
  { title: 'Last 3 Months', value: 3 },
  { title: 'Last 6 Months', value: 6 },
  { title: 'Last 12 Months', value: 12 },
  { title: 'All Time', value: 999 }
]

const methodNames = {
  cash: 'Cash',
  wx: 'WeChat Pay',
  alipay: 'Alipay',
  card: 'Credit/Debit Card',
  btc: 'Bitcoin',
  paypal: 'PayPal',
  apple: 'Apple Pay'
}

const methodIcons = {
  cash: 'mdi-cash',
  wx: 'mdi-wechat',
  alipay: 'mdi-alpha-a-circle',
  card: 'mdi-credit-card',
  btc: 'mdi-bitcoin',
  paypal: 'mdi-paypal',
  apple: 'mdi-apple'
}

const categoryNames = {
  food: 'Food & Dining',
  transport: 'Transportation',
  shopping: 'Shopping',
  entertainment: 'Entertainment',
  health: 'Health & Medical',
  education: 'Education',
  housing: 'Housing',
  utilities: 'Utilities',
  other: 'Other'
}

const categoryIcons = {
  food: 'mdi-food',
  transport: 'mdi-car',
  shopping: 'mdi-shopping',
  entertainment: 'mdi-movie',
  health: 'mdi-hospital-box',
  education: 'mdi-school',
  housing: 'mdi-home',
  utilities: 'mdi-lightning-bolt',
  other: 'mdi-dots-horizontal'
}

const maxMonthlyAmount = computed(() => {
  if (!analyticsData.value || !analyticsData.value.monthlyStats.length) return 0
  return Math.max(
    ...analyticsData.value.monthlyStats.map(stat =>
      Math.max(parseFloat(stat.income), parseFloat(stat.expense))
    )
  )
})

const loadAnalytics = async () => {
  if (!userStore.isAuthenticated) {
    router.push('/login')
    return
  }

  loading.value = true
  error.value = null

  try {
    const response = await axios.get('/api/analytics', {
      params: {
        months: selectedMonths.value
      },
      headers: {
        Authorization: `Bearer ${userStore.token}`
      }
    })

    if (response.data.success) {
      analyticsData.value = response.data.data
    } else {
      error.value = response.data.message || 'Failed to load analytics data'
    }
  } catch (err) {
    console.error('Error loading analytics:', err)
    error.value = err.response?.data?.message || 'Failed to load analytics data'

    if (err.response?.status === 401) {
      userStore.logout()
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}

const formatNumber = (value) => {
  if (!value) return '0.00'
  return parseFloat(value).toFixed(2)
}

const getPercentage = (value, max) => {
  if (!max || max === 0) return 0
  return (parseFloat(value) / max) * 100
}

const getMethodName = (method) => {
  return methodNames[method] || method
}

const getMethodIcon = (method) => {
  return methodIcons[method] || 'mdi-cash'
}

const getCategoryName = (category) => {
  return categoryNames[category] || category
}

const getCategoryIcon = (category) => {
  return categoryIcons[category] || 'mdi-dots-horizontal'
}

onMounted(() => {
  loadAnalytics()
})
</script>
