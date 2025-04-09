/**
 * Generated by orval v7.8.0 🍺
 * Do not edit manually.
 * OpenAPI definition
 * OpenAPI spec version: v0
 */
import {
  useMutation,
  useQuery
} from '@tanstack/vue-query';
import type {
  DataTag,
  MutationFunction,
  QueryClient,
  QueryFunction,
  QueryKey,
  UseMutationOptions,
  UseMutationReturnType,
  UseQueryOptions,
  UseQueryReturnType
} from '@tanstack/vue-query';

import * as axios from 'axios';
import type {
  AxiosError,
  AxiosRequestConfig,
  AxiosResponse
} from 'axios';

import {
  computed,
  unref
} from 'vue';
import type {
  MaybeRef
} from 'vue';

import type {
  AddToFavorites200,
  CreateItem200,
  CreateItemParams,
  DeleteItem200,
  GetItemById200,
  GetUserFavorites200,
  GetUserItems200,
  IsItemFavorited200,
  ItemResponseDTO,
  RemoveFromFavorites200
} from '.././model';





/**
 * Retrieves a list of all items in the marketplace
 * @summary Get all items
 */
export const getAllItems = (
     options?: AxiosRequestConfig
 ): Promise<AxiosResponse<ItemResponseDTO[]>> => {
    
    
    return axios.default.get(
      `/api/items`,options
    );
  }


export const getGetAllItemsQueryKey = () => {
    return ['api','items'] as const;
    }

    
export const getGetAllItemsQueryOptions = <TData = Awaited<ReturnType<typeof getAllItems>>, TError = AxiosError<unknown>>( options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getAllItems>>, TError, TData>>, axios?: AxiosRequestConfig}
) => {

const {query: queryOptions, axios: axiosOptions} = options ?? {};

  const queryKey =  getGetAllItemsQueryKey();

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof getAllItems>>> = ({ signal }) => getAllItems({ signal, ...axiosOptions });

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof getAllItems>>, TError, TData> 
}

export type GetAllItemsQueryResult = NonNullable<Awaited<ReturnType<typeof getAllItems>>>
export type GetAllItemsQueryError = AxiosError<unknown>


/**
 * @summary Get all items
 */

export function useGetAllItems<TData = Awaited<ReturnType<typeof getAllItems>>, TError = AxiosError<unknown>>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getAllItems>>, TError, TData>>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient 
 ): UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getGetAllItemsQueryOptions(options)

  const query = useQuery(queryOptions , queryClient) as UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = unref(queryOptions).queryKey as DataTag<QueryKey, TData, TError>;

  return query;
}



/**
 * Creates a new item with associated images
 * @summary Create new item
 */
export const createItem = (
    params: MaybeRef<CreateItemParams>, options?: AxiosRequestConfig
 ): Promise<AxiosResponse<CreateItem200>> => {
    params = unref(params);
    
    return axios.default.post(
      `/api/items`,undefined,{
    ...options,
        params: {...unref(params), ...options?.params},}
    );
  }



export const getCreateItemMutationOptions = <TError = AxiosError<unknown>,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<Awaited<ReturnType<typeof createItem>>, TError,{params: CreateItemParams}, TContext>, axios?: AxiosRequestConfig}
): UseMutationOptions<Awaited<ReturnType<typeof createItem>>, TError,{params: CreateItemParams}, TContext> => {
    
const mutationKey = ['createItem'];
const {mutation: mutationOptions, axios: axiosOptions} = options ?
      options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey ?
      options
      : {...options, mutation: {...options.mutation, mutationKey}}
      : {mutation: { mutationKey, }, axios: undefined};

      


      const mutationFn: MutationFunction<Awaited<ReturnType<typeof createItem>>, {params: CreateItemParams}> = (props) => {
          const {params} = props ?? {};

          return  createItem(params,axiosOptions)
        }

        


  return  { mutationFn, ...mutationOptions }}

    export type CreateItemMutationResult = NonNullable<Awaited<ReturnType<typeof createItem>>>
    
    export type CreateItemMutationError = AxiosError<unknown>

    /**
 * @summary Create new item
 */
