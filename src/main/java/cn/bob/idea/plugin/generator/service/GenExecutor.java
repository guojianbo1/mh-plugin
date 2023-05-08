package cn.bob.idea.plugin.generator.service;

import cn.bob.idea.plugin.common.dto.ParamsMap;
import cn.bob.idea.plugin.common.util.Constant;
import cn.bob.idea.plugin.common.util.DateUtils;
import cn.bob.idea.plugin.entity.ColumnEntity;
import cn.bob.idea.plugin.entity.TableEntity;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.NullLogChute;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * @description: 代码生成器   工具类
 * @author: bianqipeng
 * @date: 2020年07月17日 09:37:37
 */
public class GenExecutor {

    /**
     * @description: 根据mybatis的类型，使用指定模板
     * @author: bianqipeng
     * @date: 2020年07月17日 10:12:57
     * @param: mybatisType
     * @return: List<String>
     */
    public static List<String> getTemplates(String mybatisType) {
        List<String> templates = new ArrayList<String>();
        templates.add("template/" + mybatisType + "/Model.java.vm");
        templates.add("template/" + mybatisType + "/Mapper.java.vm");
        templates.add("template/" + mybatisType + "/Mapper.xml.vm");
        templates.add("template/" + mybatisType + "/Service.java.vm");
        templates.add("template/" + mybatisType + "/ServiceImpl.java.vm");
        templates.add("template/" + mybatisType + "/Controller.java.vm");
        return templates;
    }

    /**
     * 生成代码
     */
    public void generatorCode(String mybatisType, Map<String, String> table,
                                     List<Map<String, String>> columns) {
        //配置信息
        Configuration config = getConfig();
        boolean hasBigDecimal = false;
        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.get("tableName"));
        tableEntity.setComments(table.get("tableComment"));
        //表名转换成Java类名
        String className = tableToJava(tableEntity.getTableName(), config.getString("tablePrefix"));
        tableEntity.setClassName(className);
        tableEntity.setClassname(StringUtils.uncapitalize(className));

        //列信息
        List<ColumnEntity> columsList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.get("columnName"));
            columnEntity.setUpColumnName(column.get("columnName").toUpperCase());
            columnEntity.setDataType(column.get("dataType"));
            columnEntity.setComments(column.get("columnComment"));
            columnEntity.setExtra(column.get("extra"));

            //列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setAttrName(attrName);
            columnEntity.setAttrname(StringUtils.uncapitalize(attrName));

            //列的数据类型，转换成Java类型
            String attrType = config.getString(columnEntity.getDataType(), "unknowType");
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && attrType.equals("BigDecimal")) {
                hasBigDecimal = true;
            }
            //是否主键
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }

            columsList.add(columnEntity);
        }
        tableEntity.setColumns(columsList);

        //没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

