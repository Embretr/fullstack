/**
 * Generated by orval v7.8.0 🍺
 * Do not edit manually.
 * OpenAPI definition
 * OpenAPI spec version: v0
 */
import type { ItemStatus } from './itemStatus';
import type { Category } from './category';

export interface Item {
  id?: number;
  title?: string;
  briefDescription?: string;
  fullDescription?: string;
  price?: number;
  latitude?: number;
  longitude?: number;
  publishDate?: string;
  reservationDate?: string;
  status?: ItemStatus;
  category?: Category;
}
