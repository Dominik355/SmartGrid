package com.dominikbilik.smartgrid.parser;

import com.dominikbilik.smartgrid.parser.utils.ParserUtils;
import com.google.common.base.Splitter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

@SpringBootApplication
public class ParserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParserApplication.class, args);
    }


    @PostConstruct
    public void nanana() {

    }

}
