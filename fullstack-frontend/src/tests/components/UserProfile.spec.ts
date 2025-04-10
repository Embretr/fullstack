import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import { createI18n } from 'vue-i18n';
import UserProfile from '@/components/UserProfile.vue';
import axios from 'axios';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

// Helper function to wait for pending promises.
function flushPromises() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

// Create a minimal i18n instance.
const i18n = createI18n({
  legacy: false,
  locale: 'en',
  messages: {
    en: {
      userProfile: {
        welcome: 'Welcome, {userName}',
        emailPrefix: 'Email: ',
        settingsButton: 'Settings',
        createItemButton: 'Create Item',
        errorNoToken: 'No token found',
      },
      navbar: {
        logout: 'Logout',
      },
      logout: {
        success: 'Logout successful',
        error: 'Logout error',
      },
    },
  },
});

// Mock axios so we can simulate API responses.
vi.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// Mock vue-router so that we capture navigation calls.
const routerPushMock = vi.fn();
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: routerPushMock,
  }),
}));

describe('UserProfile.vue', () => {
  // Clear localStorage and mocks before each test.
  beforeEach(() => {
    localStorage.setItem('authToken', 'dummy-token');
    routerPushMock.mockClear();
    mockedAxios.get.mockReset();
    mockedAxios.post.mockReset();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('renders user profile header and buttons correctly', async () => {
    // Set up axios mocks for email, name and items.
    mockedAxios.get.mockImplementation((url) => {
      if (url === 'http://localhost:8080/api/userinfo/email') {
        return Promise.resolve({ data: 'test@example.com' });
      }
      if (url === 'http://localhost:8080/api/userinfo/name') {
        return Promise.resolve({ data: 'Test User' });
      }
      if (url === 'http://localhost:8080/api/user/items') {
        return Promise.resolve({ data: [] });
      }
      return Promise.reject(new Error('Unknown URL'));
    });

    // Mount the component after the mock is set up.
    const wrapper = mount(UserProfile, {
      global: { plugins: [i18n], stubs: ['router-link'] },
    });

    // Wait for onMounted API calls to resolve.
    await flushPromises();
    await nextTick(() => {});
    // Force another update cycle to ensure reactive updates propagate.
    wrapper.vm.$forceUpdate();
    await nextTick(() => {});

    // Check that the header text is correctly rendered.
    // Note: If the translation uses a parameter, the translation function must receive the updated value.
    expect(wrapper.html()).toContain('Welcome, Test User');
    expect(wrapper.html()).toContain('Email: test@example.com');

    // Verify that the header buttons display the expected text.
    expect(wrapper.html()).toContain('Logout');
    expect(wrapper.html()).toContain('Settings');
    expect(wrapper.html()).toContain('Create Item');
  });

  it('fetches and displays user items when available', async () => {
    const itemsData = [
      {
        id: 1,
        title: 'Item 1',
        description: 'A nice item',
        price: 99.99,
        pictureUrl: 'path/to/img1.jpg',
      },
    ];

    // Set up axios mocks to return items.
    mockedAxios.get.mockImplementation((url) => {
      if (url === 'http://localhost:8080/api/userinfo/email') {
        return Promise.resolve({ data: 'test@example.com' });
      }
      if (url === 'http://localhost:8080/api/userinfo/name') {
        return Promise.resolve({ data: 'Test User' });
      }
      if (url === 'http://localhost:8080/api/user/items') {
        return Promise.resolve({ data: itemsData });
      }
      return Promise.reject(new Error('Unknown URL'));
    });

    // Mount after the mocks are set.
    const wrapper = mount(UserProfile, {
      global: { plugins: [i18n], stubs: ['router-link'] },
    });
    await flushPromises();

    await nextTick(() => {});    // Extra wait in case multiple API calls need to resolve.
    await flushPromises();
    await nextTick(() => {});
    // The items container (v-else branch) should now be present.
    const itemsContainer = wrapper.find('#items-container');
    expect(itemsContainer.exists()).toBe(true);
    // Check that item details are rendered.
    expect(itemsContainer.html()).toContain('Item 1');
    expect(itemsContainer.html()).toContain('A nice item');
    expect(itemsContainer.html()).toContain('$99.99');
    // Check that the image src is transformed.
    const img = itemsContainer.find('img');
    expect(img.exists()).toBe(true);
    expect(img.attributes('src')).toContain('http://localhost:8080/api/images/');
  });

  it('displays message when no user items are available', async () => {
    // Set up axios mocks to return an empty array for items.
    mockedAxios.get.mockImplementation((url) => {
      if (url === 'http://localhost:8080/api/userinfo/email') {
        return Promise.resolve({ data: 'test@example.com' });
      }
      if (url === 'http://localhost:8080/api/userinfo/name') {
        return Promise.resolve({ data: 'Test User' });
      }
      if (url === 'http://localhost:8080/api/user/items') {
        return Promise.resolve({ data: [] });
      }
      return Promise.reject(new Error('Unknown URL'));
    });

    // Mount component after the mocks.
    const wrapper = mount(UserProfile, {
      global: { plugins: [i18n], stubs: ['router-link'] },
    });
    await flushPromises();
    await nextTick(() => {});

    // Verify that the "no items" branch is rendered.
    expect(wrapper.html()).toContain('You have not listed any items.');
    // Instead of a positional selector, find the button with the exact text.
    const buttons = wrapper.findAll('button');
    const listItemButton = buttons.find((btn) => btn.text().trim() === 'List Item');
    expect(listItemButton).toBeTruthy();
  });

  it('redirects to login if token is missing during fetchUserEmail', async () => {
    // Remove the token.
    localStorage.removeItem('authToken');

    // Mount component so that onMounted runs without a token.
    const wrapper = mount(UserProfile, {
      global: { plugins: [i18n], stubs: ['router-link'] },
    });
    await flushPromises();
    await nextTick(() => {});

    // The error handler in fetchUserEmail should call logout and redirect.
    expect(routerPushMock).toHaveBeenCalledWith('/login');
  });

  it('logout function calls API, clears token, alerts, and redirects', async () => {
    // Simulate a successful logout API call.
    mockedAxios.post.mockResolvedValueOnce({ data: {} });
    // Spy on window.alert.
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});
    // Mount component.
    const wrapper = mount(UserProfile, {
      global: { plugins: [i18n], stubs: ['router-link'] },
    });
    // Find and click the logout button (first button in the user-profile block).
    const logoutButton = wrapper.find('.user-profile button.button');
    await logoutButton.trigger('click');
    await flushPromises();
    await nextTick(() => {});
    // Check API call.
    expect(mockedAxios.post).toHaveBeenCalledWith(
      'http://localhost:8080/api/logout',
      {},
      { headers: { Authorization: 'Bearer dummy-token' } }
    );
    // Token should be removed.
    expect(localStorage.getItem('authToken')).toBeNull();
    // Verify alert and redirection.
    expect(alertSpy).toHaveBeenCalledWith('Logout successful');
    expect(routerPushMock).toHaveBeenCalledWith('/login');
    alertSpy.mockRestore();
  });

  it('navigates to settings when settings button is clicked', async () => {
    const wrapper = mount(UserProfile, {
      global: { plugins: [i18n], stubs: ['router-link'] },
    });
    // Find the settings button by its text.
    const settingsButton = wrapper.findAll('button').find((btn) => btn.text().trim() === 'Settings');
    expect(settingsButton).toBeTruthy();
    await settingsButton!.trigger('click');
    expect(routerPushMock).toHaveBeenCalledWith('/userSettings');
  });

  it('navigates to create item when create item or list item button is clicked', async () => {
    // First, test the header's "Create Item" button.
    const wrapper = mount(UserProfile, {
      global: { plugins: [i18n], stubs: ['router-link'] },
    });
    const createItemButton = wrapper.findAll('button').find((btn) => btn.text().trim() === 'Create Item');
    expect(createItemButton).toBeTruthy();
    await createItemButton!.trigger('click');
    expect(routerPushMock).toHaveBeenCalledWith('/createItem');

    // Next, simulate the no-items branch (empty items array)
    // Set up mocks to return no items.
    mockedAxios.get.mockImplementation((url) => {
      if (url === 'http://localhost:8080/api/userinfo/email') return Promise.resolve({ data: 'test@example.com' });
      if (url === 'http://localhost:8080/api/userinfo/name') return Promise.resolve({ data: 'Test User' });
      if (url === 'http://localhost:8080/api/user/items') return Promise.resolve({ data: [] });
      return Promise.reject(new Error('Unknown URL'));
    });
    const wrapperNoItems = mount(UserProfile, {
      global: { plugins: [i18n], stubs: ['router-link'] },
    });
    await flushPromises();
    await nextTick(() => {});
    // Find the "List Item" button by filtering all buttons.
    const listItemButton = wrapperNoItems.findAll('button').find(btn => btn.text().trim() === 'List Item');
    expect(listItemButton).toBeTruthy();
    await listItemButton!.trigger('click');
    expect(routerPushMock).toHaveBeenCalledWith('/createItem');
  });
});
