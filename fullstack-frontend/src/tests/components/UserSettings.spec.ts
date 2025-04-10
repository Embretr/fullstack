// UserProfile.spec.ts
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import UserProfile from '@/components/UserProfile.vue'
import { useRouter } from 'vue-router'

// Mock vue-router
vi.mock('vue-router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
  })),
}))

// Mock API functions with proper Vue Query structure
vi.mock('@/api/authentication/authentication', () => ({
  useGetMe: vi.fn(() => ({
    data: { value: { email: 'test@example.com' } },
    isLoading: false,
    isError: false,
  })),
  useLogout: vi.fn(() => ({
    mutate: vi.fn().mockResolvedValue({}),
  })),
}))

vi.mock('@/api/item/item', () => ({
  useGetUserItems: vi.fn(() => ({
    data: { value: [{ id: 1, name: 'Test Item' }] },
    isLoading: false,
    isError: false,
  })),
}))

describe('UserProfile', () => {
  let wrapper: any

  beforeEach(() => {
    // Clear all mocks before each test
    vi.clearAllMocks()

    // Mock localStorage
    Storage.prototype.clear = vi.fn()
    Storage.prototype.getItem = vi.fn(() => 'mock-token')

    // Mock alert
    window.alert = vi.fn()

    wrapper = mount(UserProfile)
  })

  it('renders user profile header and buttons correctly', async () => {
    await wrapper.vm.$nextTick()
    expect(wrapper.find('h1').text()).toBe('userProfile.title')
    expect(wrapper.find('.email-display').text()).toContain('test@example.com')
    expect(wrapper.findAll('button').length).toBeGreaterThan(0)
  })

  it('fetches and displays user items when available', async () => {
    await wrapper.vm.$nextTick()
    expect(wrapper.find('.item-list').exists()).toBe(true)
    expect(wrapper.findAll('.item').length).toBeGreaterThan(0)
  })

  it('displays message when no user items are available', async () => {
    // Override mock for this test
    vi.mocked(useGetUserItems).mockReturnValueOnce({
      data: { value: [] },
      isLoading: false,
      isError: false,
    })

    wrapper = mount(UserProfile)
    await wrapper.vm.$nextTick()
    expect(wrapper.find('.no-items-message').exists()).toBe(true)
  })

  it('redirects to login if token is missing during fetchUserEmail', async () => {
    // Simulate missing token
    Storage.prototype.getItem = vi.fn(() => null)

    const router = useRouter()
    wrapper = mount(UserProfile)
    await wrapper.vm.$nextTick()

    expect(router.push).toHaveBeenCalledWith('/login')
  })

  it('logout function calls API, clears token, alerts, and redirects', async () => {
    const router = useRouter()
    await wrapper.find('.logout-btn').trigger('click')

    // Check logout was called
    expect(wrapper.vm.useLogout().mutate).toHaveBeenCalled()
    expect(localStorage.clear).toHaveBeenCalled()
    expect(window.alert).toHaveBeenCalledWith('userProfile.logoutSuccess')
    expect(router.push).toHaveBeenCalledWith('/login')
  })

  it('navigates to settings when settings button is clicked', async () => {
    const router = useRouter()
    await wrapper.find('.settings-btn').trigger('click')
    expect(router.push).toHaveBeenCalledWith('/settings')
  })

  it('navigates to create item when create item button is clicked', async () => {
    const router = useRouter()
    await wrapper.find('.create-item-btn').trigger('click')
    expect(router.push).toHaveBeenCalledWith('/create-item')
  })
})