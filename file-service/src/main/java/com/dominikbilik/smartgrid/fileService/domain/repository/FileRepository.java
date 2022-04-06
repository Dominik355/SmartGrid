package com.dominikbilik.smartgrid.fileService.domain.repository;

import com.dominikbilik.smartgrid.fileService.domain.dao.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

    File findByName(String name);

}