package cn.bob.idea.plugin;

import cn.bob.idea.plugin.ui.SettingUI;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author guojianbo
 * @date 2023/4/28 17:56
 */
public class MyAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 获取项目对象
        Project project = e.getProject();
        System.out.println(project);
        // 创建一个自定义的UI窗口
        SettingUI dialog = new SettingUI(project,false);
        dialog.show();
    }
}
