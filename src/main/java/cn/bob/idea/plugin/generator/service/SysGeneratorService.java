package cn.bob.idea.plugin.generator.service;

import cn.bob.idea.plugin.common.util.Constant;
import cn.bob.idea.plugin.generator.dao.GeneratorDao;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.List;
import java.util.Map;


public class SysGeneratorService {
    private String mybatisType = "tk-mybatis";
    private GenExecutor genExecutor;
    private GeneratorDao generatorDao;

    public SysGeneratorService(GenExecutor genExecutor, GeneratorDao generatorDao) {
        this.genExecutor = genExecutor;
        this.generatorDao = generatorDao;
    }

    public static void main(String[] args) throws Exception {
//        GeneratorDao generatorDao = new GeneratorDao();
////        SysGeneratorService sysGeneratorService = new SysGeneratorService(new GenUtils(),generatorDao);
////        sysGeneratorService.generatorCode(new String[]{"scs_driver"},generatorDao);
//        generatorDao.close();
//        File configFile = new File("config.properties");
//        InputStream inputStream = GenExecutor.class.getResourceAsStream("/generator.properties");
//        Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        PropertiesConfiguration config = (PropertiesConfiguration)GenExecutor.getConfig();
        config.setProperty(Constant.CONTROLLER_DIR,"234");
        config.setFileName("config.properties");
        config.save();
    }

    public void generatorCode(String[] tableNames) {
        for (String tableName : tableNames) {
            Map<String, String> table = generatorDao.queryTable(tableName);
            if (table.isEmpty()){
                throw new RuntimeException(tableName+"该表不存在");
            }
            List<Map<String, String>> columns = generatorDao.queryColumns(tableName);
            genExecutor.generatorCode(mybatisType, table, columns);
        }
    }
}
