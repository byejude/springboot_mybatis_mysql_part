package com.tulip.springboot_mybatis_mysql_part.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tulip.springboot_mybatis_mysql_part.annotation.ReadDataSource;
import com.tulip.springboot_mybatis_mysql_part.annotation.WriteDataSource;
import com.tulip.springboot_mybatis_mysql_part.dao.UserMapper;
import com.tulip.springboot_mybatis_mysql_part.domain.User;
import com.tulip.springboot_mybatis_mysql_part.service.UserService;
import com.tulip.springboot_mybatis_mysql_part.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void wirteAndRead(User u) {
        getService().insertUser(u);//这里走写库，那后面的读也都要走写库
        //这是刚刚插入的
        User uu = getService().findById(u.getId());
        log.info("==读写混合测试中的读(刚刚插入的)====id="+u.getId()+",  user_name=" + uu.getUserName());
        //为了测试,3个库中id=1的user_name是不一样的
        User uuu = getService().findById(u.getId());
        log.info("==读写混合测试中的读====id=2,  user_name=" + uuu.getUserName());
    }

    @Override
    public void readAndWirte(User u) {
        //为了测试,3个库中id=1的user_name是不一样的
        User uu = getService(). findById("2");
        log.info("==读写混合测试中的读====id=1,user_name=" + uu.getUserName());
        getService().insertUser(u);
    }
    @Override
    @ReadDataSource
    public User findById(String id) {
        User u = this.userMapper.findById(id);
        return u;
    }

    @Override
    @ReadDataSource
    public PageInfo<User> queryPage(String userName,int pageNum,int pageSize){
        Page<User> page = PageHelper.startPage(pageNum, pageSize);
        //PageHelper会自动拦截到下面这查询sql
        this.userMapper.query(userName);
        return page.toPageInfo();
    }

    @Override
    @WriteDataSource
    @Transactional(propagation= Propagation.REQUIRED,isolation= Isolation.DEFAULT,readOnly=false)
    public void insertUser(User u) {
        this.userMapper.insert(u);
    }

    private UserService getService(){
        return SpringContextUtil.getBean(this.getClass());
    }
}