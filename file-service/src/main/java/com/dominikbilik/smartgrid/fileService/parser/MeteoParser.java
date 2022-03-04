package com.dominikbilik.smartgrid.fileService.parser;

import com.dominikbilik.smartgrid.fileService.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.fileService.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.fileService.exception.SmartGridParsingException;
import com.dominikbilik.smartgrid.fileService.utils.ParserUtils;
import com.dominikbilik.smartgrid.fileService.utils.common.Tuple2;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementType.METEO;
import static com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementTypeByTime.PERIODICAL;
import static com.dominikbilik.smartgrid.fileService.utils.ParserUtils.MeteoUtils.*;

public class MeteoParser extends MeasurementParser<MultiValuesMeasurement> {

    private static final Logger LOG = LoggerFactory.getLogger(MeteoParser.class);

    public MeteoParser() {
        super(METEO, PERIODICAL);
        this.measurement = new MultiValuesMeasurement(METEO, PERIODICAL);
    }

    @Override
    public MultiValuesMeasurement parseToMeasurement(List<String> input, String fileName) {
        lines = input;
        this.fileName = fileName;

        try {
            // validate input
            validateInput();

            // we dont parse filename , cause here it says nothing, usually looks like 'download08'

            // split text into header and body
            Tuple2<List<String>, List<String>> headerBodyTuple = stripAndSplit();

            // split header which contains just names of columns like CSV -> just od it in paralell with both lines
            parseHeaders(headerBodyTuple.getT1());

            // split body and fill measurement -> take dateTime of first and last
            parseBody(headerBodyTuple.getT2());

            // fill other stuff
            measurement.setSourceFileName(fileName);
            measurement.setMeasurementsCount(((MultiMeasurementRecord) measurement.getRecords().get(0)).getValues().length);
            measurement.setFrom(((MultiMeasurementRecord) measurement.getRecords().get(measurement.getRecords().size() - 1)).getDateTime());
            measurement.setTo(((MultiMeasurementRecord) measurement.getRecords().get(0)).getDateTime());
            measurement.setFrequencyInMinutes(
                    (int) Math.abs(
                            ChronoUnit.MINUTES.between(
                                    ((MultiMeasurementRecord) measurement.getRecords().get(0)).getDateTime(),
                                    ((MultiMeasurementRecord) measurement.getRecords().get(1)).getDateTime())
                    )
            );

        } catch (Exception e) {
            throw new SmartGridParsingException(e.getMessage(), e);
        }

        return this.measurement;
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

    protected Tuple2<List<String>, List<String>> stripAndSplit() {
        lines.removeIf(Strings::isNullOrEmpty);
        // first - find line separating header and body - it should be third line
        if (!ParserUtils.MeteoUtils.isSeparatingLine(lines.get(2))) {
            throw new SmartGridParsingException("Your heading should consist of 2 lines... no more, no less");
        }
        return new Tuple2<>(lines.subList(0,2), lines.subList(3, lines.size()));
    }

    /**
     * just split on whitespaces
     * @param bodyLines
     */
    protected void parseBody(List<String> bodyLines) {
        for (String line : bodyLines) {
            String[] sepStrings = line.strip().split("\\s+");
            if (sepStrings == null || sepStrings.length < 2) {
                continue; // we need at least DATE, TIME and some VALUE ... eitherwise, we skip
            }

            // first 2 columns are dateTime. This should ot be hard coded.
            // correct way -> look at the column names, find the Date and Time value, find the indices and take those values
            // sometimes it doesnt correspond to pattern used. E.g. days or hours with 1 digit start without zero -> needs to be corrected
            String[] separatedDate = sepStrings[0].split("\\.");
            for (int i = 0; i < separatedDate.length; i++) {
                if (separatedDate[i].length() != 2) {
                    separatedDate[i] = "0" + separatedDate[i];
                }
            }

            String[] separatedTime = sepStrings[1].split(":");
            for (int i = 0; i < separatedTime.length; i++) {
                if (separatedTime[i].length() != 2) {
                    separatedTime[i] = "0" + separatedTime[i];
                }
            }
            LocalDateTime dateTime = null;
            try {
                dateTime = LocalDate.parse(String.join(".", separatedDate), ParserUtils.MeteoUtils.DATE_FORMAT)
                        .atTime(LocalTime.parse(String.join(":", separatedTime), ParserUtils.MeteoUtils.TIME_FORMAT));
            } catch (DateTimeParseException e ) {
                System.out.println("exception for : ");
                System.out.println("Date: " + Arrays.asList(separatedDate));
                System.out.println("Time: " + Arrays.asList(separatedTime));
            }


            // get rid of date and time and then parse
            Double[] values = Arrays.stream(Arrays.copyOfRange(sepStrings, 2, sepStrings.length))
                    .map(this::mapToDouble)
                    .toArray(Double[]::new);

            this.measurement.addRecord(new MultiMeasurementRecord.Builder<>()
                    .withValues(values)
                    .withDateTime(dateTime)
                    .build()
            );
        }
    }

    /**
     * not implemented - just go with hard defined values for now
     * @param headerLines
     */
    protected void parseHeaders(List<String> headerLines) {
        // parallel processing of both sentences equally. I will compare the values with the list values from
        // ParserUtils and add them to the half found columns to avoid duplication. In this way,
        // we can successfully parse the entire header using spaces, even if the gap does not indicate column separation.
        this.measurement.setQuantityNames(new String[] {"Date", "Time", "Temp Out", "Hi Temp", "Low Temp", "Out Hum",
                "Dew Pt.", "Wind Speed", "Wind Dir", "Wind Run", "Hi Speed", "Hi Dir", "Wind Chill", "Heat Index",
                "THW Index", "THSW Index", "Bar", "Rain", "Rain Rate", "Solar Rad.", "Solar Energy", "UV Index",
                "UV Dose", "Hi UV", "Heat D-D", "Cool D-D", "In Temp", "In Hum", "In Dew", "In Heat", "In EMC",
                "In Air Density", "ET", "Wind Samp", "Wind Tx", "ISS Recept", "Arc. Int."});
    }

    private Double mapToDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            // ignore
        }
        if (ParserUtils.MeteoUtils.WIND_DIRECTIONS.containsKey(s)) {
            return WIND_DIRECTIONS.get(s);
        }
        return null;
    }
}