export const useCreateItem = <TError = AxiosError<unknown>,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<Awaited<ReturnType<typeof createItem>>, TError,{params: CreateItemParams}, TContext>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient): UseMutationReturnType<
        Awaited<ReturnType<typeof createItem>>,
        TError,
        {params: CreateItemParams},
        TContext
      > => {

      const mutationOptions = getCreateItemMutationOptions(options);

      return useMutation(mutationOptions , queryClient);
    }
    /**
 * Adds an item to the user's favorites
 * @summary Add item to favorites
 */
export const addToFavorites = (
    itemId: MaybeRef<number>, options?: AxiosRequestConfig
 ): Promise<AxiosResponse<AddToFavorites200>> => {
    itemId = unref(itemId);
    
    return axios.default.post(
      `/api/items/${itemId}/favorite`,undefined,options
    );
  }



export const getAddToFavoritesMutationOptions = <TError = AxiosError<unknown>,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<Awaited<ReturnType<typeof addToFavorites>>, TError,{itemId: number}, TContext>, axios?: AxiosRequestConfig}
): UseMutationOptions<Awaited<ReturnType<typeof addToFavorites>>, TError,{itemId: number}, TContext> => {
    
const mutationKey = ['addToFavorites'];
const {mutation: mutationOptions, axios: axiosOptions} = options ?
      options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey ?
      options
      : {...options, mutation: {...options.mutation, mutationKey}}
      : {mutation: { mutationKey, }, axios: undefined};

      


      const mutationFn: MutationFunction<Awaited<ReturnType<typeof addToFavorites>>, {itemId: number}> = (props) => {
          const {itemId} = props ?? {};

          return  addToFavorites(itemId,axiosOptions)
        }

        


  return  { mutationFn, ...mutationOptions }}

    export type AddToFavoritesMutationResult = NonNullable<Awaited<ReturnType<typeof addToFavorites>>>
    
    export type AddToFavoritesMutationError = AxiosError<unknown>

    /**
 * @summary Add item to favorites
 */
export const useAddToFavorites = <TError = AxiosError<unknown>,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<Awaited<ReturnType<typeof addToFavorites>>, TError,{itemId: number}, TContext>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient): UseMutationReturnType<
        Awaited<ReturnType<typeof addToFavorites>>,
        TError,
        {itemId: number},
        TContext
      > => {

      const mutationOptions = getAddToFavoritesMutationOptions(options);

      return useMutation(mutationOptions , queryClient);
    }
    /**
 * Removes an item from the user's favorites
 * @summary Remove item from favorites
 */
export const removeFromFavorites = (
    itemId: MaybeRef<number>, options?: AxiosRequestConfig
 ): Promise<AxiosResponse<RemoveFromFavorites200>> => {
    itemId = unref(itemId);
    
    return axios.default.delete(
      `/api/items/${itemId}/favorite`,options
    );
  }



export const getRemoveFromFavoritesMutationOptions = <TError = AxiosError<unknown>,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<Awaited<ReturnType<typeof removeFromFavorites>>, TError,{itemId: number}, TContext>, axios?: AxiosRequestConfig}
): UseMutationOptions<Awaited<ReturnType<typeof removeFromFavorites>>, TError,{itemId: number}, TContext> => {
    
const mutationKey = ['removeFromFavorites'];
const {mutation: mutationOptions, axios: axiosOptions} = options ?
      options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey ?
      options
      : {...options, mutation: {...options.mutation, mutationKey}}
      : {mutation: { mutationKey, }, axios: undefined};

      


      const mutationFn: MutationFunction<Awaited<ReturnType<typeof removeFromFavorites>>, {itemId: number}> = (props) => {
          const {itemId} = props ?? {};

          return  removeFromFavorites(itemId,axiosOptions)
        }

        


  return  { mutationFn, ...mutationOptions }}

    export type RemoveFromFavoritesMutationResult = NonNullable<Awaited<ReturnType<typeof removeFromFavorites>>>
    
    export type RemoveFromFavoritesMutationError = AxiosError<unknown>

    /**
 * @summary Remove item from favorites
 */
export const useRemoveFromFavorites = <TError = AxiosError<unknown>,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<Awaited<ReturnType<typeof removeFromFavorites>>, TError,{itemId: number}, TContext>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient): UseMutationReturnType<
        Awaited<ReturnType<typeof removeFromFavorites>>,
        TError,
        {itemId: number},
        TContext
      > => {

      const mutationOptions = getRemoveFromFavoritesMutationOptions(options);

      return useMutation(mutationOptions , queryClient);
    }
    /**
 * Retrieves a single item by its ID
 * @summary Get item by ID
 */
