package com.example.super_movie.service.impl;

import com.example.super_movie.entity.Test;
import com.example.super_movie.mapper.TestMapper;
import com.example.super_movie.service.ITestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements ITestService {
    @Autowired
    TestMapper testMapper;
    public String getMagic(Integer id){
        return testMapper.getMagic(id);
    }

}
