package com.smart.common.utils;

import com.smart.common.wrapper.SmartProperties;
import com.smart.web.base.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Auther: lzy
 * Description:
 * Date Created by： 10:04 on 2018/4/23
 * Modified By：
 */

public class JDBCUtils {
  private static  Logger log= LoggerFactory.getLogger(JDBCUtils.class);
    /**
     * 功能实现:获取Connection连接
     * <p>
     * Author: Lzy
     * Date: 2018/4/17 15:11
     * Param: []
     * Return: void
     */
    public static Connection getConn() {

         String driver = "com.mysql.jdbc.Driver";
         SmartProperties conf = Loader.pro;
         String url = conf.get("jdbc.url");
         String username = conf.get("jdbc.username");
         String password = conf.get("jdbc.password");
        Connection conn = null;
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("驱动注册失败：{}", e.getMessage());
        }
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("获取mysql连接失败：{}", e.getMessage());
            System.exit(1);
        }
        return conn;
    }

}
