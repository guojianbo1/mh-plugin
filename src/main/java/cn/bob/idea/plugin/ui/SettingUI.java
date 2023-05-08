package cn.bob.idea.plugin.ui;

import cn.bob.idea.plugin.common.dto.ParamsMap;
import cn.bob.idea.plugin.common.util.Constant;
import cn.bob.idea.plugin.generator.service.GenExecutor;
import cn.bob.idea.plugin.generator.dao.GeneratorDao;
import cn.bob.idea.plugin.generator.service.SysGeneratorService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

/**
 * @author guojianbo
 * @date 2023/5/4 13:40
 */
public class SettingUI extends DialogWrapper {
    private JPanel mainPanel;
    private JTextField urlText;
    private JTextField userNameText;
    private JButton conButton;
    private JButton createButton;
    private JPasswordField pwdText;
    private JTextField tableTextField;
    private JTextField controllerDirField;
    private JTextField controllerPackageField;
    private JTextField modelDirField;
    private JTextField modelPackageField;
    private JTextField serviceDirField;
    private JTextField servicePackageField;
    private JTextField mapperDirField;
    private JTextField mapperPackageField;
    private JTextField mapperXmlDirField;
    private JTextField authorText;

    public SettingUI(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        try {
            this.setParams(project);
        }catch (Exception e){
            Messages.showMessageDialog("创建失败:"+e.getMessage(),"Mysql连接",Messages.getInformationIcon());
        }
        // 给按钮添加一个事件
        conButton.addActionListener(e -> {
            try {
                this.initParams();
                this.saveParams();
                GeneratorDao generatorDao = new GeneratorDao(urlText.getText(), userNameText.getText(), pwdText.getText());
                Messages.showMessageDialog("连接成功","Mysql连接",Messages.getInformationIcon());
                generatorDao.close();
            }catch (Exception er){
                Messages.showMessageDialog("连接失败","Mysql连接",Messages.getInformationIcon());
            }
        });
        // 给按钮添加一个事件
        createButton.addActionListener(e -> {
            try {
                this.initParams();
                this.saveParams();
                this.doGeneratorCode(project);
                Messages.showMessageDialog("生成代码完成","生成代码",Messages.getInformationIcon());
            }catch (Exception er){
                er.printStackTrace();
                Messages.showMessageDialog("生成代码失败:"+er.getMessage(),"生成代码",Messages.getInformationIcon());
            }
        });
        // 将mainPanel添加到Dialog中
        init();
    }

    private void setParams(Project project) {
        String basePath = project.getBasePath();
        basePath = basePath.replace("/","\\");
        Configuration config = GenExecutor.getConfig();
        if (StringUtils.isEmpty(config.getString(Constant.CONTROLLER_DIR))) {
            controllerDirField.setText(basePath);
        }else{
            controllerDirField.setText(config.getString(Constant.CONTROLLER_DIR));
            controllerPackageField.setText(config.getString(Constant.CONTROLLER_PACKAGE));
        }
        if (StringUtils.isEmpty(config.getString(Constant.MODEL_DIR))) {
            modelDirField.setText(basePath);
        }else{
            modelDirField.setText(config.getString(Constant.MODEL_DIR));
            modelPackageField.setText(config.getString(Constant.MODEL_PACKAGE));
        }
        if (StringUtils.isEmpty(config.getString(Constant.SERVICE_DIR))) {
            serviceDirField.setText(basePath);
        }else{
            serviceDirField.setText(config.getString(Constant.SERVICE_DIR));
            servicePackageField.setText(config.getString(Constant.SERVICE_PACKAGE));
        }
        if (StringUtils.isEmpty(config.getString(Constant.MAPPER_DIR))) {
            mapperDirField.setText(basePath);
        }else{
            mapperDirField.setText(config.getString(Constant.MAPPER_DIR));
            mapperPackageField.setText(config.getString(Constant.MAPPER_PACKAGE));
        }
        if (StringUtils.isEmpty(config.getString(Constant.MAPPER_XML_DIR))) {
            mapperXmlDirField.setText(basePath + "\\resource\\mapper");
        }else{
            mapperXmlDirField.setText(config.getString(Constant.MAPPER_XML_DIR));
        }
        if (StringUtils.isNotEmpty(config.getString(Constant.MYSQL_URL))) {
            urlText.setText(config.getString(Constant.MYSQL_URL));
        }
        if (StringUtils.isNotEmpty(config.getString(Constant.MYSQL_USERNAME))) {
            userNameText.setText(config.getString(Constant.MYSQL_USERNAME));
        }
        if (StringUtils.isNotEmpty(config.getString(Constant.MYSQL_PWD))) {
            pwdText.setText(config.getString(Constant.MYSQL_PWD));
        }
        if (StringUtils.isNotEmpty(config.getString(Constant.MYSQL_TABLES))) {
            tableTextField.setText(config.getString(Constant.MYSQL_TABLES));
        }
        if (StringUtils.isNotEmpty(config.getString(Constant.AUTHOR))) {
            authorText.setText(config.getString(Constant.AUTHOR));
        }
    }

