package com.example.super_movie.service.impl;

import com.example.super_movie.entity.Admin;
import com.example.super_movie.mapper.AdminMapper;
import com.example.super_movie.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.SelectMovieList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author earun
 * @since 2020-05-15
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {
    @Autowired
    RedisUtil redisUtil;

    public boolean pushMovieToRecommend(int id){
        SelectMovieList movie=getBaseMapper().getASelectMovie(id);
        return redisUtil.lSet("recommend",movie);
    }

    public boolean removeAllRecommend(){
        try {
            redisUtil.del("recommend");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean banUser(int userId){
        return redisUtil.setBit("userState",userId,false);
    }

    public boolean unBanUser(int userId){
        return redisUtil.setBit("userState",userId,true);
    }

    public boolean banMovieComment(int id){
        return redisUtil.setBit("commentState",id,false);
    }

    public boolean unBanMovieComment(int id){
        return redisUtil.setBit("commentState",id,true);
    }

    public boolean removeReply(int id){
        return getBaseMapper().deleteReply(id)>0;
    }



}
