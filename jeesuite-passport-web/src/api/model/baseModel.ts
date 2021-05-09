export interface BasicPageParams {
  page: number;
  pageSize: number;
}

export interface BasicFetchResult<T extends any> {
  items: T;
  total: number;
}


export interface TabItem {
  key: string;
  name: string;
  component: string;
}