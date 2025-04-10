import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import { createI18n } from 'vue-i18n';
import Header from '@/components/NavBar.vue';
import axios from 'axios';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

const i18n = createI18n({
  legacy: false,
  locale: 'en',
  messages: {
    en: {
      navbar: {
        logo: 'MyLogo',
        home: 'Home',
        categories: 'Categories',
        favorites: 'Favorites',
        messages: 'Messages',
        language: 'Language'
      }
    }
  }
});

vi.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

const fakeUserStore = {
  isLoggedIn: false,
  login: vi.fn(),
  logout: vi.fn()
};

vi.mock('@/stores/user', () => ({
  useUserStore: () => fakeUserStore
}));

describe('Header.vue', () => {
  let wrapper: any;

  beforeEach(() => {
    fakeUserStore.isLoggedIn = false;
    fakeUserStore.login.mockClear();
    fakeUserStore.logout.mockClear();
    mockedAxios.get.mockReset();
    localStorage.clear();
  });

  afterEach(() => {
    if (wrapper) wrapper.unmount();
  });

  it('renders header with logo and nav links', async () => {
    wrapper = mount(Header, {
      global: {
        plugins: [i18n],
        stubs: {
          // Stub RouterLink to render the slot content in an anchor element.
          RouterLink: {
            template: '<a><slot /></a>'
          },
          LoggedInNavBar: true,
          LoggedOutNavBar: true
        }
      }
    });
    await nextTick(() => {});
    expect(wrapper.find('.logo').text()).toBe('MyLogo');
    const navLinks = wrapper.findAll('.nav-links a');
    expect(navLinks).toHaveLength(4);
    expect(navLinks[0].text()).toBe('Home');
    expect(navLinks[1].text()).toBe('Categories');
    expect(navLinks[2].text()).toBe('Favorites');
    expect(navLinks[3].text()).toBe('Messages');
    expect(wrapper.find('.language-switcher label').text()).toContain('Language');
  });

  it('shows LoggedInNavBar when logged in', async () => {
    fakeUserStore.isLoggedIn = true;
    wrapper = mount(Header, {
      global: {
        plugins: [i18n],
        stubs: {
          RouterLink: {
            template: '<a><slot /></a>'
          },
          LoggedInNavBar: { template: '<div class="logged-in-nav" />' },
          LoggedOutNavBar: { template: '<div class="logged-out-nav" />' }
        }
      }
    });
    await nextTick(() => {});
    expect(wrapper.find('.logged-in-nav').exists()).toBe(true);
    expect(wrapper.find('.logged-out-nav').exists()).toBe(false);
  });

  it('shows LoggedOutNavBar when not logged in', async () => {
    fakeUserStore.isLoggedIn = false;
    wrapper = mount(Header, {
      global: {
        plugins: [i18n],
        stubs: {
          RouterLink: {
            template: '<a><slot /></a>'
          },
          LoggedInNavBar: { template: '<div class="logged-in-nav" />' },
          LoggedOutNavBar: { template: '<div class="logged-out-nav" />' }
        }
      }
    });
    await nextTick(() => {});
    expect(wrapper.find('.logged-out-nav').exists()).toBe(true);
    expect(wrapper.find('.logged-in-nav').exists()).toBe(false);
  });

  it('calls userStore.login on mounted when token exists and axios returns true', async () => {
    localStorage.setItem('authToken', 'dummy-token');
    mockedAxios.get.mockResolvedValueOnce({ data: true });
    wrapper = mount(Header, {
      global: {
        plugins: [i18n],
        stubs: {
          RouterLink: {
            template: '<a><slot /></a>'
          },
          LoggedInNavBar: true,
          LoggedOutNavBar: true
        }
      }
    });
    await nextTick(() => {});
    expect(fakeUserStore.login).toHaveBeenCalledWith({ name: 'User', email: 'user@example.com' });
  });

  it('calls userStore.logout on mounted when token exists and axios call fails', async () => {
    localStorage.setItem('authToken', 'dummy-token');
    mockedAxios.get.mockRejectedValueOnce(new Error('failed'));
    wrapper = mount(Header, {
      global: {
        plugins: [i18n],
        stubs: {
          RouterLink: {
            template: '<a><slot /></a>'
          },
          LoggedInNavBar: true,
          LoggedOutNavBar: true
        }
      }
    });
    await nextTick(() => {});
    expect(fakeUserStore.logout).toHaveBeenCalled();
  });
});
