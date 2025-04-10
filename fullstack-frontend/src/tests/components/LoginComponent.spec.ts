import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import { createI18n } from 'vue-i18n';
import LoginForm from '@/components/LoginComponent.vue';
import axios from 'axios';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

function flushPromises() {
  return new Promise(resolve => setTimeout(resolve, 0));
}

const messages = {
  en: {
    loginForm: {
      emailLabel: 'Email',
      emailPlaceholder: 'Enter email',
      passwordLabel: 'Password',
      passwordPlaceholder: 'Enter password',
      loginButton: 'Login',
      errorDefault: 'Login failed',
      errorUnexpected: 'Unexpected error'
    }
  }
};

const i18n = createI18n({
  legacy: false,
  locale: 'en',
  messages,
});

const fakeUserStore = {
  login: vi.fn()
};

vi.mock('@/stores/user', () => ({
  useUserStore: () => fakeUserStore
}));

vi.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// Ensure axios.isAxiosError returns true if error has isAxiosError flag.
import type { AxiosError } from 'axios';

axios.isAxiosError = function <T = any, D = any>(error: any): error is AxiosError<T, D> {
  return !!error?.isAxiosError;
};

// Override window.location.href for testing redirection.
const originalLocation = window.location;
beforeAll(() => {
  delete (window as any).location;
  (window as any).location = { href: '' };
});
afterAll(() => {
  Object.defineProperty(window, 'location', {
    value: originalLocation,
    writable: true,
  });
});

describe('LoginForm.vue', () => {
  let wrapper: any;
  let alertSpy: any;

  beforeEach(() => {
    localStorage.clear();
    fakeUserStore.login.mockClear();
    mockedAxios.post.mockReset();
    alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});
  });

  afterEach(() => {
    if (wrapper) wrapper.unmount();
    alertSpy.mockRestore();
  });

  it('renders the login form correctly', () => {
    wrapper = mount(LoginForm, {
      global: { plugins: [i18n] }
    });
    expect(wrapper.find('label[for="email"]').text()).toBe('Email');
    expect(wrapper.find('input#email').attributes('placeholder')).toBe('Enter email');
    expect(wrapper.find('label[for="password"]').text()).toBe('Password');
    expect(wrapper.find('input#password').attributes('placeholder')).toBe('Enter password');
    expect(wrapper.find('button.submit-btn').text()).toBe('Login');
    expect(wrapper.find('.error-message').exists()).toBe(false);
  });

  it('handles successful login', async () => {
    const userData = { id: 1, name: 'Test User' };
    const token = 'dummy-token';
    mockedAxios.post.mockResolvedValueOnce({ data: { user: userData, token } });
    wrapper = mount(LoginForm, {
      global: { plugins: [i18n] }
    });
    await wrapper.find('input#email').setValue('test@example.com');
    await wrapper.find('input#password').setValue('password123');
    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();
    expect(fakeUserStore.login).toHaveBeenCalledWith(userData);
    expect(localStorage.getItem('authToken')).toBe(token);
    expect(window.location.href).toBe('/');
  });

  it('shows error message on axios error with response', async () => {
    mockedAxios.post.mockRejectedValueOnce({
      isAxiosError: true,
      response: { data: 'Invalid credentials' }
    });
    wrapper = mount(LoginForm, {
      global: { plugins: [i18n] }
    });
    await wrapper.find('input#email').setValue('wrong@example.com');
    await wrapper.find('input#password').setValue('wrongpassword');
    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();
    expect(wrapper.find('.error-message').text()).toBe('Invalid credentials');
  });

  it('shows error message on axios error without response', async () => {
    mockedAxios.post.mockRejectedValueOnce({
      isAxiosError: true,
      response: null
    });
    wrapper = mount(LoginForm, {
      global: { plugins: [i18n] }
    });
    await wrapper.find('input#email').setValue('wrong@example.com');
    await wrapper.find('input#password').setValue('wrongpassword');
    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();
    expect(wrapper.find('.error-message').text()).toBe('Login failed');
  });

  it('shows error message on non-axios error', async () => {
    mockedAxios.post.mockRejectedValueOnce(new Error('Network error'));
    wrapper = mount(LoginForm, {
      global: { plugins: [i18n] }
    });
    await wrapper.find('input#email').setValue('wrong@example.com');
    await wrapper.find('input#password').setValue('wrongpassword');
    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();
    expect(wrapper.find('.error-message').text()).toBe('Unexpected error');
  });
});
