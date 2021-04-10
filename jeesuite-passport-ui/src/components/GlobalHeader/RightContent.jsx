import { Tooltip, Tag } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';
import React from 'react';
import { connect, SelectLang } from 'umi';
import Avatar from './AvatarDropdown';
import styles from './index.less';
import NoticeIconView from './NoticeIconView';
const ENVTagColor = {
  dev: 'orange',
  test: 'green',
  pre: '#87d068',
};

const GlobalHeaderRight = (props) => {
  const { theme, layout } = props;
  let className = styles.right;

  if (theme === 'dark' && layout === 'top') {
    className = `${styles.right}  ${styles.dark}`;
  }

  return (
    <div className={className}>
      <Tooltip title="使用文档">
        <a
          style={{
            color: 'inherit',
          }}
          target="_blank"
          href="https://pro.ant.design/docs/getting-started"
          rel="noopener noreferrer"
          className={styles.action}
        >
          <QuestionCircleOutlined />
        </a>
      </Tooltip>
      <NoticeIconView />
      <Avatar menu />
      {REACT_APP_ENV && (
        <span>
          <Tag color={ENVTagColor[REACT_APP_ENV]}>{REACT_APP_ENV}</Tag>
        </span>
      )}
      <SelectLang className={styles.action} />
    </div>
  );
};

export default connect(({ settings }) => ({
  theme: settings.navTheme,
  layout: settings.layout,
}))(GlobalHeaderRight);
