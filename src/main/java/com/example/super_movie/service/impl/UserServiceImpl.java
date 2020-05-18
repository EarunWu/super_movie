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
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public int doRegister(String username, String password, String email) {
        // 这里可以验证各字段是否为空
        if(username==null||password==null||email==null)
            return -1;
        //利用正则表达式（可改进）验证邮箱是否符合邮箱的格式
        if(!email.matches("^\\w+@(\\w+\\.)+\\w+$")){
            return -1;
        }
        if (redisUtil.sHasKey("userEmail",email)){
            return 0;
        }
        String code= CodeUtil.generateUniqueCode();
        if(getBaseMapper().save(username,email, DigestUtils.md5DigestAsHex(password.getBytes()),0,code)>0){
            redisUtil.sSet("userEmail",email);
            new Thread(new MailUtil(email, code)).start();
            return 1;
        }
        return 2;
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
            //redis中没有记录，批量插入用户的关注记录到redis set
            List<String> a=getBaseMapper().getFollowByUserId(userId);
            redisService.insertKey(a,"follow"+userId);
            //插入个0值防止没有数据时被清空
            redisUtil.sSet("follow"+userId,0);
            redisUtil.expire("follow"+userId,60*60*24*7);
        }
        //返回查询结果
        return redisUtil.sHasKey("follow"+userId,followId);
    }
    public int follow(int userId,int followId){
        if (userId==followId)
            return 0;
        if (redisUtil.sSet("follow"+userId,followId)==1){
            //关注成功操作，点赞数加1
            redisUtil.zSetInc("followNum",followId,1);
            //存入新的点赞记录hash，以备导入数据库
            redisUtil.hset("newFollow",userId+"_"+followId,1);
            return 1;
        }else {
            //已点关注，转为取消关注
            redisUtil.zSetInc("followNum",followId,-1);
            redisUtil.setRemove("follow"+userId,followId);
            //存入新的关注记录hash，以备导入数据库
            redisUtil.hset("newFollow",userId+"_"+followId,0);
            return -1;
        }
    }
//userId是被被关注者，id是关注者
    public UserInfo getUserInfoById(int userId, int id){
            //检测目标用户是否存在
         if (!redisUtil.getBit("userState",userId))
             return UserInfo.userInfo0;
         UserInfo userInfo=(UserInfo) redisUtil.get("userInfo"+userId);
         if (userInfo==null){
             userInfo=getBaseMapper().getUserInfoById(userId);
             redisUtil.set("userInfo"+userId,userInfo,60*60*24);
         }
         userInfo.setFollowNum(getFollowNum(userId));
         userInfo.setFollowState(getFollowState(id,userId));
        return userInfo;
    }
    //获取黑名单
    public List<UserInfo> getBlackList(int userId){
        Set set=redisUtil.sGet("userBlackList"+userId);
        List list0=new ArrayList(set);
        List<String> keys = new ArrayList<>();
        for (int i=0;i<list0.size();i++){
            keys.add("userInfo"+list0.get(i));
        }
        Object ob=redisUtil.get(keys);
        List<UserInfo> list=(List<UserInfo>) ob;
        for (int i=0;i<list.size();i++){
            if (list.get(0)==null)
                list.set(i,getUserInfoById((int)list0.get(i),1));
        }
        return list;
    }

    public int addBlackList(int userId,int id){
        //是否存在此用户
        if (!redisUtil.getBit("userState",id))
            return 0;
        //加入黑名单
        if (redisUtil.sSet("userBlackList"+userId,id)==1){
            redisUtil.hincr("number","userBlackList"+userId,1);
            return 1;
        }
        //加入失败说明已加入
        return -1;

    }

    public int removeBlackList(int userId,int id){
        if (redisUtil.setRemove("userBlackList"+userId,id)==1){
            redisUtil.hincr("number","userBlackList"+userId,-1);
            return 1;
        }
        return 0;
    }

    public int updateUserInfo(String username,String introduction,int userId){
        if (getBaseMapper().updateUserInfo(username, introduction, userId)>0){
            redisUtil.del("userInfo"+userId);
            return 1;
        }
        return 0;
    }

    public int updatePassword(String newPassword,int userId,String password){
        //如果怕有人疯狂爆破修改密码可以把密码存在redis一份
        return getBaseMapper().updatePassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()), userId, DigestUtils.md5DigestAsHex(password.getBytes()));
    }
    public Integer login(String email,String password){
        User user=getBaseMapper().getStateByeMail(email);
        if (!redisUtil.getBit("userState",user.getId()))
            return 0;
        if (DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword()))
            return user.getId();
        return null;
    }
    public int banUser(Integer userId){
        int num=(int)redisUtil.hincr("userBanTimes",userId.toString(),1 );
        if (num>10)
            return redisUtil.setBit("userState",userId,false)?1:0;
        return getBaseMapper().banUser(userId, LocalDateTime.now().plusDays(num));
    }


}
