<template>
    <v-card-title class="text-h4 mb-6 text-grey-darken-3">
      <v-icon icon="mdi-cog" class="mr-8"></v-icon>
      User Settings
    </v-card-title>
    <v-divider></v-divider>
    <v-card-text class="my-8">
      <div class="d-flex align-center mb-10">
        <div class="text-subtitle-1 text-grey-darken-2" style="width: 120px;">ID</div>
        <div class="text-h5 text-grey-darken-3">{{ id }}</div>
      </div>
      <div class="d-flex align-center">
        <div class="text-subtitle-1 text-grey-darken-2" style="width: 120px;">Username</div>
        <div class="text-h5 text-grey-darken-3">{{ username }}</div>
      </div>
    </v-card-text>
    <v-divider></v-divider>
    <v-card-actions class="d-flex my-8 ga-6">
      <v-btn color="primary" variant="elevated" prepend-icon="mdi-account-edit" @click="openUsernameDialog">
        Change Username
      </v-btn>
      <v-btn color="warning" variant="elevated" prepend-icon="mdi-lock-reset" @click="openPasswordDialog">
        Change Password
      </v-btn>
      <v-btn color="error" variant="elevated" prepend-icon="mdi-account-remove" @click="openDeleteDialog">
        Delete Account
      </v-btn>
    </v-card-actions>

    <v-dialog v-model="usernameDialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5">Change Name</v-card-title>
        <v-card-text>
          <v-form ref="usernameFormRef">
            <v-text-field
              v-model="newUsername"
              label="New Name"
              variant="underlined"
              prepend-icon="mdi-account"
              :rules="nameRules"
              :disabled="loading"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="closeUsernameDialog" :disabled="loading">Cancel</v-btn>
          <v-btn color="primary" variant="elevated" @click="changeUsername" :loading="loading">Confirm</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="passwordDialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5">Change Password</v-card-title>
        <v-card-text>
          <v-form ref="passwordFormRef">
            <v-text-field
              v-model="currentPassword"
              label="Current Password"
              type="password"
              variant="underlined"
              prepend-icon="mdi-lock"
              :rules="passwordRequiredRules"
              :disabled="loading"
              class="mb-2"
            ></v-text-field>
            <v-text-field
              v-model="newPassword"
              label="New Password"
              type="password"
              variant="underlined"
              prepend-icon="mdi-lock-reset"
              :rules="passwordRules"
              :disabled="loading"
              class="mb-2"
            ></v-text-field>
            <v-text-field
              v-model="confirmPassword"
              label="Confirm New Password"
              type="password"
              variant="underlined"
              prepend-icon="mdi-lock-check"
              :rules="confirmPasswordRules"
              :disabled="loading"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="closePasswordDialog" :disabled="loading">Cancel</v-btn>
          <v-btn color="warning" variant="elevated" @click="changePassword" :loading="loading">Confirm</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="deleteDialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5 text-error">Delete Account</v-card-title>
        <v-card-text>
          <v-alert type="warning" variant="tonal" class="mb-4">
            This action cannot be undone!
          </v-alert>
          <p class="text-body-1">
            Are you sure you want to delete your account? All your data will be permanently removed.
          </p>
          <v-form ref="deleteFormRef">
            <v-text-field
              v-model="deleteConfirmation"
              label="Type your username to confirm"
              variant="underlined"
              prepend-icon="mdi-alert"
              :rules="usernameMatchRules"
              :disabled="loading"
              class="mt-4"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="closeDeleteDialog" :disabled="loading">Cancel</v-btn>
          <v-btn color="error" variant="elevated" @click="deleteAccount" :loading="loading">
            Delete Account
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

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
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserdataStore } from '@/stores/userdata'
import { useRouter } from 'vue-router'
import axios from 'axios'

const user = useUserdataStore()
const router = useRouter()

const id = computed(() => user.id)
const username = computed(() => user.name)

const usernameDialog = ref(false)
const passwordDialog = ref(false)
const deleteDialog = ref(false)

const newUsername = ref('')
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const deleteConfirmation = ref('')

const loading = ref(false)
const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

