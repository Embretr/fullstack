/**
 * Generated by orval v7.8.0 🍺
 * Do not edit manually.
 * OpenAPI definition
 * OpenAPI spec version: v0
 */
import type { UserRole } from './userRole';
import type { Item } from './item';
import type { Favorite } from './favorite';
import type { Message } from './message';
import type { Order } from './order';

export interface User {
  id?: number;
  /**
   * @minLength 3
   * @maxLength 20
   */
  username: string;
  email: string;
  /**
   * @minLength 6
   * @maxLength 2147483647
   */
  password: string;
  role?: UserRole;
  items?: Item[];
  favorites?: Favorite[];
  sentMessages?: Message[];
  receivedMessages?: Message[];
  orders?: Order[];
}
