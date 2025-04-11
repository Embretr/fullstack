import { User } from './user';
import { Category } from './category';

export interface Item {
    id: number;
    title: string;
    briefDescription: string;
    fullDescription: string;
    price: number;
    latitude: number;
    longitude: number;
    publishDate: string;
    status: 'ACTIVE' | 'ARCHIVED' | 'SOLD';
    owner: User;
    category: Category;
    imageUrls?: string[];
} 