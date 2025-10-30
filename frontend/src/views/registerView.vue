<template>
  <v-container fluid class="fill-height">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card elevation="12" class="pa-4">
          <v-card-title class="text-h4 text-center mb-4">
            Create Account
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
                hint="Used for login (unique identifier)"
              ></v-text-field>
              <v-text-field
                v-model="name"
                label="Display Name"
                prepend-inner-icon="mdi-account-circle"
                type="text"
                variant="underlined"
                :rules="nameRules"
                hint="Your name shown in the app"
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
              <v-text-field
                v-model="confirmPassword"
                label="Confirm Password"
                prepend-inner-icon="mdi-lock-check"
                :type="showConfirmPassword ? 'text' : 'password'"
                :append-inner-icon="showConfirmPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showConfirmPassword = !showConfirmPassword"
                variant="underlined"
                :rules="confirmPasswordRules"
              ></v-text-field>
              <v-btn
                block
                color="primary"
                size="large"
                variant="elevated"
                @click="handleRegister"
                :loading="loading"
              >
                Register
              </v-btn>
              <v-divider class="my-4"></v-divider>
              <v-row>
                <v-col cols="12" class="text-center">
                  <span class="text-body-2">Already have an account?</span>
                  <router-link to="/login" class="text-primary ms-1">Sign in</router-link>
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
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const username = ref('')
const name = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const loading = ref(false)
const formRef = ref(null)

const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

const usernameRules = [
  v => !!v || 'Username cannot be empty',
  v => (v && v.length >= 2 && v.length <= 16) || 'Username must be between 2-16 characters'
]
const nameRules = [
  v => !!v || 'Display name cannot be empty',
  v => (v && v.length >= 1 && v.length <= 50) || 'Display name must be between 1-50 characters'
]
const passwordRules = [
  v => !!v || 'Password cannot be empty',
  v => (v && v.length >= 4 && v.length <= 32) || 'Password must be between 4-32 characters'
]
const confirmPasswordRules = [
  v => v === password.value || 'The passwords entered twice do not match'
]

const handleRegister = async () => {
  const { valid } = await formRef.value.validate()
  if (!valid) {
    return
  }

  loading.value = true
  try {
    const response = await axios.post('/api/auth/register', {
      username: username.value,
      name: name.value,
      password: password.value
    })

    if (response.data.success) {
      snackbarText.value = 'Registration successful! Redirecting to login...'
      snackbarColor.value = 'success'
      snackbar.value = true

      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      snackbarText.value = response.data.message || 'Registration failed'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error) {
    if (error.response) {
      snackbarText.value = error.response.data?.message || 'Registration failed'
      snackbarColor.value = 'error'
    } else if (error.request) {
      snackbarText.value = 'Unable to connect to server'
      snackbarColor.value = 'error'
    } else {
      snackbarText.value = 'Registration failed, please try again'
      snackbarColor.value = 'error'
    }
    snackbar.value = true
  } finally {
    loading.value = false
  }
}
</script>
