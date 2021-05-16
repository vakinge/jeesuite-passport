<template>
  <div :class="prefixCls">
    <a-row>
      <a-col :span="5" :class="`${prefixCls}-left`">
        <a-row>
          <a-col :span="16">
            <div :class="`${prefixCls}-left__avatar`">
              <img :src="currentUser.avatar" />
              <span>{{currentUser.realname ? currentUser.realname : currentUser.nickname}}</span>
              <div>海纳百川，有容乃大</div>
            </div>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="19">
            <div :class="`${prefixCls}-left__detail`">
              <p v-if="currentUser.employeeId">
                <Icon icon="ic:outline-contacts" />
                {{ currentUser.employeeId }}
              </p>
              <p v-if="currentUser.departmentName">
                <Icon icon="ant-design:tags-filled" />
                {{ currentUser.departmentName }}
              </p>
              <p v-if="currentUser.postName">
                <Icon icon="ant-design:paper-clip-outlined" />
                {{ currentUser.postName }}
              </p>
            </div>
          </a-col>
        </a-row>

      </a-col>
      <a-col :span="18">
        <div :class="`${prefixCls}-right`">
          <Tabs>
            <template v-for="item in tabItems" :key="item.key">
              <TabPane :tab="item.name">
                <component :is="item.component" />
              </TabPane>
            </template>
          </Tabs>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts">
  import {
    Tag,
    Tabs,
    Row,
    Col
  } from 'ant-design-vue';
  import {
    defineComponent,computed
  } from 'vue';
  import {
    CollapseContainer
  } from '/@/components/Container/index';
  import Icon from '/@/components/Icon/index';
  import Settings from './Settings.vue';
  import Workbench from './Workbench.vue';
  import LoginLog from './LoginLog.vue';
  import { TabItem } from '/@/api/model/baseModel';
  import {
    useUserStore
  } from '/@/store/modules/user';

  export default defineComponent({
    name: 'Ucenter',
    components: {
      CollapseContainer,
      Icon,
      Tag,
      Tabs,
      TabPane: Tabs.TabPane,
      Settings,
      Workbench,
      LoginLog,
      [Row.name]: Row,
      [Col.name]: Col,
    },
    setup() {
      const userStore = useUserStore();
      const currentUser = computed(() => userStore.getUserInfo);
      const tabItems: TabItem[] = [
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
      ]
      return {
        prefixCls: 'account-center',
        currentUser,
        tabItems,
      };
    },
  });
</script>
<style lang="less" scoped>
  .account-center {
    &-col:not(:last-child) {
      padding: 0 10px;

      &:not(:last-child) {
        border-right: 1px dashed rgb(206, 206, 206, 0.5);
      }
    }

    &-left {
      padding: 10px;
      margin: 16px 16px 12px 16px;
      background-color: @component-background;
      border-radius: 3px;

      &__avatar {
        text-align: center;

        img {
          border-radius: 50%;
          width: 100px;
          height: 100px;
          margin-bottom: 20px;
        }

        span {
          display: block;
          font-size: 20px;
          font-weight: 500;
        }

        div {
          margin-left: 3px;
          font-size: 12px;
        }
      }

      &__detail {
        padding-left: 10px;
        margin-left: 15px;
      }

      &__team {
        &-item {
          display: inline-block;
          padding: 4px 24px;
        }

        span {
          margin-left: 3px;
        }
      }
    }

    &-right {
      padding: 10px;
      margin: 16px 16px 16px 16px;
      background-color: @component-background;
      border-radius: 3px;
    }
  }
</style>
