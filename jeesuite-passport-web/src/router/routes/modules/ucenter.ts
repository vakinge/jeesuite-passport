import type { AppRouteModule } from '/@/router/types';

import { LAYOUT } from '/@/router/constant';

const ucenter: AppRouteModule = {
  path: '/ucenter',
  name: 'Ucenter',
  component: LAYOUT,
  redirect: '/ucenter/index',
  meta: {
    icon: 'ion:grid-outline',
    title: '用户中心',
  },
  children: [
        {
          path: 'index',
          name: 'Ucenter',
          component: () => import('/@/views/ucenter/index.vue'),
          meta: {
            title: '用户中心',
          },
        },
      ],
};

export default ucenter;
