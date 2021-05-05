import type { AppRouteRecordRaw, Menu } from '/@/router/types';

import { defineStore } from 'pinia';
import { store } from '/@/store';
import { useI18n } from '/@/hooks/web/useI18n';
import { useUserStore } from './user';
import { useAppStoreWidthOut } from './app';
import { toRaw } from 'vue';

import projectSetting from '/@/settings/projectSetting';

import { PermissionModeEnum } from '/@/enums/appEnum';

import { asyncRoutes } from '/@/router/routes';
import { ERROR_LOG_ROUTE, PAGE_NOT_FOUND_ROUTE } from '/@/router/routes/basic';

import { filter } from '/@/utils/helper/treeHelper';

import { getCurrentPermCode } from '/@/api/sys/user';

import { useMessage } from '/@/hooks/web/useMessage';

interface PermissionState {
  // Permission code list
  permCodeList: string[];
  // Whether the route has been dynamically added
  isDynamicAddedRoute: boolean;
}
export const usePermissionStore = defineStore({
  id: 'app-permission',
  state: (): PermissionState => ({
    permCodeList: [],
    // Whether the route has been dynamically added
    isDynamicAddedRoute: false,
  }),
  getters: {
    getPermCodeList() {
      return this.permCodeList;
    },
    getIsDynamicAddedRoute() {
      return this.isDynamicAddedRoute;
    },
  },
  actions: {
    setPermCodeList(codeList: string[]) {
      this.permCodeList = codeList;
    },
    setDynamicAddedRoute(added: boolean) {
      this.isDynamicAddedRoute = added;
    },
    resetState(): void {
      this.isDynamicAddedRoute = false;
      this.permCodeList = [];
    },
    async changePermissionCode(userId: string) {
      const codeList = await getCurrentPermCode({ userId });
      this.setPermCodeList(codeList);
    },
    async buildRoutesAction(id?: number | string): Promise<AppRouteRecordRaw[]> {
      const { t } = useI18n();
      const userStore = useUserStore();
      const appStore = useAppStoreWidthOut();

      let routes: AppRouteRecordRaw[] = [];
      const roleList = toRaw(userStore.getRoleList);
      const { permissionMode = projectSetting.permissionMode } = appStore.getProjectConfig;
      // role permissions
      if (permissionMode === PermissionModeEnum.ROLE) {
        const routeFilter = (route: AppRouteRecordRaw) => {
          const { meta } = route;
          const { roles } = meta || {};
          if (!roles) return true;
          return roleList.some((role) => roles.includes(role));
        };
        routes = filter(asyncRoutes, routeFilter);
        routes = routes.filter(routeFilter);
      } else if (permissionMode === PermissionModeEnum.BACK) {
        let routeList: AppRouteRecordRaw[] = [];
        routes = [PAGE_NOT_FOUND_ROUTE, ...routeList];
      }
      routes.push(ERROR_LOG_ROUTE);
      return routes;
    },
  },
});

// Need to be used outside the setup
export function usePermissionStoreWidthOut() {
  return usePermissionStore(store);
}
