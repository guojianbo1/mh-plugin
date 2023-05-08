package cn.bob.idea.plugin.generator.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guojianbo
 * @date 2023/4/28 18:21
 */
public class GeneratorDao {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    public static void main(String[] args) {
        GeneratorDao generatorDao = new GeneratorDao();
        System.out.println(generatorDao.queryTable("scs_driver"));
        System.out.println(generatorDao.queryColumns("scs_driver"));
        generatorDao.close();
    }

    public GeneratorDao() {
        try {
            // 加载MySQL驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            // 创建连接
            conn = DriverManager.getConnection("jdbc:mysql://192.168.240.46:3306/mh_scs?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&allowMultiQueries=true", "dev_mall_admin", "MaMonDev#20");
            // 创建Statement对象
            stmt = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GeneratorDao(String url,String username,String pwd) throws Exception {
        try {
            // 加载MySQL驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            // 创建连接
            conn = DriverManager.getConnection(url,username,pwd);
            // 创建Statement对象
            stmt = conn.createStatement();
        } catch (Exception e) {
            throw new Exception("连接失败");
        }
    }

    public void close() {
        // 关闭连接
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> queryTable(String tableName){
        Map<String, String> map = new HashMap<>();
        try {
            // 执行SQL查询语句
            rs = stmt.executeQuery("select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables where table_schema = (select database()) and table_name = '" + tableName + "'");
            // 处理查询结果
            while (rs.next()) {
                map.put("tableName",rs.getString("tableName"));
                map.put("engine",rs.getString("engine"));
                map.put("tableComment",rs.getString("tableComment"));
                map.put("createTime",rs.getString("createTime"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    public List<Map<String, String>> queryColumns(String tableName){
        List<Map<String, String>> list = new ArrayList<>();
        try {
            // 执行SQL查询语句
            rs = stmt.executeQuery("select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra from information_schema.columns where table_name = '" + tableName +"' and table_schema = (select database()) order by ordinal_position");
            // 处理查询结果
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("columnName",rs.getString("columnName"));
                map.put("dataType",rs.getString("dataType"));
                map.put("columnComment",rs.getString("columnComment"));
                map.put("columnKey",rs.getString("columnKey"));
                map.put("extra",rs.getString("extra"));
                list.add(map);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
