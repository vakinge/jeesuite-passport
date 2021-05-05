import { defHttp } from '/@/utils/http/axios';
import { AccountBaseInfoModel } from './model/accountModel';

enum Api {
  ACCOUNT_INFO = '/account/baseInfo',
}

// Get personal center-basic settings

export const accountInfoApi = () => defHttp.get<AccountBaseInfoModel>({ url: Api.ACCOUNT_INFO });
