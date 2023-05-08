package cn.bob.idea.plugin.factory;

import cn.bob.idea.plugin.ui.SettingUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * ���ù���
 *
 * @author guojianbo
 * @date 2023/5/4 13:55
 */
public class SettingFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        try {
            // ����
            ViewBars viewPanel = new ViewBars(project);
            // ��ȡ���ݹ�����ʵ��
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            // ��ȡ ToolWindow ��ʾ������
            Content content = contentFactory.createContent(viewPanel, "��������", false);
            // ���� ToolWindow ��ʾ������
            toolWindow.getContentManager().addContent(content, 0);
        }catch (Exception e){
            File file = new File("E:\\mystudy\\mh-plugin\\log.txt");
            try {
                OutputStream outputStream = new FileOutputStream(file);
                outputStream.write(e.getMessage().getBytes());
                outputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
