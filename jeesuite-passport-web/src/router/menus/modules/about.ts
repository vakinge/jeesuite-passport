import type { MenuModule } from '/@/router/types';
import { t } from '/@/hooks/web/useI18n';

const ucenterMenu: MenuModule = {
  orderNo: 100000,
  menu: {
    path: '/ucenter/index',
    name: '用户中心',
  },
};
export default ucenterMenu;
