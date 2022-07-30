package cn.itsuki.blog.entities;

import java.util.Arrays;

/**
 * @author: itsuki
 * @create: 2022-07-30 17:05
 **/
public class FileUpload {
    private String contentType;
    private byte[] content;
    private String name;

    public FileUpload(String name, String contentType, byte[] content) {
        this.name = name;
        this.contentType = contentType;
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "FileUpload{" +
                "contentType='" + contentType + '\'' +
                ", content=" + Arrays.toString(content) +
                ", name='" + name + '\'' +
                '}';
    }
}
