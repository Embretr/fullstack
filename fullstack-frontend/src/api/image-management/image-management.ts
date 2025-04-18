/**
 * Generated by orval v7.8.0 🍺
 * Do not edit manually.
 * OpenAPI definition
 * OpenAPI spec version: v0
 */
import {
  useQuery
} from '@tanstack/vue-query';
import type {
  DataTag,
  QueryClient,
  QueryFunction,
  QueryKey,
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





/**
 * Retrieves an image file for an item
 * @summary Get item image
 */
export const getImage1 = (
    filename: MaybeRef<string>, options?: AxiosRequestConfig
 ): Promise<AxiosResponse<Blob>> => {
    filename = unref(filename);
    
    return axios.default.get(
      `/api/images/${filename}`,{
        responseType: 'blob',
    ...options,}
    );
  }


export const getGetImage1QueryKey = (filename: MaybeRef<string>,) => {
    return ['api','images',filename] as const;
    }

    
export const getGetImage1QueryOptions = <TData = Awaited<ReturnType<typeof getImage1>>, TError = AxiosError<unknown>>(filename: MaybeRef<string>, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getImage1>>, TError, TData>>, axios?: AxiosRequestConfig}
) => {

const {query: queryOptions, axios: axiosOptions} = options ?? {};

  const queryKey =  getGetImage1QueryKey(filename);

  

    const queryFn: QueryFunction<Awaited<ReturnType<typeof getImage1>>> = ({ signal }) => getImage1(filename, { signal, ...axiosOptions });

      

      

   return  { queryKey, queryFn, enabled: computed(() => !!(unref(filename))), ...queryOptions} as UseQueryOptions<Awaited<ReturnType<typeof getImage1>>, TError, TData> 
}

export type GetImage1QueryResult = NonNullable<Awaited<ReturnType<typeof getImage1>>>
export type GetImage1QueryError = AxiosError<unknown>


/**
 * @summary Get item image
 */

export function useGetImage1<TData = Awaited<ReturnType<typeof getImage1>>, TError = AxiosError<unknown>>(
 filename: MaybeRef<string>, options?: { query?:Partial<UseQueryOptions<Awaited<ReturnType<typeof getImage1>>, TError, TData>>, axios?: AxiosRequestConfig}
 , queryClient?: QueryClient 
 ): UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> } {

  const queryOptions = getGetImage1QueryOptions(filename,options)

  const query = useQuery(queryOptions , queryClient) as UseQueryReturnType<TData, TError> & { queryKey: DataTag<QueryKey, TData, TError> };

  query.queryKey = unref(queryOptions).queryKey as DataTag<QueryKey, TData, TError>;

  return query;
}



