package com.dominikbilik.smartgrid.measureddata;

import com.dominikbilik.smartgrid.measureddata.domain.entity.QuantityDetail;
import com.dominikbilik.smartgrid.measureddata.domain.entity.dataSet.DeviceDataSet;
import com.dominikbilik.smartgrid.measureddata.domain.repository.DataSetRepository;
import com.dominikbilik.smartgrid.measureddata.domain.repository.QuantityDetailRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MeasurementApplication.class, H2Config.class})
@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JdbcTest {

    @Autowired
    DataSetRepository dataSetRepository;

    @Autowired
    QuantityDetailRepository quantityDetailRepository;

    @Autowired
    JdbcAggregateTemplate temlate;

    //@Test
    public void testFindByObisCode() {
        QuantityDetail quantityDetail = new QuantityDetail(
                "kW",
                true,
                "1",
                "1",
                "1",
                "1",
                "1",
                "1"
        );

        quantityDetail = quantityDetailRepository.save(quantityDetail);

        System.out.println(quantityDetailRepository.findByObisCode("kW",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1"));
    }

    //@Test
    public void testRelationships() {
        DeviceDataSet dataset = null;
        QuantityDetail quantityDetail10 = null;
        try {
            quantityDetail10 = new QuantityDetail();
            quantityDetail10.setName("prud");
            quantityDetail10.setUnit("Amper");

            quantityDetail10 = quantityDetailRepository.save(quantityDetail10);

            dataset = new DeviceDataSet();
            dataset.setVersion(0);
            dataset.setDataSetName("dataset name");
            dataset.setReferenceDeviceId(6846L);
            dataset.setQuantityTypeName("CLASSIC");
            dataset.addQuantityDetail(quantityDetail10);

            dataset = dataSetRepository.save(dataset);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            dataSetRepository.findAllByReferenceDeviceId(6846L).get(0).getQuantities().forEach(System.out::println);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            QuantityDetail quantityDetail = new QuantityDetail();
            quantityDetail.setName("napetie");
            quantityDetail.setUnit("volt");

            QuantityDetail quantityDetail1 = new QuantityDetail();
            quantityDetail1.setName("ziarenie");
            quantityDetail1.setUnit("lux");


            Iterable<QuantityDetail> details = quantityDetailRepository.saveAll(Arrays.asList(quantityDetail1, quantityDetail));
            dataset.addQuantityDetails(details);
            dataSetRepository.save(dataset);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            dataSetRepository.findAllByReferenceDeviceId(6846L).get(0).getQuantities().forEach(System.out::println);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            dataset.getQuantities().remove(quantityDetail10);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        try {
            System.out.println(quantityDetailRepository.findAllById(dataset.getQuantityDetailIds()));
            System.out.println(dataSetRepository.findByQuantityDetailId(quantityDetail10.getId()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            dataset = new DeviceDataSet();
            dataset.setVersion(0);
            dataset.setDataSetName("dataset 2");
            dataset.setReferenceDeviceId(6947999L);
            dataset.setQuantityTypeName("OBIS");
            dataset.addQuantityDetail(quantityDetail10);

            dataset = dataSetRepository.save(dataset);

            System.out.println(dataSetRepository.findByQuantityDetailId(quantityDetail10.getId()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
