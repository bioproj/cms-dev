package com.wangyang.handle;

import com.wangyang.common.exception.FileOperationException;
import com.wangyang.pojo.entity.Attachment;
import com.wangyang.pojo.enums.AttachmentStoreType;
import com.wangyang.pojo.support.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.LinkedList;

@Slf4j
@Component
public class FileHandlers {

    /**
     * File handler container.
     */
    private final Collection<FileHandler> fileHandlers = new LinkedList<>();

    public FileHandlers(ApplicationContext applicationContext) {
        // Add all file handler
        addFileHandlers(applicationContext.getBeansOfType(FileHandler.class).values());
    }

    public UploadResult upload(@NonNull MultipartFile file, @NonNull AttachmentStoreType attachmentType) {
        Assert.notNull(file, "Multipart file must not be null");
        Assert.notNull(attachmentType, "Attachment type must not be null");

        for (FileHandler fileHandler : fileHandlers) {
            if (fileHandler.supportType(attachmentType)) {
                return fileHandler.upload(file);
            }
        }

        throw new FileOperationException("No available file handler to upload the file").setErrorData(attachmentType);
    }

    public UploadResult upload(@NonNull MultipartFile file, @NonNull AttachmentStoreType attachmentType, String path) {
        Assert.notNull(file, "Multipart file must not be null");
        Assert.notNull(attachmentType, "Attachment type must not be null");

        for (FileHandler fileHandler : fileHandlers) {
            if (fileHandler.supportType(attachmentType)) {
                return fileHandler.upload(file,path);
            }
        }

        throw new FileOperationException("No available file handler to upload the file").setErrorData(attachmentType);
    }

    public UploadResult uploadStrContent(@NonNull String content, String strName,@NonNull AttachmentStoreType attachmentType) {

        for (FileHandler fileHandler : fileHandlers) {
            if (fileHandler.supportType(attachmentType)) {
                return fileHandler.uploadStrContent(content,strName);
            }
        }

        throw new FileOperationException("No available file handler to upload the file").setErrorData(attachmentType);
    }

    public UploadResult upload(@NonNull String url,@NonNull String name,@NonNull AttachmentStoreType attachmentType){
        for (FileHandler fileHandler : fileHandlers) {
            if (fileHandler.supportType(attachmentType)) {
                return fileHandler.upload(url,name);
            }
        }
        throw new FileOperationException("通过url上传文件失败!!").setErrorData(attachmentType);
    }

    public void delete(@NonNull Attachment attachment) {
        Assert.notNull(attachment, "Attachment must not be null");
        delete(attachment.getType(), attachment.getFileKey());
    }


    public void delete(@Nullable AttachmentStoreType type, @NonNull String key) {
        for (FileHandler fileHandler : fileHandlers) {
            if (fileHandler.supportType(type)) {
                // Delete the file
                fileHandler.delete(key);
                return;
            }
        }
        log.error("No available file handlers to delete the file");
//        throw new FileOperationException).setErrorData(type);
    }

    /**
     * Adds file handlers.
     *
     * @param fileHandlers file handler collection
     * @return current file handlers
     */
    @NonNull
    public FileHandlers addFileHandlers(@Nullable Collection<FileHandler> fileHandlers) {
        if (!CollectionUtils.isEmpty(fileHandlers)) {
            this.fileHandlers.addAll(fileHandlers);
        }
        return this;
    }
}
