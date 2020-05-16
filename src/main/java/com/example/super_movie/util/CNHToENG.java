package com.example.super_movie.util;

public class CNHToENG {
    public static String getCHNById(int state){
        switch (state){
            case(1):return "科幻";
            case(2):return "喜剧";
            case(3):return "恐怖";
            case(4):return "爱情";
            case(5):return "悬疑";
            case(6):return "动作";
            default:return "什么鬼";
        }
    }
}
