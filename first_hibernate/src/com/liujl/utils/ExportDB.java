package com.liujl.utils;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
/**
* Hibernate工具<br/>
* 将对象模型生成关系模型(将对象生成数据库中的表)
* 把hbm映射文件转换成DDL
* 生成数据表之前要求已经存在数据库
* 注：这个工具类建立好后，以后就不用建立了。以后直接Copy来用。
* @author wjt276
* @version 1.0 2009/10/16
*/
public class ExportDB {
public static void main(String[] args){
/*
* org.hibernate.cfg.Configuration类的作用：
* 读取hibernate配置文件(hibernate.cfg.xml或hiberante.properties)的.
wjt276 Hibernate学习笔记第12页 共 132页
* new Configuration()默认是读取hibernate.properties
* 所以使用new Configuration().configure();来读取hibernate.cfg.xml配置文件
*/
Configuration cfg = new Configuration().configure();
/*
* org.hibernate.tool.hbm2ddl.SchemaExport工具类：
* 需要传入Configuration参数
* 此工具类可以将类导出生成数据库表
*/
SchemaExport export = new SchemaExport(cfg);
/*
* 开始导出
* 第一个参数：script 是否打印DDL信息
* 第二个参数：export 是否导出到数据库中生成表
*/
export.create(true, true);
}
}