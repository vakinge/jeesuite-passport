import type { UserInfo } from '/#/store';
import type { ErrorMessageMode } from '/@/utils/http/axios/types';

import { defineStore } from 'pinia';
import { store } from '/@/store';

import { RoleEnum } from '/@/enums/roleEnum';
import { PageEnum } from '/@/enums/pageEnum';
import { ROLES_KEY, TOKEN_KEY, USER_INFO_KEY } from '/@/enums/cacheEnum';

import { getAuthCache, setAuthCache } from '/@/utils/auth';
import {
  UserInfoModel,
  LoginParams,
} from '/@/api/model/userModel';

import { getCurrentUserInfo, loginApi ,logoutApi} from '/@/api/user';

import { useI18n } from '/@/hooks/web/useI18n';
import { useMessage } from '/@/hooks/web/useMessage';
import router from '/@/router';
import axios from 'axios'

interface UserState {
  userInfo: Nullable<UserInfo>;
  token?: string;
  roleList: RoleEnum[];
}

export const useUserStore = defineStore({
  id: 'app-user',
  state: (): UserState => ({
    // user info
    userInfo: null,
    // token
    token: undefined,
    // roleList
    roleList: [],
  }),
  getters: {
    getUserInfo(): UserInfo {
      return this.userInfo || getAuthCache<UserInfo>(USER_INFO_KEY) || {};
    },
    getToken(): string {
      return this.token || getAuthCache<string>(TOKEN_KEY);
    },
    getRoleList(): RoleEnum[] {
      return this.roleList.length > 0 ? this.roleList : getAuthCache<RoleEnum[]>(ROLES_KEY);
    },
  },
  actions: {
    setToken(info: string) {
      this.token = info;
      setAuthCache(TOKEN_KEY, info);
    },
    setUserInfo(info: UserInfo) {
      this.userInfo = info;
      setAuthCache(USER_INFO_KEY, info);
    },
    resetState() {
      this.userInfo = null;
      this.token = '';
    },
    /**
     * @description: login
     */
    async login(
      params: LoginParams & {
        mode?: ErrorMessageMode;
      }
    ): Promise<UserInfoModel | null> {
      try {
        const ticket = params.ticket;
        delete params.ticket;
        const { mode, ...loginParams } = params;
        const data = await loginApi(loginParams, mode,ticket);
        const { token ,redirect} = data;
        // save token
        this.setToken(token);
        // get user info
        const userInfo = await getCurrentUserInfo();
        this.setUserInfo(userInfo);
        if(redirect){
          window.location.href = redirect;
        }else{
          await router.replace(PageEnum.BASE_HOME)
        }
        return userInfo;
      } catch (error) {
        return null;
      }
    },
    /**
     * @description: logout
     */
    async logout(goLogin = false) {
      const data = await logoutApi();
      this.resetState();
      if(data.redirect){
        window.location.href = data.redirect;
      }else{
        goLogin && router.push(PageEnum.BASE_LOGIN);
      }
    },

    /**
     * @description: Confirm before logging out
     */
    confirmLoginOut() {
      const { createConfirm } = useMessage();
      const { t } = useI18n();
      createConfirm({
        iconType: 'warning',
        title: t('sys.app.logoutTip'),
        content: t('sys.app.logoutMessage'),
        onOk: async () => {
          await this.logout(true);
        },
      });
    },
    async checkLoginStatus(){
       const token = this.getToken;
       const response = await axios.get('/auth/status');
       let data = response.data;
       if(data.code == 200){
         const currentToken = data.data.access_token;
         if(currentToken !== token){
           console.log("new token:" + currentToken + ",oldToken:" + token);
            // save token
            this.setToken(currentToken);
            // get user info
            const userInfo = await getCurrentUserInfo();
            this.setUserInfo(userInfo);
         }
         router.push(PageEnum.BASE_HOME);
       }else if(data.code == 401){
          this.resetState();
          router.push(PageEnum.BASE_LOGIN);
       }
    },
  },
});

// Need to be used outside the setup
export function useUserStoreWidthOut() {
  return useUserStore(store);
}
