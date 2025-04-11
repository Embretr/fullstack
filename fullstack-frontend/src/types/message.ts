export interface User {
  id: number
  username: string
}

export interface Item {
  id: number
}

export interface Message {
  id: number
  content: string
  timestamp: string
  sender?: User
  receiver?: User
  item?: Item
} 