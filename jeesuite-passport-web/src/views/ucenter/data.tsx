export interface ListItem {
  title: string;
  icon: string;
  color?: string;
}

export interface TabItem {
  key: string;
  name: string;
  component: string;
}

export const tags: string[] = [
  '很有想法的',
  '专注设计',
  '川妹子',
  '大长腿',
  '海纳百川',
  '前端开发',
  'vue3',
];
<span class="iconify" data-icon="jam:codepen-circle" data-inline="false"></span>;
export const teams: ListItem[] = [
  {
    icon: 'ri:alipay-fill',
    title: '科学搬砖组',
    color: '#ff4000',
  },
  {
    icon: 'emojione-monotone:letter-a',
    title: '中二少年团',
    color: '#7c51b8',
  },
  {
    icon: 'ri:alipay-fill',
    title: '高逼格设计',
    color: '#00adf7',
  },
  {
    icon: 'jam:codepen-circle',
    title: '程序员日常',
    color: '#00adf7',
  },
  {
    icon: 'fa:behance-square',
    title: '科学搬砖组',
    color: '#7c51b8',
  },
  {
    icon: 'jam:codepen-circle',
    title: '程序员日常',
    color: '#ff4000',
  },
];

export const details: ListItem[] = [
  {
    icon: 'ic:outline-contacts',
    title: '交互专家',
  },
  {
    icon: 'grommet-icons:cluster',
    title: '某某某事业群',
  },
  {
    icon: 'bx:bx-home-circle',
    title: '福建省厦门市',
  },
];

export const achieveList: TabItem[] = [
  {
    key: '1',
    name: '设置',
    component: 'Settings',
  },
  {
    key: '2',
    name: '工作台',
    component: 'Workbench',
  },
  {
    key: '3',
    name: '登录日志',
    component: 'LoginLog',
  },
];

export const actions: any[] = [
  { icon: 'clarity:star-line', text: '156', color: '#018ffb' },
  { icon: 'bx:bxs-like', text: '156', color: '#459ae8' },
  { icon: 'bx:bxs-message-dots', text: '2', color: '#42d27d' },
];


export const systemList = (() => {
  const result: any[] = [];
  result.push({
    url: 'http://',
    title: '差旅系统',
  });
  result.push({
    url: 'http://',
    title: 'OA系统',
  });
  
  return result;
})();
