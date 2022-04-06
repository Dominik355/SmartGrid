package com.dominikbilik.smartgrid.fileService.domain.dao;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long deviceId;
    private LocalDateTime creationTime;
    private String fileType;

    @Column(unique = true)
    private String name;
    private String contentType;
    private Long size;

    @Lob
    private byte[] data;

    public File() {}

    public File(LocalDateTime creationTime, String fileType, String name, String contentType, Long size, byte[] data) {
        this.creationTime = creationTime;
        this.fileType = fileType;
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", creationTime=" + creationTime +
                ", fileType='" + fileType + '\'' +
                ", name='" + name + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                '}';
    }
}