export const getItemById = (
    itemId: MaybeRef<number>, options?: AxiosRequestConfig
 ): Promise<AxiosResponse<GetItemById200>> => {
    itemId = unref(itemId);
    
    return axios.default.get(
      `/api/items/${itemId}`,options
    );
  }


export const getGetItemByIdQueryKey = (itemId: MaybeRef<number>,) => {
    return ['api','items',itemId] as const;
    }

    
export const getGetItemByIdQueryOptions = <TData = Awaited<ReturnType<typeof getItemById>>, TError = AxiosError<unknown>>(itemId: MaybeRef<number>, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getItemById>>, TError, TData>>, axios?: AxiosRequestConfig}
) => {

const {query: queryOptions, axios: axiosOptions} = options ?? {};

  const queryKey =  getGetItemByIdQueryKey(itemId);

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof getItemById>>> = ({ signal }) => getItemById(itemId, { signal, ...axiosOptions });

      

      

   return  { queryKey, queryFn, enabled: computed(() => !!(unref(itemId))), ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof getItemById>>, TError, TData> 
}

export type GetItemByIdQueryResult = NonNullable<Awaited<ReturnType<typeof getItemById>>>
export type GetItemByIdQueryError = AxiosError<unknown>


/**
 * @summary Get item by ID
 */

export function useGetItemById<TData = Awaited<ReturnType<typeof getItemById>>, TError = AxiosError<unknown>>(
 itemId: MaybeRef<number>, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getItemById>>, TError, TData>>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient 
 ): UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getGetItemByIdQueryOptions(itemId,options)

  const query = useQuery(queryOptions , queryClient) as UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = unref(queryOptions).queryKey as DataTag<QueryKey, TData, TError>;

  return query;
}



/**
 * Deletes an item by its ID
 * @summary Delete item
 */
export const deleteItem = (
    itemId: MaybeRef<number>, options?: AxiosRequestConfig
 ): Promise<AxiosResponse<DeleteItem200>> => {
    itemId = unref(itemId);
    
    return axios.default.delete(
      `/api/items/${itemId}`,options
    );
  }



export const getDeleteItemMutationOptions = <TError = AxiosError<unknown>,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<Awaited<ReturnType<typeof deleteItem>>, TError,{itemId: number}, TContext>, axios?: AxiosRequestConfig}
): UseMutationOptions<Awaited<ReturnType<typeof deleteItem>>, TError,{itemId: number}, TContext> => {
    
const mutationKey = ['deleteItem'];
const {mutation: mutationOptions, axios: axiosOptions} = options ?
      options.mutation && 'mutationKey' in options.mutation && options.mutation.mutationKey ?
      options
      : {...options, mutation: {...options.mutation, mutationKey}}
      : {mutation: { mutationKey, }, axios: undefined};

      


      const mutationFn: MutationFunction<Awaited<ReturnType<typeof deleteItem>>, {itemId: number}> = (props) => {
          const {itemId} = props ?? {};

          return  deleteItem(itemId,axiosOptions)
        }

        


  return  { mutationFn, ...mutationOptions }}

    export type DeleteItemMutationResult = NonNullable<Awaited<ReturnType<typeof deleteItem>>>
    
    export type DeleteItemMutationError = AxiosError<unknown>

    /**
 * @summary Delete item
 */
