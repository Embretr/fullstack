/**
 * Generated by orval v7.8.0 🍺
 * Do not edit manually.
 * OpenAPI definition
 * OpenAPI spec version: v0
 */
import type { User } from './user';
import type { Item } from './item';

export interface Message {
  id?: number;
  sender?: User;
  receiver?: User;
  item?: Item;
  content?: string;
  timestamp?: string;
}
