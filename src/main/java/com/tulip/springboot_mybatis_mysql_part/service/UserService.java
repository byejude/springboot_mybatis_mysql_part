package com.tulip.springboot_mybatis_mysql_part.service;


import com.github.pagehelper.PageInfo;
import com.tulip.springboot_mybatis_mysql_part.domain.User;


public interface UserService {
     void wirteAndRead(User u);

     void readAndWirte(User u);

     User findById(String id);

     PageInfo<User> queryPage(String userName, int pageNum, int pageSize);

     void insertUser(User u);
}
