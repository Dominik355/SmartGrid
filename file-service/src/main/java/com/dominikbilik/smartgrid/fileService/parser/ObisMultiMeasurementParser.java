package com.dominikbilik.smartgrid.fileService.parser;

import com.dominikbilik.smartgrid.fileService.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.fileService.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.fileService.exception.SmartGridParsingException;
import com.dominikbilik.smartgrid.fileService.utils.common.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementType.OBIS;
import static com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementTypeByTime.PERIODICAL;

public class ObisMultiMeasurementParser extends AbstractAblParser<MultiMeasurementRecord, MultiValuesMeasurement> {

    private static final Logger LOG = LoggerFactory.getLogger(ObisMultiMeasurementParser.class);

    private String valueSeparator;
    private String headerSeparator;

    public ObisMultiMeasurementParser() {
        super(OBIS, PERIODICAL);
        this.measurement = new MultiValuesMeasurement(OBIS, PERIODICAL);
    }

    @Override
    public MultiValuesMeasurement parseToMeasurement(List<String> input, String fileName) {
        lines = input;
        this.fileName = fileName;
        measurement.setSourceFileName(fileName);
        try {
            // validate input
            validateInput();

            // parse filename
            parsedFilename = parseFileName(fileName);
            measurement.addHeaders(parsedFilename);

            // split text into header and body
            Tuple2<List<String>, List<String>> headerBodyTuple = stripAndSplit();

            // split headers and fill Measurement
            parseHeaders(headerBodyTuple.getT1());

            // split body and fill measurement
            parseBody(headerBodyTuple.getT2());

        } catch (Exception e) {
            throw new SmartGridParsingException(e.getMessage(), e);
        }

        return this.measurement;
    }

    @Override
    protected void parseBody(List<String> bodyLines) {
        // first we need to find header of body telling us about columns
        Integer bodyHeaderIndex = null;
        String tag = resolveNameToTag(parsedFilename.get("NAME"));
        for (int i = 0; i < bodyLines.size(); i++) {
            String line = bodyLines.get(i);
            if (line.contains(tag)) {
                bodyHeaderIndex = i;
                parseHeaderBodyLine(line, tag);
                break;
            }
        }
        if (bodyHeaderIndex == null) {
            throw new SmartGridParsingException("Could not find Header line of body part describing columns of body");
        }

        // now we parse header line
        parseHeaderBodyLine(bodyLines.get(bodyHeaderIndex), tag);

        // sublist value lines
        List<String> bodyValueLines= bodyLines.subList(bodyHeaderIndex + 1, bodyLines.size());

        // parse value lines; realOrder - in case we have the wrong line, we want to deduct the correct number from the time reference value
        for (int i = bodyHeaderIndex + 1, realOrder = 0; i < bodyLines.size(); i++) {
            try {
                Double[] values = parseValueBodyLine(bodyLines.get(i));
                if (values == null || values.length != measurement.getMeasurementsCount()) {
                    continue;
                }
                this.measurement.addRecord(new MultiMeasurementRecord.Builder<>()
                        .withValues(values)
                        .withDateTime(measurement.getDateTimeFromHeader().orElseThrow(
                                        () -> new SmartGridParsingException("Missing DateTime in Measurement, can not define record datetime")
                                ).minusMinutes(realOrder * measurement.getFrequencyInMinutes()))
                        .build());
                realOrder++;
            } catch (Exception e) {
                LOG.info("Invalid line found : " + bodyLines.get(i) + " exception : " + e.getMessage());
                // we found invalid line -> just ignore that
            }
        }

    }

    private Double[] parseValueBodyLine(String line) {
        return Arrays.stream(line.substring(1, line.length() - 1).split("\\)\\("))
                .map(Double::valueOf)
                .toArray(Double[]::new);
    }

    /**
     * @param line - example: "C.1.0(06416324)P.01(1211017230100)(00000000)(1)(6)(1.5)(kW)(2.5)(kW)(5.5)(kvar)(6.5)(kvar)(7.5)(kvar)(8.5)(kvar)"
     * C.1.0 is OBIS code for device ID. So we are looking for P.01, which is register number. If we find it,
     * we ignore first 2 brackets. Then 3rd bracket is frequency in minutes and 6th is number of measurements.
     * Then goes name in obis code and unit for every column.
     */
    private void parseHeaderBodyLine(String line, String tagToStart) {
        String[] separated = line
                .substring(line.indexOf(tagToStart) + tagToStart.length() + 1, line.length() - 1)
                .split("\\)\\(");

        String[] measurementInfo = Arrays.copyOfRange(separated, 0, 4);
        String[] columnInfo = Arrays.copyOfRange(separated, 4, separated.length);

        if (measurementInfo.length != 4 && columnInfo.length < 2 && columnInfo.length % 2 != 0) {
            throw new SmartGridParsingException("Body-Header line has invalid format");
        }

        // first resolve info brackets - we ignore first 2, then take frequency and count
        for (int i = 0; i < measurementInfo.length; i++) {
            switch (i) {
                case 0:
                case 1:
                    break;
                case 2:
                    this.measurement.setFrequencyInMinutes(Integer.parseInt(measurementInfo[i]));
                    break;
                case 3:
                    this.measurement.setMeasurementsCount(Integer.parseInt(measurementInfo[i]));
                    break;
            }
        }

        String[] quantityNames = new String[this.measurement.getMeasurementsCount()];
        String[] quantityUnits = new String[this.measurement.getMeasurementsCount()];
        for (int i = 0; i < columnInfo.length; i+=2) {
            quantityNames[i/2] = columnInfo[i];
            quantityUnits[i/2] = columnInfo[i+1];
        }
        this.measurement.setQuantityNames(quantityNames);
        this.measurement.setQuantityUnits(quantityUnits);
    }

    /**
     * Type (usually name of register belonging to device) needs to have 2 parts. Part of letters and part of number(optional).
     * Examples:[P01, DR]. If number is contained, it is separated with dot. This string us used to identify start of recognized
     * header line in file.
     */
    private String resolveNameToTag(String name) {
        StringBuilder finder = new StringBuilder();
        for (char c : name.toCharArray()) {
            if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
                finder.append(c);
            } else {
                if (finder.length() < 1) {
                    throw new SmartGridParsingException("Invalid Type in filename. Type needs to have 2 parts. First are letters, then numbers (optional)");
                }
                finder.append(".");
                return finder.append(name.substring(finder.length()-1)).toString();
            }
        }
        // if we get here, name does not contain any numbers, so we just return name;
        return name;
    }
}
