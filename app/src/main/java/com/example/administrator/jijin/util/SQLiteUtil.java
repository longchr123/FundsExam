package com.example.administrator.jijin.util;
//习题数据库的使用，包括试用和正式的数据库

import android.content.ContentValues;
import android.util.Log;

import com.example.administrator.jijin.bean.ExamSmallItem;
import com.example.administrator.jijin.bean.ProgressItem;
import com.example.administrator.jijin.bean.TestItem;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/25.
 */
public class SQLiteUtil {

    //获取数据库表中文名
    public static List<String> getTableChineseName(SQLiteDatabase database) {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query("tableNames", null, null, null, null, null, null, null);
        //利用游标遍历所有数据对象
        while (cursor.moveToNext()) {
            String chineseName = cursor.getString(cursor.getColumnIndex("ChineseName"));
            list.add(chineseName);
        }
        cursor.close();
        return list;
    }

    //获取数据库表中文名
    public static List<ProgressItem> getProgressItem(SQLiteDatabase database,boolean isXiTi) {

        List<ProgressItem> list = new ArrayList<>();
        Cursor cursor = database.query("tableNames", null, null, null, null, null, null, null);
        //利用游标遍历所有数据对象
        while (cursor.moveToNext()) {
            ProgressItem item =new ProgressItem();
            String chineseName = cursor.getString(cursor.getColumnIndex("ChineseName"));
            item.setTitle(chineseName);
            if (isXiTi) {
                int max = cursor.getInt(cursor.getColumnIndex("number"));
                int current = cursor.getInt(cursor.getColumnIndex("read"));
                item.setMax(max);
                item.setCurrent(current);
            }
            list.add(item);
        }
        cursor.close();
        return list;
    }


    //获取数据库表英文名
    public static List<String> getTableEnglishName(SQLiteDatabase database) {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query("tableNames", null, null, null, null, null, null, null);
        //利用游标遍历所有数据对象
        while (cursor.moveToNext()) {
            String englishName = cursor.getString(cursor.getColumnIndex("EnglishName"));
            list.add(englishName);
        }
        cursor.close();
        return list;
    }

    //获取题库数据库某一个表的数据
    public static List<TestItem> getTableData(SQLiteDatabase database, String tableName) {
        List<TestItem> list = new ArrayList<>();
        Cursor cursor = database.query(tableName, null, null, null, null, null, null, null);
        //利用游标遍历所有数据对象
        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex("题干"));
            String answerA = cursor.getString(cursor.getColumnIndex("A"));
            String answerB = cursor.getString(cursor.getColumnIndex("B"));
            String answerC = cursor.getString(cursor.getColumnIndex("C"));
            String answerD = cursor.getString(cursor.getColumnIndex("D"));
            String answerE = cursor.getString(cursor.getColumnIndex("E"));
            String answer = cursor.getString(cursor.getColumnIndex("答案"));
            String jieXi = cursor.getString(cursor.getColumnIndex("解析"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            TestItem testItem = new TestItem(content, answerA, answerB, answerC, answerD, answerE, answer, jieXi, id);
            list.add(testItem);
        }
        cursor.close();
        return list;
    }

    //获取考试类型数据库某一个表的数据
    public static List<ExamSmallItem> getExamTableData(SQLiteDatabase database, String tableName) {
        List<ExamSmallItem> list = new ArrayList<>();
        Cursor cursor = database.query(tableName, null, null, null, null, null, null, null);
        //利用游标遍历所有数据对象
        while (cursor.moveToNext()) {
            String formalDBURL = cursor.getString(cursor.getColumnIndex("formalDBURL"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            ExamSmallItem examSmallItem = new ExamSmallItem();
            examSmallItem.setFormalDBURL(formalDBURL);
            examSmallItem.setTitle(title);
            list.add(examSmallItem);
        }
        cursor.close();
        return list;
    }

    //创建错题数据库表格,database1是正式数据库，database2是错题数据库
    public static void creatTable(SQLiteDatabase database1, SQLiteDatabase database2) {
        database2.execSQL("create table if not exists tableNames(EnglishName varchar,ChineseName varchar)");
        Cursor cursor = database1.query("tableNames", null, null, null, null, null, null, null);
        while ((cursor.moveToNext())) {
            String chineseName = cursor.getString(cursor.getColumnIndex("ChineseName"));
            String englishName = cursor.getString(cursor.getColumnIndex("EnglishName"));
            ContentValues values = new ContentValues();
            values.put("ChineseName", chineseName);
            values.put("EnglishName", englishName);
            database2.insert("tableNames", "id", values);
        }
        for (int i = 0; i < getTableEnglishName(database1).size(); i++) {
            database2.execSQL("create table if not exists " +
                    getTableEnglishName(database1).get(i) +
                    "("
                    + "id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "题干 varchar,"
                    + "A varchar,"
                    + "B varchar,"
                    + "C varchar,"
                    + "D varchar,"
                    + "E varchar,"
                    + "答案 varchar,"
                    + "extension varchar,"
                    + "解析 varchar,"
                    + "testId integer)");
            Log.i("tableeee", getTableEnglishName(database1).get(i));
        }
        cursor.close();
//        database1.close();
        database2.close();
    }

    //创建习题数据库表格,database1是正式数据库，database2是习题数据库
    public static void creatXiTiTable(SQLiteDatabase database1, SQLiteDatabase database2) {
        database2.execSQL("create table if not exists tableNames(EnglishName varchar,ChineseName varchar,number integer default 0,read integer default 0)");
        Cursor cursor = database1.query("tableNames", null, null, null, null, null, null, null);
        Cursor cursor1=null;
        while ((cursor.moveToNext())) {
            String chineseName = cursor.getString(cursor.getColumnIndex("ChineseName"));
            String englishName = cursor.getString(cursor.getColumnIndex("EnglishName"));
            cursor1=database1.query(englishName,null,null, null, null, null, null, null);
            ContentValues values = new ContentValues();
            values.put("ChineseName", chineseName);
            values.put("EnglishName", englishName);
            values.put("number", cursor1.getCount());
            database2.insert("tableNames", "id", values);

            database2.execSQL("create table if not exists " +
                    englishName +
                    "("
                    + "id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "题干 varchar,"
                    + "A varchar,"
                    + "B varchar,"
                    + "C varchar,"
                    + "D varchar,"
                    + "E varchar,"
                    + "答案 varchar,"
                    + "extension varchar,"
                    + "解析 varchar,"
                    + "testId integer)");
            while (cursor1.moveToNext()){
                ContentValues values2 = new ContentValues();
                String A=cursor1.getString(cursor1.getColumnIndex("A"));
                String B=cursor1.getString(cursor1.getColumnIndex("B"));
                String C=cursor1.getString(cursor1.getColumnIndex("C"));
                String D=cursor1.getString(cursor1.getColumnIndex("D"));
                String E=cursor1.getString(cursor1.getColumnIndex("E"));
                String subject=cursor1.getString(cursor1.getColumnIndex("题干"));
                String answer=cursor1.getString(cursor1.getColumnIndex("答案"));
                String analysis=cursor1.getString(cursor1.getColumnIndex("解析"));
                values2.put("题干", subject);
                values2.put("A", A);
                values2.put("B", B);
                values2.put("C",C);
                values2.put("D", D);
                values2.put("E", E);
                values2.put("答案", answer);
                values2.put("解析", analysis);
                database2.insert(englishName, "id", values2);
            }
        }
        cursor.close();
        cursor1.close();
        database1.close();
        database2.close();
    }
}
