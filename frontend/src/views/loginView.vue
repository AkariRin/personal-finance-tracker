<template>
  <v-container fluid class="fill-height">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card elevation="12" class="pa-4">
          <v-card-title class="text-h4 text-center mb-4">
            Sign In
          </v-card-title>
          <v-card-text>
            <v-form ref="formRef">
              <v-text-field
                v-model="username"
                label="Username"
                prepend-inner-icon="mdi-account"
                type="text"
                variant="underlined"
                :rules="usernameRules"
              ></v-text-field>
              <v-text-field
                v-model="password"
                label="Password"
                prepend-inner-icon="mdi-lock"
                :type="showPassword ? 'text' : 'password'"
                :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showPassword = !showPassword"
                variant="underlined"
                :rules="passwordRules"
              ></v-text-field>
              <v-btn
                block
                color="primary"
                size="large"
                variant="elevated"
                @click="handleLogin"
                :loading="loading"
              >
                Sign In
              </v-btn>
              <v-divider class="my-4"></v-divider>
              <v-row>
                <v-col cols="12" class="text-center">
                  <span class="text-body-2">Don't have an account yet?</span>
                  <RouterLink to="/register" class="text-primary ms-1">Sign up</RouterLink>
                </v-col>
              </v-row>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <v-snackbar
      v-model="snackbar"
      :color="snackbarColor"
      :timeout="3000"
      location="top"
    >
      {{ snackbarText }}
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="snackbar = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserdataStore } from '@/stores/userdata'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const user = useUserdataStore()

const username = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const formRef = ref(null)
const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

const usernameRules = [
  v => !!v || 'Username cannot be empty',
  v => (v && v.length >= 2 && v.length <= 16) || 'Username must be between 2-16 characters'
]
const passwordRules = [
  v => !!v || 'Password cannot be empty',
  v => (v && v.length >= 4 && v.length <= 32) || 'Password must be between 4-32 characters'
]

const handleLogin = async () => {
  const { valid } = await formRef.value.validate()
  if (!valid) {
    return
  }

  loading.value = true
  try {
    const response = await axios.post('/api/auth/login', {
      username: username.value,
      password: password.value
    })

    if (response.data.success && response.data.data) {
      const { token, username, name, balance } = response.data.data

      user.login(token, username, name, balance)

      snackbarText.value = 'Login successful!'
      snackbarColor.value = 'success'
      snackbar.value = true

      // 登录成功后跳转到redirect参数指定的页面，如果没有则跳转到首页
      const redirectPath = route.query.redirect || '/'
      setTimeout(() => {
        router.push(redirectPath)
      }, 2000)
    } else {
      snackbarText.value = response.data.message || 'Login failed'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error) {
    console.error('Login error:', error)

    if (error.response) {
      snackbarText.value = error.response.data?.message || 'Invalid username or password'
      snackbarColor.value = 'error'
    } else if (error.request) {
      snackbarText.value = 'Unable to connect to server'
      snackbarColor.value = 'error'
    } else {
      snackbarText.value = 'Login failed, please try again'
      snackbarColor.value = 'error'
    }
    snackbar.value = true
  } finally {
    loading.value = false
  }
}
</script>
