/**
 * @description: Login interface parameters
 */
export interface LoginParams {
  loginType: number;
  account: string;
  password: string;
  code: string;
}

/**
 * @description: Login interface return value
 */
export interface LoginResultModel {
  uid: string | number;
  token: string;
  redirect: string;
}

/**
 * @description: Get user information return value
 */
export interface UserInfoModel {
  id: string | number;
  name: string;
  nickname: string;
  avatar: string;
}
