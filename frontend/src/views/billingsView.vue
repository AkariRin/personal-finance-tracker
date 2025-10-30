<template>
  <v-card-title class="text-h4 mb-6 text-grey-darken-3">
    <v-icon icon="mdi-receipt-text" class="mr-2" />
    Billings
  </v-card-title>
  <v-divider></v-divider>
  <v-card-text class="pa-6">
    <v-row class="mb-6">
      <v-col cols="12" md="4">
        <v-card class="pa-4" color="success" variant="tonal">
          <div class="d-flex align-center">
            <v-icon size="40" class="mr-3">mdi-arrow-down-circle</v-icon>
            <div>
              <div class="text-caption">Total Income</div>
              <div class="text-h5 font-weight-bold">¥{{ totalIncome.toFixed(2) }}</div>
            </div>
          </div>
        </v-card>
      </v-col>
      <v-col cols="12" md="4">
        <v-card class="pa-4" color="error" variant="tonal">
          <div class="d-flex align-center">
            <v-icon size="40" class="mr-3">mdi-arrow-up-circle</v-icon>
            <div>
              <div class="text-caption">Total Expense</div>
              <div class="text-h5 font-weight-bold">¥{{ totalExpense.toFixed(2) }}</div>
            </div>
          </div>
        </v-card>
      </v-col>
      <v-col cols="12" md="4">
        <v-card class="pa-4" color="primary" variant="tonal">
          <div class="d-flex align-center">
            <v-icon size="40" class="mr-3">mdi-receipt-text</v-icon>
            <div>
              <div class="text-caption">Total Bills</div>
              <div class="text-h5 font-weight-bold">{{ totalBills }}</div>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>
    <v-row class="mb-4">
      <v-col cols="12" md="4">
        <v-text-field
          v-model="searchQuery"
          label="Search counterparty or note"
          prepend-inner-icon="mdi-magnify"
          variant="underlined"
          density="comfortable"
          clearable
          hide-details
        ></v-text-field>
      </v-col>
      <v-col cols="12" md="3">
        <v-select
          v-model="filterType"
          :items="typeOptions"
          label="Bill Type"
          variant="underlined"
          density="comfortable"
          hide-details
        ></v-select>
      </v-col>
      <v-col cols="12" md="3">
        <v-select
          v-model="filterMethod"
          :items="methodOptions"
          label="Payment Method"
          variant="underlined"
          density="comfortable"
          hide-details
        ></v-select>
      </v-col>
      <v-col cols="12" md="2">
        <v-btn
          @click="openAddDialog"
          color="primary"
          block
          size="large"
        >
          <v-icon class="mr-2">mdi-plus</v-icon>
          Add Bill
        </v-btn>
      </v-col>
    </v-row>
    <v-data-table
      :headers="headers"
      :items="filteredBills"
      :loading="loading"
      :items-per-page="10"
      class="elevation-2"
    >
      <template #[`item.type`]="{ item }">
        <v-chip
          :color="item.type === 'in' ? 'success' : 'error'"
          size="small"
          variant="flat"
        >
          <v-icon start :icon="item.type === 'in' ? 'mdi-arrow-down' : 'mdi-arrow-up'"></v-icon>
          {{ item.type === 'in' ? 'Income' : 'Expense' }}
        </v-chip>
      </template>

      <template #[`item.amount`]="{ item }">
        <span :class="item.type === 'in' ? 'text-success' : 'text-error'" class="font-weight-bold">
          {{ item.type === 'in' ? '+' : '-' }}¥{{ item.amount.toFixed(2) }}
        </span>
      </template>

      <template #[`item.method`]="{ item }">
        <v-chip size="small" variant="text">
          <v-icon start :icon="getMethodIcon(item.method)"></v-icon>
          {{ getMethodLabel(item.method) }}
        </v-chip>
      </template>

      <template #[`item.time`]="{ item }">
        <div>
          <div class="text-body-2">{{ formatDate(item.time) }}</div>
          <div class="text-caption text-grey">{{ formatTime(item.time) }}</div>
        </div>
      </template>

      <template #[`item.category`]="{ item }">
        <v-chip v-if="item.category" size="small" color="primary" variant="tonal">
          {{ item.category }}
        </v-chip>
        <span v-else class="text-grey">-</span>
      </template>

      <template #[`item.note`]="{ item }">
        <div class="text-truncate" style="max-width: 200px;">
          {{ item.note || '-' }}
        </div>
      </template>

      <template #[`item.uuid`]="{ item }">
        <v-tooltip location="top">
          <template v-slot:activator="{ props }">
            <span v-bind="props" class="text-caption text-grey">
              {{ item.uuid.substring(0, 8) }}...
            </span>
          </template>
          <span>{{ item.uuid }}</span>
        </v-tooltip>
      </template>

      <template #[`item.actions`]="{ item }">
        <v-btn
          icon="mdi-eye"
          size="small"
          variant="text"
          @click="viewDetails(item)"
        ></v-btn>
      </template>

      <template v-slot:loading>
        <v-skeleton-loader type="table-row@5"></v-skeleton-loader>
      </template>

      <template v-slot:no-data>
        <div class="text-center pa-8">
          <v-icon size="64" color="grey-lighten-1">mdi-receipt-text-outline</v-icon>
          <div class="text-h6 text-grey mt-4">No billing records</div>
        </div>
      </template>
    </v-data-table>
    <v-dialog v-model="addDialog" max-width="700" persistent>
      <v-card>
        <v-card-title class="d-flex align-center bg-primary">
          <v-icon class="mr-2">mdi-plus-circle</v-icon>
          Add New Bill
          <v-spacer></v-spacer>
          <v-btn icon="mdi-close" variant="text" @click="closeAddDialog"></v-btn>
        </v-card-title>
        <v-card-text class="pa-6">
          <v-form ref="addForm" v-model="formValid">
            <v-row>
              <v-col cols="12" md="6">
                <v-select
                  v-model="newBill.type"
                  :items="typeOptionsForForm"
                  label="Bill Type *"
                  variant="underlined"
                  :rules="[v => !!v || 'Type is required']"
                  prepend-inner-icon="mdi-tag"
                ></v-select>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="newBill.amount"
                  label="Amount *"
                  variant="underlined"
                  type="number"
                  step="0.01"
                  min="0.01"
                  :rules="amountRules"
                  prepend-inner-icon="mdi-currency-cny"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-select
                  v-model="newBill.method"
                  :items="methodOptionsForForm"
                  label="Payment Method *"
                  variant="underlined"
                  :rules="[v => !!v || 'Payment method is required']"
                  prepend-inner-icon="mdi-credit-card"
                ></v-select>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newBill.counterparty"
                  label="Counterparty *"
                  variant="underlined"
                  :rules="counterpartyRules"
                  prepend-inner-icon="mdi-account"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newBill.category"
                  label="Category"
                  variant="underlined"
                  :rules="[v => !v || v.length <= 50 || 'Category cannot exceed 50 characters']"
                  prepend-inner-icon="mdi-shape"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newBill.time"
                  label="Transaction Time"
                  variant="underlined"
                  type="datetime-local"
                  prepend-inner-icon="mdi-clock-outline"
                  hint="Leave empty to use current time"
                  persistent-hint
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-textarea
                  v-model="newBill.note"
                  label="Note"
                  variant="underlined"
                  rows="3"
                  :rules="[v => !v || v.length <= 500 || 'Note cannot exceed 500 characters']"
                  prepend-inner-icon="mdi-note-text"
                ></v-textarea>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn @click="closeAddDialog" variant="text" size="large">
            Cancel
          </v-btn>
          <v-btn
            @click="submitNewBill"
            color="primary"
            variant="flat"
            size="large"
            :loading="submitting"
            :disabled="!formValid"
          >
            <v-icon class="mr-2">mdi-check</v-icon>
            Add Bill
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
      top
    >
      {{ snackbar.message }}
      <template v-slot:actions>
        <v-btn
          color="white"
          variant="text"
          @click="snackbar.show = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
    <v-dialog v-model="detailDialog" max-width="600">
      <v-card v-if="selectedBill">
        <v-card-title class="d-flex align-center bg-primary">
          <v-icon class="mr-2">mdi-receipt-text</v-icon>
          Bill Details
          <v-spacer></v-spacer>
          <v-btn icon="mdi-close" variant="text" @click="detailDialog = false"></v-btn>
        </v-card-title>
        <v-card-text class="pa-6">
          <v-list>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon>mdi-identifier</v-icon>
              </template>
              <v-list-item-title>Bill UUID</v-list-item-title>
              <v-list-item-subtitle class="text-wrap">{{ selectedBill.uuid }}</v-list-item-subtitle>
            </v-list-item>
            <v-divider></v-divider>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon :color="selectedBill.type === 'in' ? 'success' : 'error'">
                  {{ selectedBill.type === 'in' ? 'mdi-arrow-down-circle' : 'mdi-arrow-up-circle' }}
                </v-icon>
              </template>
              <v-list-item-title>Amount</v-list-item-title>
              <v-list-item-subtitle>
                <span :class="selectedBill.type === 'in' ? 'text-success' : 'text-error'" class="text-h6 font-weight-bold">
                  {{ selectedBill.type === 'in' ? '+' : '-' }}¥{{ selectedBill.amount.toFixed(2) }}
                </span>
              </v-list-item-subtitle>
            </v-list-item>
            <v-divider></v-divider>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon>mdi-tag</v-icon>
              </template>
              <v-list-item-title>Type</v-list-item-title>
              <v-list-item-subtitle>
                <v-chip :color="selectedBill.type === 'in' ? 'success' : 'error'" size="small">
                  {{ selectedBill.type === 'in' ? 'Income' : 'Expense' }}
                </v-chip>
              </v-list-item-subtitle>
            </v-list-item>
            <v-divider></v-divider>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon>{{ getMethodIcon(selectedBill.method) }}</v-icon>
              </template>
              <v-list-item-title>Payment Method</v-list-item-title>
              <v-list-item-subtitle>{{ getMethodLabel(selectedBill.method) }}</v-list-item-subtitle>
            </v-list-item>
            <v-divider></v-divider>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon>mdi-account</v-icon>
              </template>
              <v-list-item-title>Counterparty</v-list-item-title>
              <v-list-item-subtitle>{{ selectedBill.counterparty }}</v-list-item-subtitle>
            </v-list-item>
            <v-divider></v-divider>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon>mdi-clock-outline</v-icon>
              </template>
              <v-list-item-title>Transaction Time</v-list-item-title>
              <v-list-item-subtitle>{{ formatDateTime(selectedBill.time) }}</v-list-item-subtitle>
            </v-list-item>
            <v-divider></v-divider>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon>mdi-shape</v-icon>
              </template>
              <v-list-item-title>Category</v-list-item-title>
              <v-list-item-subtitle>{{ selectedBill.category || 'Uncategorized' }}</v-list-item-subtitle>
            </v-list-item>
            <v-divider></v-divider>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon>mdi-note-text</v-icon>
              </template>
              <v-list-item-title>Note</v-list-item-title>
              <v-list-item-subtitle class="text-wrap">{{ selectedBill.note || 'None' }}</v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-card-text>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserdataStore } from '@/stores/userdata'