    private void initParams() throws ConfigurationException {
        ParamsMap.getInstance().put(Constant.CONTROLLER_DIR,controllerDirField.getText());
        ParamsMap.getInstance().put(Constant.CONTROLLER_PACKAGE,controllerPackageField.getText());
        ParamsMap.getInstance().put(Constant.MODEL_DIR,modelDirField.getText());
        ParamsMap.getInstance().put(Constant.MODEL_PACKAGE,modelPackageField.getText());
        ParamsMap.getInstance().put(Constant.SERVICE_DIR,serviceDirField.getText());
        ParamsMap.getInstance().put(Constant.SERVICE_PACKAGE,servicePackageField.getText());
        ParamsMap.getInstance().put(Constant.MAPPER_DIR,mapperDirField.getText());
        ParamsMap.getInstance().put(Constant.MAPPER_PACKAGE,mapperPackageField.getText());
        ParamsMap.getInstance().put(Constant.MAPPER_XML_DIR,mapperXmlDirField.getText());
        ParamsMap.getInstance().put(Constant.AUTHOR,authorText.getText());
    }

    @Override
    protected void doOKAction() {
        this.saveParams();
        super.doOKAction();
    }

    private void saveParams() {
        PropertiesConfiguration config = (PropertiesConfiguration)GenExecutor.getConfig();
        config.setProperty(Constant.CONTROLLER_DIR,controllerDirField.getText());
        config.setProperty(Constant.CONTROLLER_PACKAGE,controllerPackageField.getText());
        config.setProperty(Constant.MODEL_DIR,modelDirField.getText());
        config.setProperty(Constant.MODEL_PACKAGE,modelPackageField.getText());
        config.setProperty(Constant.SERVICE_DIR,serviceDirField.getText());
        config.setProperty(Constant.SERVICE_PACKAGE,servicePackageField.getText());
        config.setProperty(Constant.MAPPER_DIR,mapperDirField.getText());
        config.setProperty(Constant.MAPPER_PACKAGE,mapperPackageField.getText());
        config.setProperty(Constant.MAPPER_XML_DIR,mapperXmlDirField.getText());
        config.setProperty(Constant.MYSQL_URL,urlText.getText());
        config.setProperty(Constant.MYSQL_USERNAME,userNameText.getText());
        config.setProperty(Constant.MYSQL_PWD,pwdText.getText());
        config.setProperty(Constant.MYSQL_TABLES,tableTextField.getText());
        config.setProperty(Constant.AUTHOR,authorText.getText());
        try {
            File configFile = new File("config.properties");
            config.setFile(configFile);
            config.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        System.out.println("createCenterPanel:"+mainPanel);
        return mainPanel;
    }

    public JTextField getUrlTextField() {
        return urlText;
    }

    public JTextField getUserNameText() {
        return userNameText;
    }

    public JTextField getPwdText() {
        return pwdText;
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    /**
     * 执行生成代码
     * @param project
     */
    private void doGeneratorCode(Project project) throws Exception {
        GeneratorDao generatorDao = new GeneratorDao(urlText.getText(), userNameText.getText(), pwdText.getText());
        GenExecutor genExecutor = new GenExecutor();
        SysGeneratorService sysGeneratorService = new SysGeneratorService(genExecutor,generatorDao);
        String tables = tableTextField.getText();
        tables = tables.replace("，",",");
        String[] tableArr = tables.split(",");
        sysGeneratorService.generatorCode(tableArr);
        generatorDao.close();

    }
}
