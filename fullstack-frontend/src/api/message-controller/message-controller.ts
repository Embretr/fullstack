import { useQuery } from '@tanstack/vue-query'
import type { Message } from '@/types/message'

export const useGetConversation = (itemId: number, receiverId: number) => {
  return useQuery({
    queryKey: ['conversation', itemId, receiverId],
    queryFn: async () => {
      const response = await fetch(`/api/messages/conversation/${itemId}/${receiverId}`)
      if (!response.ok) {
        throw new Error('Failed to fetch conversation')
      }
      return response.json()
    }
  })
} 