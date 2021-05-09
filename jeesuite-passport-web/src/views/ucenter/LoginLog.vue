<template>
  <div class="p-4">
    <BasicTable title="登录日志" titleHelpMessage="温馨提醒" :columns="columns" :dataSource="data" :canResize="canResize"
      :loading="loading" :striped="striped" :bordered="border" showTableSetting :pagination="pagination">
    </BasicTable>
  </div>
</template>
<script lang="ts">
  import {
    defineComponent,
    ref
  } from 'vue';
  import {
    BasicTable
  } from '/@/components/Table';

  export default defineComponent({
    components: {
      BasicTable
    },
    setup() {
      const canResize = ref(false);
      const loading = ref(false);
      const striped = ref(true);
      const border = ref(true);
      const pagination = ref < any > (false);

      function toggleCanResize() {
        canResize.value = !canResize.value;
      }

      function toggleStriped() {
        striped.value = !striped.value;
      }

      function toggleLoading() {
        loading.value = true;
        setTimeout(() => {
          loading.value = false;
          pagination.value = {
            pageSize: 20
          };
        }, 3000);
      }

      function toggleBorder() {
        border.value = !border.value;
      }
      return {
        columns: [{
            title: 'ID',
            dataIndex: 'id',
            fixed: 'left',
            width: 200,
          },
          {
            title: '登录方式',
            dataIndex: 'loginType',
          },
          {
            title: '浏览器',
            dataIndex: 'userAgent',
            width: 150,
          },
          {
            title: '是否成功',
            width: 120,
            sorter: false,
            dataIndex: 'loginResult',
          },
          {
            title: '登录IP',
            width: 120,
            sorter: false,
            dataIndex: 'loginIp',
          },
          {
            title: '登录时间',
            width: 120,
            sorter: false,
            dataIndex: 'loginTime',
          },
        ],
        data: [],
        canResize,
        loading,
        striped,
        border,
        toggleStriped,
        toggleCanResize,
        toggleLoading,
        toggleBorder,
        pagination,
      };
    },
  });
</script>
