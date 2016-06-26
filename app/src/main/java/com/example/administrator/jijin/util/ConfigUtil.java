package com.example.administrator.jijin.util;

/**
 * Created by Administrator on 2016/4/27.
 */
public class ConfigUtil {
    private static String name="jijin";

    public static String path = "/data/data/com.zhongyuedu."+name+"/files/";
    //密码
    public static String mi_ma = "WCXLYHGYQLWWYLSWP2016";
    //sp保存
    public static String spSave = "com.zyedu."+name;
    //收藏数据库
    public static String saveFilename = "/data/data/com.zhongyuedu."+name+"/files" + "/save.db";//收藏数据库文件
    //保存考试类型
    public static String examTypeFileName = "/data/data/com.zhongyuedu."+name+"/files" + "/examType.db";
    //正式数据库
    public static String getNormalSqLite(int position) {
        return position+name+"Normal.db";
    }
    //错题数据库
    public static String getCuoSqLite(int position) {
        return position+name+"Cuo.db";
    }
    //习题数据库
    public static String getXiSqLite(int position) {
        return position+name+"Xi.db";
    }


    public static int alarmHour , alarmMinute ;
}
