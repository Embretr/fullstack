import { mount } from '@vue/test-utils';
import { createI18n } from 'vue-i18n';
import CreateItemForm from '@/components/CreateItemForm.vue';
import axios from 'axios';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { VueQueryPlugin, QueryClient } from '@tanstack/vue-query';

function flushPromises() {
  return new Promise(resolve => setTimeout(resolve, 0));
}

const messages = {
  en: {
    createItemForm: {
      title: 'Create New Item',
      nameLabel: 'Item Name',
      descriptionLabel: 'Item Description',
      priceLabel: 'Item Price',
      categoryLabel: 'Item Category',
      categorySelectDefault: 'Select Category',
      categoryElectronics: 'Electronics',
      categoryClothing: 'Clothing',
      categoryHome: 'Home',
      categoryToys: 'Toys',
      errorNotLoggedIn: 'You are not logged in',
      successMessage: 'Item created successfully',
      errorDefault: 'Failed to create item'
    },
    userProfile: {
      createItemButton: 'Create Item'
    }
  }
};

const i18n = createI18n({
  legacy: false,
  locale: 'en',
  messages
});

vi.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// Override window.location.href for redirection testing.
const originalLocation = window.location;
beforeAll(() => {
  delete (window as any).location;
  (window as any).location = { href: '' };
});
afterAll(() => {
  window.location = originalLocation as any;
});

// Create a query client for testing
const testQueryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false, // Disable retries for tests
    },
  },
});

describe('CreateItemForm.vue', () => {
  let wrapper: any;
  let alertSpy: any;

  beforeEach(() => {
    localStorage.clear();
    alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});
    mockedAxios.post.mockReset();
  });

  afterEach(() => {
    if (wrapper) wrapper.unmount();
    alertSpy.mockRestore();
  });

  it('renders the form correctly', () => {
    wrapper = mount(CreateItemForm, {
      global: {
        plugins: [i18n, [VueQueryPlugin, { queryClient: testQueryClient }]], // Register VueQueryPlugin
      },
    });
    expect(wrapper.find('h2').text()).toBe(messages.en.createItemForm.title);
    expect(wrapper.find('label[for="name"]').text()).toBe(messages.en.createItemForm.nameLabel);
    expect(wrapper.find('label[for="description"]').text()).toBe(messages.en.createItemForm.descriptionLabel);
    expect(wrapper.find('label[for="price"]').text()).toBe(messages.en.createItemForm.priceLabel);
    expect(wrapper.find('label[for="category"]').text()).toBe(messages.en.createItemForm.categoryLabel);
    expect(wrapper.find('select#category').exists()).toBe(true);
    expect(wrapper.find('input[type="file"]').exists()).toBe(true);
    expect(wrapper.find('button.submit-btn').text()).toBe(messages.en.userProfile.createItemButton);
  });

  it('redirects to login if no token is found on submit', async () => {
    localStorage.removeItem('authToken');
    wrapper = mount(CreateItemForm, {
      global: { plugins: [i18n, [VueQueryPlugin, { queryClient: testQueryClient }]] }
    });
    await wrapper.find('input#name').setValue('Test Item');
    await wrapper.find('textarea#description').setValue('Test Description');
    await wrapper.find('input#price').setValue('12.34');
    await wrapper.find('select#category').setValue('electronics');
    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();
    expect(alertSpy).toHaveBeenCalledWith(messages.en.createItemForm.errorNotLoggedIn);
    expect(window.location.href).toBe('/login');
  });

  it('submits the form successfully and clears fields', async () => {
    localStorage.setItem('authToken', 'dummy-token');
    const responseData = { id: 100, name: 'Test Item' };
    mockedAxios.post.mockResolvedValueOnce({ data: responseData });
    wrapper = mount(CreateItemForm, {
      global: { plugins: [i18n, [VueQueryPlugin, { queryClient: testQueryClient }]] }
    });

    await wrapper.find('input#name').setValue('Test Item');
    await wrapper.find('textarea#description').setValue('Test Description');
    await wrapper.find('input#price').setValue('12.34');
    await wrapper.find('select#category').setValue('electronics');

    // Simulate file input change without using DataTransfer
    const dummyFile = new File(['dummy content'], 'dummy.png', { type: 'image/png' });
    const fileInput = wrapper.find('input[type="file"]');
    Object.defineProperty(fileInput.element, 'files', { value: [dummyFile] });
    await fileInput.trigger('change');

    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();

    expect(mockedAxios.post).toHaveBeenCalled();
    const callArgs = mockedAxios.post.mock.calls[0];
    expect(callArgs[0]).toBe('http://localhost:8080/api/createItem');
    expect(callArgs[2]).toMatchObject({
      headers: {
        Authorization: 'Bearer dummy-token',
        'Content-Type': 'multipart/form-data'
      }
    });
    const formData: FormData = callArgs[1] as FormData;
    expect(formData.get('name')).toBe('Test Item');
    expect(formData.get('description')).toBe('Test Description');
    expect(formData.get('price')).toBe('12.34');
    expect(formData.get('category')).toBe('electronics');
    expect(formData.get('image')).toBe(dummyFile);
    expect(alertSpy).toHaveBeenCalledWith(messages.en.createItemForm.successMessage);
    expect(wrapper.find('input#name').element.value).toBe('');
    expect(wrapper.find('textarea#description').element.value).toBe('');
    // Expect number input to be cleared to "0" since v-model.number resets to 0.
    expect(wrapper.find('input#price').element.value).toBe('0');
    expect(wrapper.find('select#category').element.value).toBe('');
  });

  it('shows error alert when submission fails', async () => {
    localStorage.setItem('authToken', 'dummy-token');
    mockedAxios.post.mockRejectedValueOnce(new Error('Submission failed'));
    wrapper = mount(CreateItemForm, {
      global: { plugins: [i18n, [VueQueryPlugin, { queryClient: testQueryClient }]] }
    });
    await wrapper.find('input#name').setValue('Test Item');
    await wrapper.find('textarea#description').setValue('Test Description');
    await wrapper.find('input#price').setValue('12.34');
    await wrapper.find('select#category').setValue('electronics');
    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();
    expect(alertSpy).toHaveBeenCalledWith(messages.en.createItemForm.errorDefault);
  });
});
