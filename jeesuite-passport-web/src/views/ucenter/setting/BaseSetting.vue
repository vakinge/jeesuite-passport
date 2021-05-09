<template>
  <CollapseContainer title="基本设置" :canExpan="false">
    <a-row :gutter="24">
      <a-col :span="16">
        <BasicForm @register="register" />
      </a-col>
      <a-col :span="8">
        <div class="change-avatar">
          <div class="mb-2"></div>
          <img width="140" :src="currentUser.avatar" />
          <Upload :showUploadList="false">
            <Button class="ml-5"> <Icon icon="feather:upload" />更换头像 </Button>
          </Upload>
        </div>
      </a-col>
    </a-row>
    <Button type="primary" @click="handleSubmit"> 更新基本信息 </Button>
  </CollapseContainer>
</template>
<script lang="ts">
  import { Button, Upload, Row, Col } from 'ant-design-vue';
  import { defineComponent, onMounted, computed } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { CollapseContainer } from '/@/components/Container/index';
  import Icon from '/@/components/Icon/index';

  import { useMessage } from '/@/hooks/web/useMessage';

  import { getCurrentUserDetails } from '/@/api/user';
  import { baseSetschemas } from './data';
  import { useUserStore } from '/@/store/modules/user';

  export default defineComponent({
    components: {
      BasicForm,
      CollapseContainer,
      Button,
      Upload,
      Icon,
      [Row.name]: Row,
      [Col.name]: Col,
    },
    setup() {
      const { createMessage } = useMessage();
      const userStore = useUserStore();
      const currentUser = computed(() => userStore.getUserInfo);
      const [register, { setFieldsValue }] = useForm({
        labelWidth: 120,
        schemas: baseSetschemas,
        showActionButtonGroup: false,
      });

      onMounted(async () => {
        const data = await getCurrentUserDetails();
        setFieldsValue(data);
      });

      return {
        currentUser,
        register,
        handleSubmit: () => {
          createMessage.success('更新成功！');
        },
      };
    },
  });
</script>

<style lang="less" scoped>
  .change-avatar {
    img {
      display: block;
      margin-bottom: 15px;
      border-radius: 50%;
    }
  }
</style>
