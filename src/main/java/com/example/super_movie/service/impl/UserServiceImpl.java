package com.example.super_movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.entity.User;
import com.example.super_movie.mapper.UserMapper;
import com.example.super_movie.service.IUserService;
import com.example.super_movie.util.CodeUtil;
import com.example.super_movie.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author earun
 * @since 2020-01-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    UserMapper userDao;
    @Override
    public boolean doRegister(String username, String password, String email) {
        // 这里可以验证各字段是否为空
        if(username==null||password==null||email==null)
            return false;
        //利用正则表达式（可改进）验证邮箱是否符合邮箱的格式
        if(!email.matches("^\\w+@(\\w+\\.)+\\w+$")){
            return false;
        }
        if (userDao.getStateByeMail(email)!=null){


            if (userDao.getStateByeMail(email).getState()==0){
                //已注册未激活的账号
                System.out.println("已注册未激活");
                new Thread(new MailUtil(email, userDao.getStateByeMail(email).getCode())).start();
                return true;
            }
            if (userDao.getStateByeMail(email).getState()==1){
                //已激活的账号
                System.out.println("已激活账号");
                return false;
            }
        }
        System.out.println("激活码1");
        //生成激活码
        String code= CodeUtil.generateUniqueCode();
//		User user=new User(userName,email,password,0,code);
//		//将用户保存到数据库
//		UserDao userDao=new UserDaoImpl();
        //保存成功则通过线程的方式给用户发送一封邮件
        System.out.println("激活码2");
        if(userDao.save(username,email,password,0,code)>0){
            System.out.println("2222222----1111");
            new Thread(new MailUtil(email, code)).start();;
            return true;
        }
        return false;
    }

    @Override
    public boolean activeUser(String code) {
//		UserDao userDao=new UserDaoImpl();
        if(userDao.activeUser(code)>0){
            return true;
        }else{
            return false;
        }
    }

}
