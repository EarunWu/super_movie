package com.example.super_movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.entity.User;
import com.example.super_movie.mapper.UserMapper;
import com.example.super_movie.service.IUserService;
import com.example.super_movie.util.CodeUtil;
import com.example.super_movie.util.MailUtil;
import com.example.super_movie.util.RedisService;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    RedisUtil redisUtil;
    @Autowired
    RedisService redisService;
    @Override
    public boolean doRegister(String username, String password, String email) {
        // 这里可以验证各字段是否为空
        if(username==null||password==null||email==null)
            return false;
        //利用正则表达式（可改进）验证邮箱是否符合邮箱的格式
        if(!email.matches("^\\w+@(\\w+\\.)+\\w+$")){
            return false;
        }
        if (getBaseMapper().getStateByeMail(email)!=null){


            if (getBaseMapper().getStateByeMail(email).getState()==0){
                //已注册未激活的账号
                System.out.println("已注册未激活");
                new Thread(new MailUtil(email, getBaseMapper().getStateByeMail(email).getCode())).start();
                return true;
            }
            if (getBaseMapper().getStateByeMail(email).getState()==1){
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
        if(getBaseMapper().save(username,email,password,0,code)>0){
            System.out.println("2222222----1111");
            new Thread(new MailUtil(email, code)).start();;
            return true;
        }
        return false;
    }

    @Override
    public boolean activeUser(String code) {
//		UserDao userDao=new UserDaoImpl();
        if(getBaseMapper().activeUser(code)>0){
            return true;
        }else{
            return false;
        }
    }

    public int getFollowNum(int userId){
        Double d=redisUtil.zGetScore("followNum",userId);
     return d==null?0:d.intValue();
    }

    public boolean getFollowState(int userId,int followId){
        if (!redisUtil.hasKey("follow"+userId)){
            //redis中没有记录，批量插入影评当天的点赞记录到redis set
            List<String> a=getBaseMapper().getFollowByUserId(userId);
            redisService.insertKey(a,"follow"+userId);
            redisUtil.expire("follow"+userId,60*60*24*7);
        }
        //返回查询结果
        return redisUtil.sHasKey("follow"+userId,followId);
    }
    public int follow(int userId,int followId){
        if (userId==followId)
            return 0;
        if (redisUtil.sSet("follow"+userId,followId)==1){
            //点赞成功操作，点赞数加1
            redisUtil.zSetInc("followNum",followId,1);
            //存入新的点赞记录hash，以备导入数据库
            redisUtil.hset("newFollow",userId+"_"+followId,1);
            return 1;
        }else {
            //已点过赞，转为取消点赞
            redisUtil.zSetInc("followNum",followId,-1);
            redisUtil.setRemove("follow"+userId,followId);
            //存入新的点赞记录hash，以备导入数据库
            redisUtil.hset("newFollow",userId+"_"+followId,0);
            return -1;
        }
    }
//userId是被被关注者，id是关注者
    public UserInfo getUserInfoById(int userId,int id){
        //如果怕被缓存穿透可以设置一个布隆过滤器
         UserInfo userInfo=(UserInfo) redisUtil.get("userInfo"+userId);
         if (userInfo==null){
             userInfo=getBaseMapper().getUserInfoById(userId);
             redisUtil.set("userInfo"+userId,userInfo,60*60*24);
         }
         userInfo.setFollowNum(getFollowNum(userId));
         userInfo.setFollowState(getFollowState(id,userId));
        return userInfo;
    }

}
