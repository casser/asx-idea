package asx.idea.plugin.project;

import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Sergey on 4/4/15.
 */
public class AsxFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(AsxFileType.INSTANCE,new ExtensionFileNameMatcher(AsxFileType.EXTENSION));
    }
}
