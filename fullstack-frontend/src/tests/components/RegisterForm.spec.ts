import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import RegisterForm from '@/components/RegisterForm.vue';
import axios, { AxiosError } from 'axios';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

// Override axios.isAxiosError so that our mocked errors work as intended.
axios.isAxiosError = function <T = any, D = any>(error: any): error is AxiosError<T, D> {
  return !!error?.isAxiosError;
};


// Mock axios
vi.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// Define a router push mock and mock vue-router module
const routerPushMock = vi.fn();
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: routerPushMock,
  }),
}));

describe('RegisterForm.vue', () => {
  let wrapper: any;

  beforeEach(() => {
    wrapper = mount(RegisterForm, {
      global: { stubs: { RouterLink: true } },
    });
    routerPushMock.mockClear();
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('renders the form with all required fields', () => {
    expect(wrapper.find('form').exists()).toBe(true);
    expect(wrapper.find('#name').exists()).toBe(true);
    expect(wrapper.find('#email').exists()).toBe(true);
    expect(wrapper.find('#password').exists()).toBe(true);
    expect(wrapper.find('#confirmPassword').exists()).toBe(true);
    expect(wrapper.find('button[type="submit"]').text()).toBe('Register');
    expect(wrapper.find('.login-link').text()).toContain('Already have an account?');
  });

  it('shows password mismatch error when passwords do not match', async () => {
    // Fill other required fields so that the test doesn’t trigger the required field error.
    await wrapper.find('#name').setValue('John Doe');
    await wrapper.find('#email').setValue('john@example.com');
    await wrapper.find('#password').setValue('password123');
    await wrapper.find('#confirmPassword').setValue('differentPassword');
    await wrapper.find('form').trigger('submit.prevent');

    // Expect to see the password mismatch error.
    expect(wrapper.find('.message').text()).toBe('Passwords do not match.');
    expect(mockedAxios.post).not.toHaveBeenCalled();
  });

  it('submits the form with correct data when passwords match and all validations pass', async () => {
    mockedAxios.post.mockResolvedValueOnce({ data: {} });

    await wrapper.find('#name').setValue('John Doe');
    await wrapper.find('#email').setValue('john@example.com');
    await wrapper.find('#password').setValue('password123');
    await wrapper.find('#confirmPassword').setValue('password123');

    await wrapper.find('form').trigger('submit.prevent');

    expect(mockedAxios.post).toHaveBeenCalledWith(
      'http://localhost:8080/api/auth/register',
      {
        username: 'John Doe',
        email: 'john@example.com',
        password: 'password123',
      }
    );
  });

  it('shows success message and redirects after successful registration', async () => {
    vi.useFakeTimers();
    mockedAxios.post.mockResolvedValueOnce({ data: {} });

    await wrapper.find('#name').setValue('John Doe');
    await wrapper.find('#email').setValue('john@example.com');
    await wrapper.find('#password').setValue('password123');
    await wrapper.find('#confirmPassword').setValue('password123');

    await wrapper.find('form').trigger('submit.prevent');
    await nextTick(() => {});


    expect(wrapper.find('.message').text()).toBe('Registration successful, going to login.');

    // Fast-forward time by 1 second for the setTimeout in the component.
    vi.advanceTimersByTime(1000);
    await nextTick(() => {});

    expect(routerPushMock).toHaveBeenCalledWith('/login');
  });

  it('handles API error with response data', async () => {
    mockedAxios.post.mockRejectedValueOnce({
      isAxiosError: true,
      response: { data: 'Email already in use' },
    });

    await wrapper.find('#name').setValue('John Doe');
    await wrapper.find('#email').setValue('john@example.com');
    await wrapper.find('#password').setValue('password123');
    await wrapper.find('#confirmPassword').setValue('password123');
    await wrapper.find('form').trigger('submit.prevent');

    await nextTick(() => {});

    expect(wrapper.find('.message').text()).toBe('Email already in use');
  });

  it('handles API error without response data', async () => {
    mockedAxios.post.mockRejectedValueOnce({
      isAxiosError: true,
      response: null,
    });

    await wrapper.find('#name').setValue('John Doe');
    await wrapper.find('#email').setValue('john@example.com');
    await wrapper.find('#password').setValue('password123');
    await wrapper.find('#confirmPassword').setValue('password123');
    await wrapper.find('form').trigger('submit.prevent');

    await nextTick(() => {});

    expect(wrapper.find('.message').text()).toBe('An error occurred during registration.');
  });

  it('handles unexpected errors', async () => {
    // Non-Axios error: missing isAxiosError property.
    mockedAxios.post.mockRejectedValueOnce({});

    await wrapper.find('#name').setValue('John Doe');
    await wrapper.find('#email').setValue('john@example.com');
    await wrapper.find('#password').setValue('password123');
    await wrapper.find('#confirmPassword').setValue('password123');
    await wrapper.find('form').trigger('submit.prevent');

    await nextTick(() => {});

    expect(wrapper.find('.message').text()).toBe('An unexpected error occurred.');
  });

  it('does not submit when required fields are empty', async () => {
    await wrapper.find('form').trigger('submit.prevent');
    expect(mockedAxios.post).not.toHaveBeenCalled();
    expect(wrapper.find('.message').text()).toBe('Please fill in all required fields.');
  });

  it('validates email format', async () => {
    // Fill in the other fields so that required-field validation passes.
    await wrapper.find('#name').setValue('John Doe');
    await wrapper.find('#password').setValue('password123');
    await wrapper.find('#confirmPassword').setValue('password123');
    // Set an invalid email.
    await wrapper.find('#email').setValue('invalid-email');
    await wrapper.find('form').trigger('submit.prevent');
    expect(mockedAxios.post).not.toHaveBeenCalled();
    expect(wrapper.find('.message').text()).toBe('Please enter a valid email address.');
  });

  it('clears messages when submitting again after error', async () => {
    // First submission: produce an error.
    mockedAxios.post.mockRejectedValueOnce({
      isAxiosError: true,
      response: { data: 'Initial error' },
    });

    await wrapper.find('#name').setValue('John Doe');
    await wrapper.find('#email').setValue('john@example.com');
    await wrapper.find('#password').setValue('password123');
    await wrapper.find('#confirmPassword').setValue('password123');
    await wrapper.find('form').trigger('submit.prevent');
    await nextTick(() => {});

    expect(wrapper.find('.message').text()).toBe('Initial error');

    // Second submission: now simulate success.
    mockedAxios.post.mockResolvedValueOnce({ data: {} });
    await wrapper.find('form').trigger('submit.prevent');
    await nextTick(() => {});

    expect(wrapper.find('.message').text()).toBe('Registration successful, going to login.');
  });

  it('applies correct styles', () => {
    const form = wrapper.find('.register-form');
    const input = wrapper.find('input');
    const button = wrapper.find('.submit-btn');
    const message = wrapper.find('.message');

    expect(form.exists()).toBe(true);
    expect(input.attributes('type')).toBe('text');
    expect(button.text()).toBe('Register');
    // The message element should not exist if there is no message.
    expect(message.exists()).toBe(false);
  });
});
