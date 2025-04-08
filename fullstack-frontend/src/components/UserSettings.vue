<template>
  <div class="user-settings">
    <h1>{{ $t('userSettings.title') }}</h1>
    <form @submit.prevent="updateSettings">
      <div class="form-group">
        <label for="name">{{ $t('userSettings.nameLabel') }}</label>
        <input
          type="text"
          id="name"
          v-model="name"
          :placeholder="$t('userSettings.namePlaceholder')"
        />
      </div>
      <div class="form-group">
        <label for="email">{{ $t('userSettings.emailLabel') }}</label>
        <input
          type="email"
          id="email"
          v-model="email"
          :placeholder="$t('userSettings.emailPlaceholder')"
        />
      </div>
      <div class="form-group">
        <label for="password">{{ $t('userSettings.newPasswordLabel') }}</label>
        <input
          type="password"
          id="password"
          v-model="password"
          :placeholder="$t('userSettings.newPasswordPlaceholder')"
        />
      </div>
      <div class="form-group">
        <label for="confirmPassword">{{ $t('userSettings.confirmNewPasswordLabel') }}</label>
        <input
          type="password"
          id="confirmPassword"
          v-model="confirmPassword"
          :placeholder="$t('userSettings.confirmNewPasswordPlaceholder')"
        />
      </div>
      <button type="submit" class="submit-btn">{{ $t('userSettings.saveButton') }}</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useUpdateUserName } from '../api/user-profile/user-profile';
import { useUpdateUserEmail } from '../api/user-profile/user-profile';
import { useUpdateUserPassword } from '../api/user-profile/user-profile';
import type { UpdateUsernameRequest, UpdateEmailRequest, UpdatePasswordRequest } from '../api/model';
import type { AxiosError } from 'axios';

const { t } = useI18n();
const name = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');

const { mutate: updateName } = useUpdateUserName({
  mutation: {
    onSuccess: () => {
      console.log('Name updated successfully');
    },
    onError: (error: AxiosError) => {
      alert(error.response?.data || t('userSettings.errorUpdateName'));
    }
  }
});

const { mutate: updateEmail } = useUpdateUserEmail({
  mutation: {
    onSuccess: () => {
      console.log('Email updated successfully');
    },
    onError: (error: AxiosError) => {
      alert(error.response?.data || t('userSettings.errorUpdateEmail'));
    }
  }
});

const { mutate: updatePassword } = useUpdateUserPassword({
  mutation: {
    onSuccess: () => {
      console.log('Password updated successfully');
    },
    onError: (error: AxiosError) => {
      alert(error.response?.data || t('userSettings.errorUpdatePassword'));
    }
  }
});

const updateSettings = async () => {
  const token = localStorage.getItem('authToken');
  if (!token) {
    alert(t('createItemForm.errorNotLoggedIn'));
    window.location.href = '/login';
    return;
  }

  try {
    // Update name
    if (name.value) {
      const nameRequest: UpdateUsernameRequest = {
        username: name.value
      };
      updateName({ data: nameRequest });
    }

    // Update email
    if (email.value) {
      const emailRequest: UpdateEmailRequest = {
        email: email.value
      };
      updateEmail({ data: emailRequest });
    }

    // Update password
    if (password.value) {
      if (password.value !== confirmPassword.value) {
        alert(t('registerForm.errorPasswordMismatch'));
        return;
      }
      const passwordRequest: UpdatePasswordRequest = {
        password: password.value
      };
      updatePassword({ data: passwordRequest });
    }

    alert(t('userSettings.successMessage'));
  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.error('Failed to update settings:', error.response?.data || error.message);
    } else {
      console.error('Failed to update settings:', error);
    }
    alert(t('userSettings.errorDefault'));
  }
};
</script>

<style scoped>
.user-settings {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
  background-color: #f9f9f9;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: var(--border-radius);
  font-size: 1rem;
}

.submit-btn {
  display: block;
  width: 100%;
  padding: 0.75rem;
  background-color: var(--secondary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius);
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-btn:hover {
  background-color: #3aa876;
}
</style>