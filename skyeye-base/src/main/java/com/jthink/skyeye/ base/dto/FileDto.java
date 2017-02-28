package com.jthink.skyeye.base.dto;

import java.io.Serializable;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-09-23 16:59:12
 */
public class FileDto implements Serializable {

    private static final long serialVersionUID = 8714191576381945205L;

    private String fileName;
    private byte[] fileBytes;

    public FileDto() {
    }

    public FileDto(String fileName, byte[] fileBytes) {
        this.fileName = fileName;
        this.fileBytes = fileBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }
}