export const useDeleteItem = <TError = AxiosError<unknown>,
    TContext = unknown>(options?: { mutation?:UseMutationOptions<Awaited<ReturnType<typeof deleteItem>>, TError,{itemId: number}, TContext>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient): UseMutationReturnType<
        Awaited<ReturnType<typeof deleteItem>>,
        TError,
        {itemId: number},
        TContext
      > => {

      const mutationOptions = getDeleteItemMutationOptions(options);

      return useMutation(mutationOptions , queryClient);
    }
    /**
 * Checks if the current user has favorited the item
 * @summary Check if item is favorited
 */
export const isItemFavorited = (
    itemId: MaybeRef<number>, options?: AxiosRequestConfig
 ): Promise<AxiosResponse<IsItemFavorited200>> => {
    itemId = unref(itemId);
    
    return axios.default.get(
      `/api/items/${itemId}/is-favorite`,options
    );
  }


export const getIsItemFavoritedQueryKey = (itemId: MaybeRef<number>,) => {
    return ['api','items',itemId,'is-favorite'] as const;
    }

    
export const getIsItemFavoritedQueryOptions = <TData = Awaited<ReturnType<typeof isItemFavorited>>, TError = AxiosError<unknown>>(itemId: MaybeRef<number>, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof isItemFavorited>>, TError, TData>>, axios?: AxiosRequestConfig}
) => {

const {query: queryOptions, axios: axiosOptions} = options ?? {};

  const queryKey =  getIsItemFavoritedQueryKey(itemId);

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof isItemFavorited>>> = ({ signal }) => isItemFavorited(itemId, { signal, ...axiosOptions });

      

      

   return  { queryKey, queryFn, enabled: computed(() => !!(unref(itemId))), ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof isItemFavorited>>, TError, TData> 
}

export type IsItemFavoritedQueryResult = NonNullable<Awaited<ReturnType<typeof isItemFavorited>>>
export type IsItemFavoritedQueryError = AxiosError<unknown>


/**
 * @summary Check if item is favorited
 */

export function useIsItemFavorited<TData = Awaited<ReturnType<typeof isItemFavorited>>, TError = AxiosError<unknown>>(
 itemId: MaybeRef<number>, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof isItemFavorited>>, TError, TData>>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient 
 ): UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getIsItemFavoritedQueryOptions(itemId,options)

  const query = useQuery(queryOptions , queryClient) as UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = unref(queryOptions).queryKey as DataTag<QueryKey, TData, TError>;

  return query;
}



/**
 * Retrieves all items belonging to the currently logged-in user
 * @summary Get user's items
 */
export const getUserItems = (
     options?: AxiosRequestConfig
 ): Promise<AxiosResponse<GetUserItems200>> => {
    
    
    return axios.default.get(
      `/api/items/user`,options
    );
  }


export const getGetUserItemsQueryKey = () => {
    return ['api','items','user'] as const;
    }

    
export const getGetUserItemsQueryOptions = <TData = Awaited<ReturnType<typeof getUserItems>>, TError = AxiosError<unknown>>( options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getUserItems>>, TError, TData>>, axios?: AxiosRequestConfig}
) => {

const {query: queryOptions, axios: axiosOptions} = options ?? {};

  const queryKey =  getGetUserItemsQueryKey();

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof getUserItems>>> = ({ signal }) => getUserItems({ signal, ...axiosOptions });

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof getUserItems>>, TError, TData> 
}

export type GetUserItemsQueryResult = NonNullable<Awaited<ReturnType<typeof getUserItems>>>
export type GetUserItemsQueryError = AxiosError<unknown>


/**
 * @summary Get user's items
 */

export function useGetUserItems<TData = Awaited<ReturnType<typeof getUserItems>>, TError = AxiosError<unknown>>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getUserItems>>, TError, TData>>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient 
 ): UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getGetUserItemsQueryOptions(options)

  const query = useQuery(queryOptions , queryClient) as UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = unref(queryOptions).queryKey as DataTag<QueryKey, TData, TError>;

  return query;
}



/**
 * Retrieves an image file for an item
 * @summary Get item image
 */
export const getImage = (
    filename: MaybeRef<string>, options?: AxiosRequestConfig
 ): Promise<AxiosResponse<Blob>> => {
    filename = unref(filename);
    
    return axios.default.get(
      `/api/items/images/${filename}`,{
        responseType: 'blob',
    ...options,}
    );
  }


export const getGetImageQueryKey = (filename: MaybeRef<string>,) => {
    return ['api','items','images',filename] as const;
    }

    
export const getGetImageQueryOptions = <TData = Awaited<ReturnType<typeof getImage>>, TError = AxiosError<unknown>>(filename: MaybeRef<string>, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getImage>>, TError, TData>>, axios?: AxiosRequestConfig}
) => {

const {query: queryOptions, axios: axiosOptions} = options ?? {};

  const queryKey =  getGetImageQueryKey(filename);

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof getImage>>> = ({ signal }) => getImage(filename, { signal, ...axiosOptions });

      

      

   return  { queryKey, queryFn, enabled: computed(() => !!(unref(filename))), ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof getImage>>, TError, TData> 
}

export type GetImageQueryResult = NonNullable<Awaited<ReturnType<typeof getImage>>>
export type GetImageQueryError = AxiosError<unknown>


/**
 * @summary Get item image
 */

export function useGetImage<TData = Awaited<ReturnType<typeof getImage>>, TError = AxiosError<unknown>>(
 filename: MaybeRef<string>, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getImage>>, TError, TData>>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient 
 ): UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getGetImageQueryOptions(filename,options)

  const query = useQuery(queryOptions , queryClient) as UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = unref(queryOptions).queryKey as DataTag<QueryKey, TData, TError>;

  return query;
}



