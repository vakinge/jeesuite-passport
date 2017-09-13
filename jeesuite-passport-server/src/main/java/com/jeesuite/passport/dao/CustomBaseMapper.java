package com.jeesuite.passport.dao;

import tk.mybatis.mapper.common.ExampleMapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface CustomBaseMapper<T> extends tk.mybatis.mapper.common.BaseMapper<T>,ExampleMapper<T>,MySqlMapper<T> {

}
