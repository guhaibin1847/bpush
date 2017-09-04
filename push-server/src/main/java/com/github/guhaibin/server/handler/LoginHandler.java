package com.github.guhaibin.server.handler;

import com.github.guhaibin.api.MessageHandler;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.push.PushResult;
import com.github.guhaibin.api.push.User;
import com.github.guhaibin.api.push.UserType;
import com.github.guhaibin.api.spi.common.CacheManager;
import com.github.guhaibin.api.spi.service.DataService;
import com.github.guhaibin.core.message.LoginMessage;
import com.github.guhaibin.utils.common.IdGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class LoginHandler implements MessageHandler<LoginMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);

    private DataService dataService;

    public LoginHandler(DataService dataService){
        this.dataService = dataService;
    }

    @Override
    public void handle(LoginMessage message, Connection connection) {

        LOG.trace("get login message. message is {}", message);

        if (validate(message)){
            // 登录成功，需要写入一些信息
            UserType userType = message.getUserType();
            String username = message.getUsername();
            User user = new User();
            user.setUserType(userType);
            user.setUsername(username);
            dataService.addUser(user, connection.getId(), message.getTag());

            connection.send(message.ack(PushResult.SUCCESS));
        }else {
            connection.send(message.ack(PushResult.FAILELD));
        }
    }


    private boolean validate(LoginMessage message){
        UserType userType = message.getUserType();
        String username = message.getUsername();
        String password = message.getPassword();
        String validatePwd = IdGen.genPwd(new User(userType, username));

        if (validatePwd.equals(password)){
            return true;
        }
        return false;
    }
}
