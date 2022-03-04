package com.dominikbilik.smartgrid.fileService;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TestUtils {

    public static File getResourceFile(String absoluteFilePath) {
        return new File(Thread.currentThread().getContextClassLoader().getResource(absoluteFilePath).getFile());
    }

    public static List<String> turnTextFileIntoLines(File textFile) {
        try {
            return Files.readAllLines(textFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getJsonString(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
