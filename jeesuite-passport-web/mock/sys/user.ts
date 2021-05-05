import { MockMethod } from 'vite-plugin-mock';
import { resultError, resultSuccess } from '../_util';
import { getToken } from '/@/utils/auth';

function createFakeUserList() {
  return [
    {
      id: '1',
      username: 'vben',
      nickname: 'Vben Admin',
      avatar: 'manager',
      password: '123456',
      token: 'fakeToken1',
    },
    {
      id: '2',
      username: 'test',
      nickname: 'xyz',
      password: '123456',
      avatar: 'test user',
      token: 'fakeToken2',
    },
  ];
}

const fakeCodeList: any = {
  '1': ['1000', '3000', '5000'],

  '2': ['2000', '4000', '6000'],
};
export default [
  // mock user login
  {
    url: '/basic-api/login',
    timeout: 200,
    method: 'post',
    response: ({ body }) => {
      const { account, password } = body;
      const checkUser = createFakeUserList().find(
        (item) => item.username === account && password === item.password
      );
      if (!checkUser) {
        return resultError('Incorrect account or password！');
      }
      return resultSuccess({
        uid: checkUser.id,
        token: checkUser.token
      });
    },
  },
  {
    url: '/basic-api/current_user',
    method: 'get',
    response: ({}) => {
      const token = getToken();
      const checkUser = createFakeUserList().find((item) => item.token === token);
      if (!checkUser) {
        return resultError('The corresponding user information was not obtained!');
      }
      return resultSuccess(checkUser);
    },
  },
  {
      url: '/basic-api/current_permssions',
      timeout: 200,
      method: 'get',
      response: () => {
        const codeList = ['1000', '3000', '5000'];
        return resultSuccess(codeList);
      },
    },
] as MockMethod[];
