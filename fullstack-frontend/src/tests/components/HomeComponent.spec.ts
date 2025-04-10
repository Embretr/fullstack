import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import { createI18n } from 'vue-i18n';
import HomeComponent from '@/components/HomeComponent.vue';
import axios from 'axios';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

function flushPromises() {
  return new Promise(resolve => setTimeout(resolve, 0));
}

const messages = {
  en: {
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

const routerPushMock = vi.fn();
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: routerPushMock
  })
}));

vi.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('HomeComponent.vue', () => {
  let wrapper: any;
  let alertSpy: any;

  beforeEach(() => {
    routerPushMock.mockClear();
    mockedAxios.get.mockReset();
    alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});
    localStorage.clear();
  });

  afterEach(() => {
    if (wrapper) wrapper.unmount();
    alertSpy.mockRestore();
  });

  it('fetches and renders items successfully', async () => {
    const itemsData = [
      { id: 1, title: 'Item 1', description: 'Desc 1', price: 10, imageUrl: 'http://example.com/img1.jpg' },
      { id: 2, title: 'Item 2', description: 'Desc 2', price: 20 } // imageUrl undefined, should default
    ];
    mockedAxios.get.mockResolvedValueOnce({ data: itemsData });
    wrapper = mount(HomeComponent, { global: { plugins: [i18n] } });
    await flushPromises();
    await nextTick(() => {});

    const itemCards = wrapper.findAll('.item-card');
    expect(itemCards.length).toBe(2);
    expect(itemCards[0].find('h3').text()).toBe('Item 1');
    expect(itemCards[0].find('img').attributes('src')).toBe('http://example.com/img1.jpg');
    expect(itemCards[1].find('h3').text()).toBe('Item 2');
    expect(itemCards[1].find('img').attributes('src')).toBe('default-image-url.jpg');
  });

  it('shows alert if fetching items fails', async () => {
    mockedAxios.get.mockRejectedValueOnce(new Error('Network Error'));
    wrapper = mount(HomeComponent, { global: { plugins: [i18n] } });
    await flushPromises();
    await nextTick(() => {});

    expect(alertSpy).toHaveBeenCalledWith('Failed to load items. Please try again later.');
  });

  it('redirects to createItem when token exists on redirect button click', async () => {
    localStorage.setItem('authToken', 'dummy-token');
    wrapper = mount(HomeComponent, { global: { plugins: [i18n] } });
    const button = wrapper.find('.redirect-btn');
    await button.trigger('click');
    expect(routerPushMock).toHaveBeenCalledWith('/createItem');
  });

  it('redirects to login when token does not exist on redirect button click', async () => {
    localStorage.removeItem('authToken');
    wrapper = mount(HomeComponent, { global: { plugins: [i18n] } });
    const button = wrapper.find('.redirect-btn');
    await button.trigger('click');
    expect(routerPushMock).toHaveBeenCalledWith('/login');
  });

  it('calls fetchItems on mounted and logs fetched items', async () => {
    const consoleLogSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
    const itemsData = [
      { id: 3, title: 'Item 3', description: 'Desc 3', price: 30, imageUrl: '' }
    ];
    mockedAxios.get.mockResolvedValueOnce({ data: itemsData });
    wrapper = mount(HomeComponent, { global: { plugins: [i18n] } });
    await flushPromises();
    await nextTick(() => {});

    expect(wrapper.vm.items).toEqual(itemsData);
    expect(consoleLogSpy).toHaveBeenCalledWith('Fetched item: Item 3, Image URL: default-image-url.jpg');
    consoleLogSpy.mockRestore();
  });
});
