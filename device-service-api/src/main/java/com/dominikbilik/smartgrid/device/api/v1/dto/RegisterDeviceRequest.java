package com.dominikbilik.smartgrid.device.api.v1.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class RegisterDeviceRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String idInFilename;
    private String owner;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdInFilename() {
        return idInFilename;
    }

    public void setIdInFilename(String idInFilename) {
        this.idInFilename = idInFilename;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "RegisterDeviceRequest{" +
                "name='" + name + '\'' +
                ", idInFilename='" + idInFilename + '\'' +
                ", owner='" + owner + '\'' +
                '}';
    }
}
