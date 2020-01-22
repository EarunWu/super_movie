package com.example.super_movie.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author earun
 * @since 2020-01-13
 */
public class Movie{

    private static final long serialVersionUID = 1L;

    private int id;

    private String name;

    /**
     * 上映日期
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate time;

    private String country;

    /**
     * 电影长度
     */
    private Integer length;

    /**
     * 简介
     */
    private String info;

    public Movie(int id, String name, LocalDate time, String country, Integer length, String info) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.country = country;
        this.length = length;
        this.info = info;
    }

    public Movie() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