//        //设置velocity资源加载器
//        Properties prop = new Properties();
//        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//        Velocity.init(prop);

        VelocityEngine ve = new VelocityEngine();
        Thread currentThread = Thread.currentThread();
        ClassLoader temp = Thread.currentThread().getContextClassLoader();
        try {
            currentThread.setContextClassLoader( GenExecutor.class.getClassLoader() );
            ve.setProperty( "runtime.log.logsystem.class", NullLogChute.class.getName() );
            ve.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            ve.init();
        } finally {
            currentThread.setContextClassLoader( temp );
        }
        String mainPath = config.getString("mainPath");
        mainPath = StringUtils.isBlank(mainPath) ? "io.guli" : mainPath;
        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getClassName());
        map.put("classname", tableEntity.getClassname());
        map.put("pathName", tableEntity.getClassname().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put(Constant.CONTROLLER_PACKAGE, ParamsMap.getInstance().get(Constant.CONTROLLER_PACKAGE));
        map.put(Constant.MODEL_PACKAGE, ParamsMap.getInstance().get(Constant.MODEL_PACKAGE));
        map.put(Constant.SERVICE_PACKAGE, ParamsMap.getInstance().get(Constant.SERVICE_PACKAGE));
        map.put(Constant.MAPPER_PACKAGE, ParamsMap.getInstance().get(Constant.MAPPER_PACKAGE));
        map.put(Constant.MAPPER_PACKAGE, ParamsMap.getInstance().get(Constant.MAPPER_PACKAGE));
        map.put(Constant.AUTHOR, ParamsMap.getInstance().get(Constant.AUTHOR));

        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates(mybatisType);
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = ve.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
                String fileName = getFileName(template, tableEntity.getClassName());
                String directoryName = getDirectoryName(template);
                // 创建目录对象
                File directory = new File(directoryName);
                // 如果目录不存在，创建一个新目录
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                // 创建文件对象
                File file = new File(directoryName,fileName);
                // 如果文件不存在，创建一个新文件
                if (!file.exists()) {
                    file.createNewFile();
                }else{
                    continue;
                }
                OutputStream outputStream = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
                writer.write(sw.toString());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }
    }


    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            PropertiesConfiguration config = null;
            try {
                config = new PropertiesConfiguration("config.properties");
            }catch (Exception ignored){

            }
            if (config!=null && !config.isEmpty()) {
                return config;
            }
            InputStream inputStream = GenExecutor.class.getResourceAsStream("/generator.properties");
            config = new PropertiesConfiguration();
            config.load(inputStream);
            return config;
        } catch (ConfigurationException e) {
            System.out.println("获取配置文件失败");
            throw new RuntimeException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getDirectoryName(String template) {
        if (template.contains(Constant.MODEL_JAVA_VM)) {
            return ParamsMap.getInstance().get(Constant.MODEL_DIR)+"\\"+ParamsMap.getInstance().get(Constant.MODEL_PACKAGE).replace(".","\\");
        }

        if (template.contains(Constant.MAPPER_JAVA_VM)) {
            return ParamsMap.getInstance().get(Constant.MAPPER_DIR)+"\\"+ParamsMap.getInstance().get(Constant.MAPPER_PACKAGE).replace(".","\\");
        }

        if (template.contains(Constant.SERVICE_JAVA_VM)) {
            return ParamsMap.getInstance().get(Constant.SERVICE_DIR)+"\\"+ParamsMap.getInstance().get(Constant.SERVICE_PACKAGE).replace(".","\\");
        }

        if (template.contains(Constant.SERVICEIMPL_JAVA_VM)) {
            return ParamsMap.getInstance().get(Constant.SERVICE_DIR)+"\\"+ParamsMap.getInstance().get(Constant.SERVICE_PACKAGE).replace(".","\\")+"\\impl";
        }

        if (template.contains(Constant.CONTROLLER_JAVA_VM)) {
            return ParamsMap.getInstance().get(Constant.CONTROLLER_DIR)+"\\"+ParamsMap.getInstance().get(Constant.CONTROLLER_PACKAGE).replace(".","\\");
        }

        if (template.contains(Constant.MAPPER_XML_VM)) {
            return ParamsMap.getInstance().get(Constant.MAPPER_XML_DIR);
        }

        return null;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className) {
        if (template.contains(Constant.MODEL_JAVA_VM)) {
            return File.separator + className + ".java" ;
        }

        if (template.contains(Constant.MAPPER_JAVA_VM)) {
            return File.separator + className + "Mapper.java" ;
        }

        if (template.contains(Constant.SERVICE_JAVA_VM)) {
            return File.separator + className + "Service.java" ;
        }

        if (template.contains(Constant.SERVICEIMPL_JAVA_VM)) {
            return File.separator + className + "ServiceImpl.java" ;
        }

        if (template.contains(Constant.CONTROLLER_JAVA_VM)) {
            return File.separator + className + "Controller.java" ;
        }

        if (template.contains(Constant.MAPPER_XML_VM)) {
            return File.separator + className + "Mapper.xml" ;
        }

        return null;
    }


}
