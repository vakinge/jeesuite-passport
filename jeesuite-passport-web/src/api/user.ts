import { defHttp } from '/@/utils/http/axios';
import {
  LoginParams,
  LoginResultModel,
  UserInfoModel,
  UserDetailsModel
} from './model/userModel';

import { ErrorMessageMode } from '/@/utils/http/axios/types';

enum Api {
  Login = '/auth/login?ticket=',
  Logout = '/auth/logout',
  CurrentUser = '/auth/current_user',
  CurrentUserDetails = '/auth/user/baseInfo'
}

/**
 * @description: user login api
 */
export function loginApi(params: LoginParams, mode: ErrorMessageMode = 'modal',ticket: string = '') {
  return defHttp.post<LoginResultModel>(
    {
      url: Api.Login + ticket,
      params,
    },
    {
      errorMessageMode: mode,
    }
  );
}

/**
 * @description: user logout api
 */
export function logoutApi() {
  return defHttp.post<Any>(
    {
      url: Api.Logout
    },
  );
}

/**
 * @description: getCurrentUserInfo
 */
export function getCurrentUserInfo() {
  return defHttp.get<UserInfoModel>({
    url: Api.CurrentUser
  });
}

export function getCurrentUserDetails() {
  return defHttp.get<UserDetailsModel>({
    url: Api.CurrentUserDetails
  });
}

export function getCurrentPermCode() {
  return [];
}