import axios from 'axios'

const userStore = useUserdataStore()

const bills = ref([])
const loading = ref(false)
const searchQuery = ref('')
const filterType = ref('all')
const filterMethod = ref('all')
const detailDialog = ref(false)
const selectedBill = ref(null)

const addDialog = ref(false)
const addForm = ref(null)
const formValid = ref(false)
const submitting = ref(false)
const newBill = ref({
  type: '',
  amount: null,
  method: '',
  counterparty: '',
  category: '',
  time: '',
  note: ''
})

const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

const typeOptions = [
  { title: 'All', value: 'all' },
  { title: 'Income', value: 'in' },
  { title: 'Expense', value: 'out' }
]

const typeOptionsForForm = [
  { title: 'Income', value: 'in' },
  { title: 'Expense', value: 'out' }
]

const methodOptions = [
  { title: 'All', value: 'all' },
  { title: 'Cash', value: 'cash' },
  { title: 'WeChat', value: 'wx' },
  { title: 'Alipay', value: 'alipay' },
  { title: 'Bank Card', value: 'card' },
  { title: 'Bitcoin', value: 'btc' },
  { title: 'PayPal', value: 'paypal' },
  { title: 'Apple Pay', value: 'apple' }
]

const methodOptionsForForm = [
  { title: 'Cash', value: 'cash' },
  { title: 'WeChat', value: 'wx' },
  { title: 'Alipay', value: 'alipay' },
  { title: 'Bank Card', value: 'card' },
  { title: 'Bitcoin', value: 'btc' },
  { title: 'PayPal', value: 'paypal' },
  { title: 'Apple Pay', value: 'apple' }
]