const usernameFormRef = ref(null)
const passwordFormRef = ref(null)
const deleteFormRef = ref(null)

const nameRules = [
  v => !!v || 'Display name cannot be empty',
  v => (v && v.length >= 1 && v.length <= 50) || 'Display name must be between 1-50 characters'
]

const passwordRequiredRules = [
  v => !!v || 'Current password cannot be empty'
]

const passwordRules = [
  v => !!v || 'Password cannot be empty',
  v => (v && v.length >= 4 && v.length <= 32) || 'Password must be between 4-32 characters'
]

const confirmPasswordRules = [
  v => v === newPassword.value || 'The passwords entered twice do not match'
]

const usernameMatchRules = [
  v => v === id.value || 'Username does not match'
]

const openUsernameDialog = () => {
  newUsername.value = username.value
  usernameDialog.value = true
}

const closeUsernameDialog = () => {
  usernameDialog.value = false
  newUsername.value = ''
  if (usernameFormRef.value) {
    usernameFormRef.value.reset()
  }
}

const openPasswordDialog = () => {
  passwordDialog.value = true
}

const closePasswordDialog = () => {
  passwordDialog.value = false
  currentPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.reset()
  }
}

const openDeleteDialog = () => {
  deleteDialog.value = true
}

const closeDeleteDialog = () => {
  deleteDialog.value = false
  deleteConfirmation.value = ''
  if (deleteFormRef.value) {
    deleteFormRef.value.reset()
  }
}

const changeUsername = async () => {
  const { valid } = await usernameFormRef.value.validate()
  if (!valid) {
    return
  }

  loading.value = true

  try {
    const response = await axios.put(
      '/api/user/name',
      { name: newUsername.value },
      {
        headers: {
          'Authorization': `Bearer ${user.token}`
        }
      }
    )

    if (response.data.success) {
      user.updateName(response.data.data)
      snackbarText.value = 'Name updated successfully'
      snackbarColor.value = 'success'
      snackbar.value = true
      setTimeout(() => {
        closeUsernameDialog()
      }, 1000)
    } else {
      snackbarText.value = response.data.message || 'Update failed'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error) {
    console.error('Change name error:', error)
    snackbarText.value = error.response?.data?.message || 'Update failed, please try again'
    snackbarColor.value = 'error'
    snackbar.value = true
  } finally {
    loading.value = false
  }
}

const changePassword = async () => {
  const { valid } = await passwordFormRef.value.validate()
  if (!valid) {
    return
  }

  loading.value = true

  try {
    const response = await axios.put(
      '/api/user/password',
      {
        currentPassword: currentPassword.value,
        newPassword: newPassword.value
      },
      {
        headers: {
          'Authorization': `Bearer ${user.token}`
        }
      }
    )

    if (response.data.success) {
      snackbarText.value = 'Password updated successfully'
      snackbarColor.value = 'success'
      snackbar.value = true
      setTimeout(() => {
        closePasswordDialog()
      }, 1000)
    } else {
      snackbarText.value = response.data.message || 'Update failed'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error) {
    console.error('Change password error:', error)
    snackbarText.value = error.response?.data?.message || 'Update failed, please try again'
    snackbarColor.value = 'error'
    snackbar.value = true
  } finally {
    loading.value = false
  }
}

const deleteAccount = async () => {
  const { valid } = await deleteFormRef.value.validate()
  if (!valid) {
    return
  }

  loading.value = true

  try {
    const response = await axios.delete(
      '/api/user/account',
      {
        data: { username: deleteConfirmation.value },
        headers: {
          'Authorization': `Bearer ${user.token}`
        }
      }
    )

    if (response.data.success) {
      snackbarText.value = 'Account deleted successfully, logging out...'
      snackbarColor.value = 'success'
      snackbar.value = true
      setTimeout(() => {
        user.logout()
        router.push('/login')
      }, 1500)
    } else {
      snackbarText.value = response.data.message || 'Delete failed'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error) {
    console.error('Delete account error:', error)
    snackbarText.value = error.response?.data?.message || 'Delete failed, please try again'
    snackbarColor.value = 'error'
    snackbar.value = true
  } finally {
    loading.value = false
  }
}
</script>

