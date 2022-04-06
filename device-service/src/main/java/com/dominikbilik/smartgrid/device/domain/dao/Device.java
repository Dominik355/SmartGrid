package com.dominikbilik.smartgrid.device.domain.dao;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device")
public class Device {

    @Id
    private Long id;
    private String name;
    @Column(unique=true)
    private String idInFilename; // id used in filename
    private LocalDateTime created;
    private String owner;

    public Device() {}

    public Device(Long id, String name, String idInFilename, LocalDateTime created, String owner) {
        this.id = id;
        this.name = name;
        this.idInFilename = idInFilename;
        this.created = created;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
