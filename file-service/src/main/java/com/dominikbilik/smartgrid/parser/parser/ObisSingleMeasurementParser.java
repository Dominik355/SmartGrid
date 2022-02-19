package com.dominikbilik.smartgrid.parser.parser;

import com.dominikbilik.smartgrid.parser.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.parser.dto.records.ObisSingleMeasurementRecord;
import com.dominikbilik.smartgrid.parser.exception.SmartGridParsingException;
import com.dominikbilik.smartgrid.parser.parser.holders.ObisHolder;
import com.dominikbilik.smartgrid.parser.parser.mappers.RegexToObisMapper;
import com.dominikbilik.smartgrid.parser.utils.common.Tuple2;

import java.util.List;
import java.util.regex.Pattern;

import static com.dominikbilik.smartgrid.parser.dto.measurements.enums.MeasurementType.OBIS;
import static com.dominikbilik.smartgrid.parser.dto.measurements.enums.MeasurementTypeByTime.INSTANT;
import static com.dominikbilik.smartgrid.parser.utils.ParserUtils.ABLUtils.OBIS_PATTERN;

public class ObisSingleMeasurementParser extends AbstractAblParser<ObisSingleMeasurementRecord, SingleValuesMeasurement> {

    protected Pattern pattern = Pattern.compile(OBIS_PATTERN);

    public ObisSingleMeasurementParser() {
        super(OBIS, INSTANT);
        this.measurement = new SingleValuesMeasurement(OBIS, INSTANT);
    }

    @Override
    public SingleValuesMeasurement parseToMeasurement(List<String> input, String fileName) {
        this.fileName = fileName;
        this.lines = input;
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
    public void parseBody(List<String> bodyLines) {
        if (bodyLines == null || bodyLines.isEmpty() || bodyLines.size() < 2) {
            throw new SmartGridParsingException("Body of file is not valid");
        }

        for (int i = 0; i < bodyLines.size(); i++) {
            ObisHolder holder = parseValueBodyLine(bodyLines.get(i));
            if (holder != null) {
                if (holder.getUnit() != null) {
                    measurement.addRecord(RegexToObisMapper.obisHolderToObisSingleMeasurementRecord(holder));
                    continue;
                }
                measurement.addInfoRecord(RegexToObisMapper.obisHolderToObisInfoRecord(holder));
            } else {
                if (bodyLines.get(i).equals("!")) {
                    break;
                }
            }
        }
    }

    private ObisHolder parseValueBodyLine(String line) {
        try {
            return RegexToObisMapper.regexResultToObisHolder(pattern.matcher(line));
        } catch (Exception e) {
            return null; // if any exception is thrown, we just ignore that line
        }
    }

}