const amountRules = [
  v => !!v || 'Amount is required',
  v => v > 0 || 'Amount must be greater than 0',
  v => /^\d+(\.\d{1,2})?$/.test(v) || 'Amount must have at most 2 decimal places'
]

const counterpartyRules = [
  v => !!v || 'Counterparty is required',
  v => (v && v.length <= 100) || 'Counterparty name cannot exceed 100 characters'
]

const headers = [
  { title: 'UUID', key: 'uuid', width: '100px' },
  { title: 'Type', key: 'type', width: '120px' },
  { title: 'Amount', key: 'amount', width: '140px' },
  { title: 'Payment Method', key: 'method', width: '140px' },
  { title: 'Counterparty', key: 'counterparty', width: '150px' },
  { title: 'Transaction Time', key: 'time', width: '140px' },
  { title: 'Category', key: 'category', width: '120px' },
  { title: 'Note', key: 'note', width: '200px' },
  { title: 'Actions', key: 'actions', sortable: false, width: '80px' }
]

const filteredBills = computed(() => {
  return bills.value.filter(bill => {
    const matchesSearch = !searchQuery.value ||
      bill.counterparty.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      (bill.note && bill.note.toLowerCase().includes(searchQuery.value.toLowerCase()))

    const matchesType = filterType.value === 'all' || bill.type === filterType.value
    const matchesMethod = filterMethod.value === 'all' || bill.method === filterMethod.value

    return matchesSearch && matchesType && matchesMethod
  })
})

