/**
 * Generated by orval v7.8.0 🍺
 * Do not edit manually.
 * OpenAPI definition
 * OpenAPI spec version: v0
 */
import type { ItemResponseDTOStatus } from './itemResponseDTOStatus';
import type { UserResponse } from './userResponse';
import type { CategoryResponseDTO } from './categoryResponseDTO';

/**
 * Item information response
 */
export interface ItemResponseDTO {
  /** Item ID */
  id?: number;
  /** Item title */
  title?: string;
  /** Brief description */
  briefDescription?: string;
  /** Full description */
  fullDescription?: string;
  /** Item price */
  price?: number;
  /** Latitude coordinate */
  latitude?: number;
  /** Longitude coordinate */
  longitude?: number;
  /** Publish date */
  publishDate?: string;
  /** Item status */
  status?: ItemResponseDTOStatus;
  /** Reservation date */
  reservationDate?: string;
  reservedBy?: UserResponse;
  owner?: UserResponse;
  category?: CategoryResponseDTO;
  /** Image URLs */
  imageUrls?: string[];
}
