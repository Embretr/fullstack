<template>
    <form @submit.prevent="handleRegister" class="register-form">
      <div class="form-group">
        <label for="name">{{ $t('registerForm.fullNameLabel') }}</label>
        <input type="text" id="name" v-model="name" required :placeholder="$t('registerForm.fullNamePlaceholder')" />
      </div>
      <div class="form-group">
        <label for="email">{{ $t('registerForm.emailLabel') }}</label>
        <input type="email" id="email" v-model="email" required :placeholder="$t('registerForm.emailPlaceholder')" />
      </div>
      <div class="form-group">
        <label for="password">{{ $t('registerForm.passwordLabel') }}</label>
        <input type="password" id="password" v-model="password" required :placeholder="$t('registerForm.passwordPlaceholder')" />
      </div>
      <div class="form-group">
        <label for="confirmPassword">{{ $t('registerForm.confirmPasswordLabel') }}</label>
        <input type="password" id="confirmPassword" v-model="confirmPassword" required :placeholder="$t('registerForm.confirmPasswordPlaceholder')" />
      </div>
      <button type="submit" class="submit-btn">{{ $t('registerForm.registerButton') }}</button>
    </form>
    <p class="login-link">
      {{ $t('registerForm.alreadyHaveAccount') }}<router-link to="/login">{{ $t('navbar.login') }}</router-link>
    </p>
  </template>

  <script setup lang="ts">
  import { ref } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { useRegister } from '../api/authentication/authentication'
  import { useRouter } from 'vue-router'
  import type { User } from '../api/model'

  const { t } = useI18n()
  const router = useRouter()
  const name = ref('')
  const email = ref('')
  const password = ref('')
  const confirmPassword = ref('')
  const message = ref('')

  const { mutate: registerUser } = useRegister({
    mutation: {
      onSuccess: (data) => {
        message.value = data.data || t('registerForm.successMessage')
        console.log('Register attempt successful:', data.data)
        alert(message.value)
        router.push('/login')
      },
      onError: (error) => {
        const errorMsg = error.response?.data || t('registerForm.errorDefault')
        alert(errorMsg)
      }
    }
  })

  const handleRegister = async () => {
    if (password.value !== confirmPassword.value) {
      alert(t('registerForm.errorPasswordMismatch'))
      return
    }
    
    const user: User = {
      username: name.value,
      email: email.value,
      password: password.value,
    }
    
    registerUser({ data: user })
  }
  </script>

<style scoped>
  .register-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  label {
    color: var(--primary-color);
    font-weight: bold;
  }

  input {
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: var(--border-radius);
    font-size: 1rem;
  }

  .submit-btn {
    background-color: var(--secondary-color);
    color: white;
    padding: 0.75rem;
    border: none;
    border-radius: var(--border-radius);
    font-size: 1rem;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .submit-btn:hover {
    background-color: #3aa876;
  }

  .login-link {
    text-align: center;
    margin-top: 1.5rem;
    color: #666;
  }

  .login-link a {
    color: var(--secondary-color);
    text-decoration: none;
  }

  .login-link a:hover {
    text-decoration: underline;
  }
</style>