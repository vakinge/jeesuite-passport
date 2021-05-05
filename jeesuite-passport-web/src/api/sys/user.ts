import { defHttp } from '/@/utils/http/axios';
import {
  LoginParams,
  LoginResultModel,
  UserInfoModel
} from './model/userModel';

import { ErrorMessageMode } from '/@/utils/http/axios/types';

enum Api {
  Login = '/login',
  CurrentUser = '/current_user',
  CurrentPermssions = '/current_permssions'
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

export function getCurrentPermCode() {
  return defHttp.get<string[]>({
    url: Api.CurrentPermssions
  });
}

