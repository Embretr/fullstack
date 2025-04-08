/**
 * Generated by orval v7.8.0 🍺
 * Do not edit manually.
 * OpenAPI definition
 * OpenAPI spec version: v0
 */
import type { ItemStatus } from './itemStatus';
import type { User } from './user';
import type { Category } from './category';
import type { Image } from './image';
import type { Favorite } from './favorite';
import type { Message } from './message';
import type { Order } from './order';

export interface Item {
  id?: number;
  title?: string;
  briefDescription?: string;
  fullDescription?: string;
  price?: number;
  latitude?: number;
  longitude?: number;
  publishDate?: string;
  status?: ItemStatus;
  user?: User;
  category?: Category;
  images?: Image[];
  favorites?: Favorite[];
  messages?: Message[];
  orders?: Order[];
}
