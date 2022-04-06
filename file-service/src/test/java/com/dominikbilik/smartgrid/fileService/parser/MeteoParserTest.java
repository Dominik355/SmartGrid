package com.dominikbilik.smartgrid.fileService.parser;

import com.dominikbilik.smartgrid.fileService.TestUtils;
import com.dominikbilik.smartgrid.fileService.service.parser.impl.FileParserServiceImpl;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.MultiMeasurementRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@RunWith(Parameterized.class)
public class MeteoParserTest {

    private FileParserServiceImpl fileParserService = new FileParserServiceImpl();

    @Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"Meteo/meteo_875.txt", LocalDateTime.of(2021, 7, 19, 1, 0), LocalDateTime.of(2021, 7, 20, 20, 29)},
                {"Meteo/meteo_876.txt", LocalDateTime.of(2021, 7, 19, 1, 0), LocalDateTime.of(2021, 7, 19, 1, 38)},
                {"Meteo/meteo_877.txt", LocalDateTime.of(2021, 8, 24, 1, 0), LocalDateTime.of(2021, 8, 31, 7, 17)},
                {"Meteo/meteo_878.txt", LocalDateTime.of(2021, 10, 6, 3, 50), LocalDateTime.of(2021, 10, 6, 4, 9)}
        });
    }

    @Parameter(0)
    public String absoluteFilePath;

    @Parameter(1)
    public LocalDateTime from;

    @Parameter(2)
    public LocalDateTime to;

    @Test
    public void successfullTest() {
        // given -> load file
        ArrayList<String> lines = (ArrayList<String>) TestUtils.turnTextFileIntoLines(TestUtils.getResourceFile(absoluteFilePath));

        // when -> create instance of parser and call parse method
        MultiValuesMeasurement<MultiMeasurementRecord> measurement =  fileParserService.parseMultiMeasurementFile(lines, absoluteFilePath.split("/")[1], MeasurementType.METEO.toString());

        // then ->
        assertThat(measurement).isNotNull();
        assertThat(measurement.getMeasurementType()).isNotNull();
        assertThat(measurement.getMeasurementTypeByTime()).isNotNull();
        assertThat(measurement.getFrom()).isNotNull();
        assertThat(measurement.getTo()).isNotNull();
        // assertThat(measurement.getDeviceId()).isNotNull(); meteo doesnt provide device ID or name in filename or file header
        // assertThat(measurement.getDeviceName()).isNotNull();
        // assertThat(measurement.getHeaders()).isNotNull(); meteo does not contain file header
        assertThat(measurement.getRecords()).isNotNull();
        assertThat(measurement.getSourceFileName()).isNotNull();
        assertThat(measurement.getMeasurementsCount()).isNotNull().isNotNegative();
        assertThat(measurement.getFrequencyInMinutes()).isNotNull().isNotNegative();
        assertThat(measurement.getFrequencyInMinutes()).isEqualTo(1);

        assertThat(measurement.getQuantityNames()).isNotNull();
        // assertThat(measurement.getQuantityUnits()).isNotNull(); again meteo does not contain that
        assertThat(measurement.getQuantityNames().length == measurement.getRecords().get(0).getValues().length);

        assertThat(measurement.getFrom().isBefore(measurement.getTo()));
        assertThat(measurement.getRecords().size()).isGreaterThan(1);
        assertThat(measurement.getRecords().get(0).getDateTime().equals(measurement.getTo()));

        assertThat(measurement.getFrom().isEqual(from));
        assertThat(measurement.getTo().isEqual(to));

        //System.out.println(TestUtils.getJsonString(measurement));
    }

}
