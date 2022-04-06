package com.dominikbilik.smartgrid.fileService.parser;

import com.dominikbilik.smartgrid.fileService.TestUtils;
import com.dominikbilik.smartgrid.fileService.service.parser.FileParserService;
import com.dominikbilik.smartgrid.fileService.service.parser.impl.FileParserServiceImpl;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.MultiMeasurementRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class ObisMultiMeasurementParserTest {

    private FileParserService fileParserService = new FileParserServiceImpl();

    @Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // P01
                {"P01/03046188_P01_2110201836.abl", LocalDateTime.of(2021, 10, 20, 18, 36, 06).minusMinutes(1483), LocalDateTime.of(2021, 10, 20, 18, 36, 06)},
                {"P01/05725306_P01_2109182006.abl", LocalDateTime.of(2021, 9, 18, 20, 6, 10).minusMinutes(1483), LocalDateTime.of(2021, 9, 18, 20, 6, 10)},
                {"P01/06416324_P01_2108180004.abl", LocalDateTime.of(2021, 8, 18, 0, 4, 33).minusMinutes(1483), LocalDateTime.of(2021, 8, 18, 0, 4, 33)},
                {"P01/08207547_P01_2110160905.abl", LocalDateTime.of(2021, 10, 16, 9, 5, 39).minusMinutes(1483), LocalDateTime.of(2021, 10, 16, 9, 5, 39)},
                {"P01/08207547_P01_2110231706.abl", LocalDateTime.of(2021, 10, 23, 17, 6, 8).minusMinutes(1483), LocalDateTime.of(2021, 10, 23, 17, 6, 8)},

                // P02
                {"P02/03046188_P02_2109301836.abl", LocalDateTime.of(2021, 9, 30, 18, 36, 44).minusMinutes(29), LocalDateTime.of(2021, 9, 30, 18, 36, 44)},
                {"P02/05725306_P02_2110070806.abl", LocalDateTime.of(2021, 10, 7, 8, 7, 3).minusMinutes(30), LocalDateTime.of(2021, 10, 7, 8, 7, 3)},
                {"P02/06416324_P02_2110061436.abl", LocalDateTime.of(2021, 10, 6, 14, 36, 35).minusMinutes(28), LocalDateTime.of(2021, 10, 6, 14, 36, 35)},
                {"P02/06416324_P02_2110171205.abl", LocalDateTime.of(2021, 10, 17, 12, 6, 8).minusMinutes(29), LocalDateTime.of(2021, 10, 17, 12, 6, 8)},
                {"P02/08207547_P02_2109181636.abl", LocalDateTime.of(2021, 9, 18, 16, 36, 45).minusMinutes(29), LocalDateTime.of(2021, 9, 18, 16, 36, 45)}

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
        MultiValuesMeasurement<MultiMeasurementRecord> measurement =  fileParserService.parseMultiMeasurementFile(lines, absoluteFilePath.split("/")[1], MeasurementType.CLASSIC_OBIS.toString());

        // then ->
        assertThat(measurement).isNotNull();
        assertThat(measurement.getMeasurementType()).isNotNull();
        assertThat(measurement.getMeasurementTypeByTime()).isNotNull();
        assertThat(measurement.getFrom()).isNotNull();
        assertThat(measurement.getTo()).isNotNull();
        assertThat(measurement.getDeviceId()).isNotNull();
        assertThat(measurement.getDeviceDataset()).isNotNull();
        assertThat(measurement.getHeaders()).isNotNull();
        assertThat(measurement.getRecords()).isNotNull();
        assertThat(measurement.getSourceFileName()).isNotNull();
        assertThat(measurement.getMeasurementsCount()).isNotNull().isNotNegative();
        assertThat(measurement.getFrequencyInMinutes()).isNotNull().isNotNegative();
        assertThat(measurement.getFrequencyInMinutes()).isEqualTo(1);

        assertThat(measurement.getQuantityNames()).isNotNull();
        assertThat(measurement.getQuantityUnits()).isNotNull();
        assertThat(measurement.getQuantityNames().length == measurement.getRecords().get(0).getValues().length);

        assertThat(measurement.getFrom().isBefore(measurement.getTo()));
        assertThat(measurement.getRecords().size()).isGreaterThan(1);
        assertThat(measurement.getRecords().get(0).getDateTime().equals(measurement.getTo()));

        assertThat(measurement.getFrom()).isEqualToIgnoringNanos(from);
        assertThat(measurement.getTo()).isEqualToIgnoringNanos(to);

        //System.out.println(TestUtils.getJsonString(measurement));
    }
}
