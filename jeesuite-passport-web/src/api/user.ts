import { defHttp } from '/@/utils/http/axios';
import {
  LoginParams,
  LoginResultModel,
  UserInfoModel,
  UserDetailsModel
} from './model/userModel';

import { ErrorMessageMode } from '/@/utils/http/axios/types';

enum Api {
  Login = '/user/login',
  CurrentUser = '/user/current',
  CurrentUserDetails = '/user/details'
}

/**
 * @description: user login api
 */
export function loginApi(params: LoginParams, mode: ErrorMessageMode = 'modal') {
  return defHttp.post<LoginResultModel>(
    {
      url: Api.Login,
      params,
    },
    {
      errorMessageMode: mode,
    }
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
