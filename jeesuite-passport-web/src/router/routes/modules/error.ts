import type { AppRouteModule } from '/@/router/types';

import { getParentLayout, LAYOUT } from '/@/router/constant';
import { ExceptionEnum } from '/@/enums/exceptionEnum';

const ExceptionPage = () => import('/@/views/sys/exception/Exception.vue');

const errorPage: AppRouteModule = {
  path: '/error',
  name: 'errorPage',
  component: LAYOUT,
  redirect: '/error/500',
  meta: {
    icon: 'ion:aperture-outline',
    title: 'Error',
  },
  children: [
        {
          path: '401',
          name: 'Unauthorized',
          component: ExceptionPage,
          props: {
            status: ExceptionEnum.Unauthorized,
          },
          meta: {
            title: '401',
          },
        },
        {
          path: '403',
          name: 'PageNotAccess',
          component: ExceptionPage,
          props: {
            status: ExceptionEnum.PAGE_NOT_ACCESS,
          },
          meta: {
            title: '403',
          },
        },
        {
          path: '404',
          name: 'PageNotFound',
          component: ExceptionPage,
          props: {
            status: ExceptionEnum.PAGE_NOT_FOUND,
          },
          meta: {
            title: '404',
          },
        },
        {
          path: '500',
          name: 'ServiceError',
          component: ExceptionPage,
          props: {
            status: ExceptionEnum.ERROR,
          },
          meta: {
            title: '500',
          },
        },
        {
          path: 'networkerror',
          name: 'NetWorkError',
          component: ExceptionPage,
          props: {
            status: ExceptionEnum.NET_WORK_ERROR,
          },
          meta: {
            title: '网络错误',
          },
        },
        {
          path: 'nodata',
          name: 'NotData',
          component: ExceptionPage,
          props: {
            status: ExceptionEnum.PAGE_NOT_DATA,
          },
          meta: {
            title: '无数据',
          },
        },
      ],
};

export default errorPage;
