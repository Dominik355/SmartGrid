package com.dominikbilik.smartgrid.fileService.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    public static List<String> getLinesOfTextFile(MultipartFile file) throws IOException {
        return getLinesOfTextFile(file, StandardCharsets.UTF_8);
    }

    public static List<String> getLinesOfTextFile(MultipartFile file, Charset charset) throws IOException {
        return new BufferedReader(new InputStreamReader(file.getInputStream(), charset))
                .lines()
                .collect(Collectors.toList());
    }

}
