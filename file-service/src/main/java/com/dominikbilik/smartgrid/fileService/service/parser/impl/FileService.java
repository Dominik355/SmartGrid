package com.dominikbilik.smartgrid.fileService.service.parser.impl;

import com.dominikbilik.smartgrid.fileService.domain.dao.File;
import com.dominikbilik.smartgrid.fileService.domain.repository.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private FileRepository fileRepository;

    @Transactional
    public String saveFile(MultipartFile multipartFile) throws IOException {
        LOG.info("called saveFile for : {}", multipartFile);
        String name = multipartFile.getOriginalFilename() != null ?  multipartFile.getOriginalFilename() : multipartFile.getName();
        File file = fileRepository.findByName(name);

        if (file != null) {
            LOG.info("File with name {} already exists, returning ID of existing file {}", name, file.getId());
            return file.getName();
        } else {
            file = fileRepository.save(new File(LocalDateTime.now(),
                    resolveFileType(multipartFile),
                    multipartFile.getOriginalFilename() != null ?  multipartFile.getOriginalFilename() : multipartFile.getName(),
                    multipartFile.getContentType(),
                    multipartFile.getSize(),
                    multipartFile.getBytes())
            );
        }

        if (file == null) {
            throw new RuntimeException("File was not saved");
        }
        return file.getName();
    }

    /**
     * @return boolean telling if file was deleted
     */
    public boolean deleteFile(Long fileId) {
        LOG.info("Deleting file [fileId={}]", fileId);
        fileRepository.deleteById(fileId);
        return !fileRepository.existsById(fileId);
    }

    // just hardcoded to make work for demonstration
    private String resolveFileType (MultipartFile file) {
        String currentFilename = file.getOriginalFilename() != null ?  file.getOriginalFilename() : file.getName();
        String fileType;
        if (currentFilename.contains("_meteo_")) {
            fileType = "METEO";
        } else if (currentFilename.contains("_P01_") || currentFilename.contains("_P02_")) {
            fileType = "CLASSIC_OBIS";
        } else {
            fileType = "OBIS";
        }
        LOG.info("For file {} we detected type {}", currentFilename, fileType);
        return fileType;
    }
}
