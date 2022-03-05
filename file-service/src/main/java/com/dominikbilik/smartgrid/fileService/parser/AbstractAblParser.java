package com.dominikbilik.smartgrid.fileService.parser;

import com.dominikbilik.smartgrid.fileService.dto.measurements.Measurement;
import com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementTypeByTime;
import com.dominikbilik.smartgrid.fileService.dto.records.MeasurementRecord;
import com.dominikbilik.smartgrid.fileService.exception.SmartGridParsingException;
import com.dominikbilik.smartgrid.fileService.utils.common.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static com.dominikbilik.smartgrid.fileService.utils.ParserUtils.ABLUtils.*;

public abstract class AbstractAblParser<R extends MeasurementRecord, M extends Measurement> extends MeasurementParser<M> {

    protected Map<String, String> parsedFilename;

    protected AbstractAblParser(MeasurementType measurementType, MeasurementTypeByTime measurementTypeByTime) {
        super(measurementType, measurementTypeByTime);
    }

    /**
     * 1. Get rid of all null and empty lines
     * 2. Split text according to header and body tag
     * @return - tuple of <Header, Body>
     */
    protected Tuple2<List<String>, List<String>> stripAndSplit() {
        lines.removeIf(StringUtils::isEmpty);

        if (!isHeaderTagLine(lines.get(0))) {
            throw new SmartGridParsingException("AbstractAblParser: First line of file is not a specified header tag [" + HEADER_TAG + "]");
        }

        // find body tag
        Integer bodyTagIndex = null;
        for (int i = 0; i < lines.size(); i++) {
            if (isBodyTagLine(lines.get(i))) {
                bodyTagIndex = i;
                break;
            }
        }
        if (bodyTagIndex == null) {
            throw new SmartGridParsingException("AbstractAblParser: File does not contain body tag");
        }
        // we start from 1 because of header tag // +1 so we do not include tag itself
        return new Tuple2<>(lines.subList(1, bodyTagIndex), lines.subList(bodyTagIndex + 1, lines.size()));
    }

    protected void parseHeaders(List<String> headerLines) {
        for (int i = 0; i < headerLines.size(); i++) {
            String line = headerLines.get(i);
            if (line == null || line.isBlank()) {
                continue;
            }
            Tuple2<String, String> tuple = resolveHeaderLine(line);
            measurement.addHeaders(Collections.singletonMap(tuple.getT1(), tuple.getT2()));
        }
    }

    protected abstract void parseBody(List<String> bodyLines);

    /**
     * Returns Tuple2 <Header Key, Header value>
     */
    protected Tuple2<String, String> resolveHeaderLine(String line) {
        String[] splitted = line.split("=", 2);
        return new Tuple2<>(splitted[0].strip(), splitted[1].strip());
    }

    protected boolean isHeaderTagLine (String line) {
        return line != null && line.strip().equals(HEADER_TAG);
    }

    protected boolean isBodyTagLine (String line) {
        return line != null && line.strip().contains(BODY_TAG);
    }

    @Override
    protected void validateInput() {
        if (fileName == null || fileName.isEmpty()) {
            throw new SmartGridParsingException("Not valid filename");
        }
        if (lines != null && !lines.isEmpty()) {
            int size = 0;
            final Iterator<String> each = lines.iterator();
            while (each.hasNext()) {
                if (!ObjectUtils.isEmpty(each.next())) {
                    size++;
                }
            }
            if (size >= MINIMAL_FILE_SIZE) {
                return;
            }
        }
        throw new SmartGridParsingException("Input text is not valid");
    }

    protected Map<String, String> parseFileName(String fileName) {
        Map<String, String> map = new HashMap<>();
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }

        String[] values = fileName.split(FILENAME_SEPARATOR);
        String[] keys = FILENAME_PATTERN.split(FILENAME_SEPARATOR);

        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if ("DATETIME".equalsIgnoreCase(keys[i])) {
                continue; // datetime from header does not contain seconds, which is also important
            }
            map.put(keys[i], value);
        }
        return map;
    }
}
