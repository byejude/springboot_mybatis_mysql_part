package com.tulip.springboot_mybatis_mysql_part.service;

import com.tulip.springboot_mybatis_mysql_part.domain.User;
import com.tulip.springboot_mybatis_mysql_part.service.Impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void insertUser() {
        User user = new User("1","test");
        userService.insertUser(user);
    }

    @Test
    public void wirteAndRead() {
        User user = new User("8","wirteAndRead5");
        userService.wirteAndRead(user);
    }

    @Test
    public void readAndWirte() {
    }

    @Test
    public void findById() {
        User user = userService.findById("1");
        log.info("findById"+user.toString());
    }

    @Test
    public void queryPage() {
    }
}