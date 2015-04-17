package asx.idea.plugin.utils;

import asx.idea.plugin.project.AsxFileType;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.util.Condition;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * Created by Sergey on 4/4/15.
 */
public class AsxFileTemplateUtil {
    private final static String HAXE_TEMPLATE_PREFIX = "Asx";
    public static List<FileTemplate> getApplicableTemplates() {
        return getApplicableTemplates(new Condition<FileTemplate>() {
            @Override
            public boolean value(FileTemplate fileTemplate) {
                return AsxFileType.EXTENSION.equals(fileTemplate.getExtension());
            }
        });
    }
    public static List<FileTemplate> getApplicableTemplates(Condition<FileTemplate> filter) {
        List<FileTemplate> applicableTemplates = new SmartList<FileTemplate>();
        applicableTemplates.addAll(ContainerUtil.findAll(FileTemplateManager.getDefaultInstance().getInternalTemplates(), filter));
        applicableTemplates.addAll(ContainerUtil.findAll(FileTemplateManager.getDefaultInstance().getAllTemplates(), filter));
        return applicableTemplates;
    }
    public static String getTemplateShortName(String templateName) {
        if (templateName.startsWith(HAXE_TEMPLATE_PREFIX)) {
            return templateName.substring(HAXE_TEMPLATE_PREFIX.length());
        }
        return templateName;
    }
    @NotNull
    public static Icon getTemplateIcon(String name) {
        name = getTemplateShortName(name);
        if ("Class".equals(name)) {
            return AsxIcons.CLASS;
        }
        else if ("Interface".equals(name)) {
            return AsxIcons.INTERFACE;
        }
        else if ("Enum".equals(name)) {
            return AsxIcons.ENUM;
        }
        return AsxIcons.FILE;
    }
}
