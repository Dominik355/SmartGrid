package com.dominikbilik.smartgrid.fileService.parser;

import com.dominikbilik.smartgrid.fileService.TestUtils;
import com.dominikbilik.smartgrid.fileService.service.parser.FileParserService;
import com.dominikbilik.smartgrid.fileService.service.parser.impl.FileParserServiceImpl;

import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.ObisSingleMeasurementRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@RunWith(Parameterized.class)
public class ObisSingleMeasurementParserTest {

    private FileParserService fileParserService = new FileParserServiceImpl();

    @Parameters
    public static Object[] data() {
        return new String[] {
                "DR/03046188_DR_2110310701.abl",
                "DR/05725306_DR_2110210231.abl",
                "DR/05725306_DR_2111010531.abl",
                "DR/06416324_DR_2111021401.abl",
                "DR/08207547_DR_2110171132.abl"
        };
    }

    @Parameter
    public String absoluteFilePath;

    @Test
    public void successfullTest() {
        // given -> load file
        ArrayList<String> lines = (ArrayList<String>) TestUtils.turnTextFileIntoLines(TestUtils.getResourceFile(absoluteFilePath));

        // when -> create instance of parser and call parse method
        SingleValuesMeasurement<ObisSingleMeasurementRecord> measurement =  fileParserService.parseSingleMeasurementFile(lines, absoluteFilePath.split("/")[1], MeasurementType.OBIS.toString());

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
        assertThat(measurement.getInfoRecords()).isNotNull();
        assertThat(measurement.getSourceFileName()).isNotNull();

        assertThat(measurement.getFrom().isEqual(measurement.getTo()));
        assertThat(measurement.getDeviceDataset()).isEqualTo("DR");
        assertThat(measurement.getInfoRecords().size()).isGreaterThan(1);
        assertThat(measurement.getRecords().size()).isGreaterThan(1);
        assertThat(measurement.getRecords().get(0).getUnit()).isNotNull();
        assertThat(measurement.getRecords().get(0).getDateTime().equals(measurement.getTo()));

        //System.out.println(TestUtils.getJsonString(measurement));
    }

}
