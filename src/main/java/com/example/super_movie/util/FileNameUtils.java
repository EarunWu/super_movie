package com.example.super_movie.util;

public class FileNameUtils {

    /**
     * 获取文件后缀
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName){
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 生成新的文件名
     * @param fileOriginName 源文件名
     * @return
     */
    public static String getFileName(String fileOriginName){
        return UUIDUtils.getUUID() + FileNameUtils.getSuffix(fileOriginName);
    }

    /**
     * 生成头像文件名
     * @param fileOriginName 源文件名
     * @return
     */
    public static String getHeadFileName(String fileOriginName,int userId){
//        return "userHead"+userId + FileNameUtils.getSuffix(fileOriginName);
        return "userHead"+userId + ".jpg";
    }
    public static String getMovieFileName(String fileOriginName,int movieId){
        return "movie"+movieId + ".jpg";
    }

}