/**
 * Retrieves all items favorited by the current user
 * @summary Get user's favorites
 */
export const getUserFavorites = (
     options?: AxiosRequestConfig
 ): Promise<AxiosResponse<GetUserFavorites200>> => {
    
    
    return axios.default.get(
      `/api/items/favorites`,options
    );
  }


export const getGetUserFavoritesQueryKey = () => {
    return ['api','items','favorites'] as const;
    }

    
export const getGetUserFavoritesQueryOptions = <TData = Awaited<ReturnType<typeof getUserFavorites>>, TError = AxiosError<unknown>>( options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getUserFavorites>>, TError, TData>>, axios?: AxiosRequestConfig}
) => {

const {query: queryOptions, axios: axiosOptions} = options ?? {};

  const queryKey =  getGetUserFavoritesQueryKey();

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof getUserFavorites>>> = ({ signal }) => getUserFavorites({ signal, ...axiosOptions });

      

      

   return  { queryKey, queryFn, ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof getUserFavorites>>, TError, TData> 
}

export type GetUserFavoritesQueryResult = NonNullable<Awaited<ReturnType<typeof getUserFavorites>>>
export type GetUserFavoritesQueryError = AxiosError<unknown>


/**
 * @summary Get user's favorites
 */

export function useGetUserFavorites<TData = Awaited<ReturnType<typeof getUserFavorites>>, TError = AxiosError<unknown>>(
  options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getUserFavorites>>, TError, TData>>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient 
 ): UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getGetUserFavoritesQueryOptions(options)

  const query = useQuery(queryOptions , queryClient) as UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = unref(queryOptions).queryKey as DataTag<QueryKey, TData, TError>;

  return query;
}



/**
 * Retrieves all items belonging to a specific category
 * @summary Get items by category
 */
export const getItemsByCategory = (
    categoryId: MaybeRef<number>, options?: AxiosRequestConfig
 ): Promise<AxiosResponse<ItemResponseDTO[]>> => {
    categoryId = unref(categoryId);
    
    return axios.default.get(
      `/api/items/category/${categoryId}`,options
    );
  }


export const getGetItemsByCategoryQueryKey = (categoryId: MaybeRef<number>,) => {
    return ['api','items','category',categoryId] as const;
    }

    
export const getGetItemsByCategoryQueryOptions = <TData = Awaited<ReturnType<typeof getItemsByCategory>>, TError = AxiosError<unknown>>(categoryId: MaybeRef<number>, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getItemsByCategory>>, TError, TData>>, axios?: AxiosRequestConfig}
) => {

const {query: queryOptions, axios: axiosOptions} = options ?? {};

  const queryKey =  getGetItemsByCategoryQueryKey(categoryId);

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof getItemsByCategory>>> = ({ signal }) => getItemsByCategory(categoryId, { signal, ...axiosOptions });

      

      

   return  { queryKey, queryFn, enabled: computed(() => !!(unref(categoryId))), ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof getItemsByCategory>>, TError, TData> 
}

export type GetItemsByCategoryQueryResult = NonNullable<Awaited<ReturnType<typeof getItemsByCategory>>>
export type GetItemsByCategoryQueryError = AxiosError<unknown>


/**
 * @summary Get items by category
 */

export function useGetItemsByCategory<TData = Awaited<ReturnType<typeof getItemsByCategory>>, TError = AxiosError<unknown>>(
 categoryId: MaybeRef<number>, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getItemsByCategory>>, TError, TData>>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient 
 ): UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getGetItemsByCategoryQueryOptions(categoryId,options)

  const query = useQuery(queryOptions , queryClient) as UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = unref(queryOptions).queryKey as DataTag<QueryKey, TData, TError>;

  return query;
}



