package cn.bob.idea.plugin.factory;

import cn.bob.idea.plugin.ui.SettingUI;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;

import javax.swing.*;

/**
 * @author guojianbo
 * @date 2023/5/5 15:28
 */
public class ViewBars extends SimpleToolWindowPanel {
    private Project project;
    private SettingUI settingUI;

    public ViewBars(Project project) {
        super(false,true);
        this.project = project;
        settingUI = new SettingUI(project, false);

        // 设置窗体侧边栏按钮
        DefaultActionGroup group = new DefaultActionGroup();

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", group, false);
        toolbar.setTargetComponent(this);
        setToolbar(toolbar.getComponent());
        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(settingUI.getComponent());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setBlockIncrement(50);

        // 添加滚动面板
        setContent(scrollPane);

//        // 添加
//        JBSplitter splitter = new JBSplitter(false);
//        splitter.setSplitterProportionKey("main.splitter.key");
//        splitter.setFirstComponent(settingUI.getComponent());
//        splitter.setProportion(0.3f);
//        setContent(splitter);
    }
    public Project getProject() {
        return project;
    }

    public SettingUI getConsoleUI() {
        return settingUI;
    }

}
