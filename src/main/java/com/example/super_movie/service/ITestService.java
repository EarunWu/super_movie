package com.example.super_movie.service;

import com.example.super_movie.entity.Test;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-01-12
 */
public interface ITestService extends IService<Test> {
    String getMagic(Integer id);
}
