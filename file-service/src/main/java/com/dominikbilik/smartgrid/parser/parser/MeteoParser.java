package com.dominikbilik.smartgrid.parser.parser;

import com.dominikbilik.smartgrid.parser.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.parser.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.parser.exception.SmartGridParsingException;
import com.dominikbilik.smartgrid.parser.utils.ParserUtils;
import com.dominikbilik.smartgrid.parser.utils.common.Tuple2;
import com.google.common.base.Strings;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.dominikbilik.smartgrid.parser.dto.measurements.enums.MeasurementType.CLASSIC;
import static com.dominikbilik.smartgrid.parser.dto.measurements.enums.MeasurementTypeByTime.PERIODICAL;
import static com.dominikbilik.smartgrid.parser.utils.ParserUtils.MeteoUtils.MINIMAL_FILE_SIZE;
import static com.dominikbilik.smartgrid.parser.utils.ParserUtils.MeteoUtils.WIND_DIRECTIONS;

public class MeteoParser extends MeasurementParser<MultiValuesMeasurement> {

    public MeteoParser() {
        super(CLASSIC, PERIODICAL);
        this.measurement = new MultiValuesMeasurement(CLASSIC, PERIODICAL);
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
            measurement.setFrom(((MultiMeasurementRecord) measurement.getRecords().get(measurement.getRecords().size())).getDateTime());
            measurement.setTo(((MultiMeasurementRecord) measurement.getRecords().get(0)).getDateTime());
            measurement.setFrequencyInMinutes(1);

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
            String[] sepStrings = line.split("\\s+");
            if (sepStrings == null || sepStrings.length < 3) {
                continue; // we need at least DATE, TIME and some VALUE ... eitherwise, we skip
            }

            // first 2 columns are dateTime. This should ot be hard coded.
            // correct way -> look at the column names, find the Date and Time value, find the indices and take those values
            LocalDateTime dateTime = LocalDate
                    .parse(sepStrings[0], ParserUtils.MeteoUtils.DATE_FORMAT)
                    .atTime(LocalTime.parse(sepStrings[1], ParserUtils.MeteoUtils.TIME_FORMAT));

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