const totalIncome = computed(() => {
  return filteredBills.value
    .filter(bill => bill.type === 'in')
    .reduce((sum, bill) => sum + parseFloat(bill.amount), 0)
})

const totalExpense = computed(() => {
  return filteredBills.value
    .filter(bill => bill.type === 'out')
    .reduce((sum, bill) => sum + parseFloat(bill.amount), 0)
})

const totalBills = computed(() => filteredBills.value.length)

const loadBills = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/bills', {
      headers: {
        Authorization: `Bearer ${userStore.token}`
      }
    })
    if (response.data.success) {
      bills.value = response.data.data
    }
  } catch (error) {
    console.error('Failed to load bills:', error)
  } finally {
    loading.value = false
  }
}

const viewDetails = (bill) => {
  selectedBill.value = bill
  detailDialog.value = true
}

const getMethodIcon = (method) => {
  const iconMap = {
    cash: 'mdi-cash',
    wx: 'mdi-wechat',
    alipay: 'mdi-alpha-a-circle',
    card: 'mdi-credit-card',
    btc: 'mdi-bitcoin',
    paypal: 'mdi-paypal',
    apple: 'mdi-apple'
  }
  return iconMap[method] || 'mdi-cash'
}

const getMethodLabel = (method) => {
  const labelMap = {
    cash: 'Cash',
    wx: 'WeChat',
    alipay: 'Alipay',
    card: 'Bank Card',
    btc: 'Bitcoin',
    paypal: 'PayPal',
    apple: 'Apple Pay'
  }
  return labelMap[method] || method
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

const formatTime = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const formatDateTime = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const openAddDialog = () => {
  addDialog.value = true
}

const closeAddDialog = () => {
  addDialog.value = false
  if (addForm.value) {
    addForm.value.reset()
  }
  newBill.value = {
    type: '',
    amount: null,
    method: '',
    counterparty: '',
    category: '',
    time: '',
    note: ''
  }
}

const submitNewBill = async () => {
  if (!addForm.value) return

  const { valid } = await addForm.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    const billData = {
      type: newBill.value.type,
      amount: newBill.value.amount,
      method: newBill.value.method,
      counterparty: newBill.value.counterparty,
      category: newBill.value.category || null,
      time: newBill.value.time || null,
      note: newBill.value.note || null
    }

    const response = await axios.post('/api/bills', billData, {
      headers: {
        Authorization: `Bearer ${userStore.token}`,
        'Content-Type': 'application/json'
      }
    })

    if (response.data.success) {
      await loadBills()
      closeAddDialog()
      snackbar.value = {
        show: true,
        message: 'Bill created successfully!',
        color: 'success'
      }
    }
  } catch (error) {
    console.error('Failed to create bill:', error)
    snackbar.value = {
      show: true,
      message: 'Failed to create bill: ' + (error.response?.data?.message || error.message),
      color: 'error'
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadBills()
})
</script>

<style scoped>
.text-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>

