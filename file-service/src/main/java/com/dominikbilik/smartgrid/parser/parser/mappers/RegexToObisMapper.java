package com.dominikbilik.smartgrid.parser.parser.mappers;

import com.dominikbilik.smartgrid.parser.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.parser.dto.records.ObisInfoRecord;
import com.dominikbilik.smartgrid.parser.dto.records.ObisSingleMeasurementRecord;
import com.dominikbilik.smartgrid.parser.parser.holders.ObisHolder;

import java.time.LocalDateTime;
import java.util.regex.Matcher;

public class RegexToObisMapper {

    // based on actual regex pattern for obis codes. Parrent and therefore the number should never change
    private static int regexGroupCount = 12;

    public static ObisHolder regexResultToObisHolder(Matcher matcher) {
        if (matcher == null || !matcher.matches() || matcher.groupCount() != 12) {
            return null;
        }
        ObisHolder holder = new ObisHolder();
        for (int i = 1; i <= regexGroupCount; i++) {
            String value = matcher.group(i);
            if (value == null || value.isBlank()) {
                continue;
            }
            switch (i) {
                case 1 :
                    holder.setA(value);
                    break;
                case 2 :
                    holder.setB(value);
                    break;
                case 3 :
                    holder.setC(value);
                    break;
                case 4 :
                    holder.setD(value);
                    break;
                case 5 :
                    holder.setE(value);
                    break;
                case 6 :
                    break; // redundant group -> see ParserUtils.ABLUtils.OBIS_PATTERN
                case 7 :
                    holder.setF(value);
                    break;
                case 8 :
                    break; // redundant group -> see ParserUtils.ABLUtils.OBIS_PATTERN
                case 9 :
                    holder.setValue(value);
                    break;
                case 10 :
                    holder.setUnit(value);
                    break;
                case 11 :
                    break; // redundant group -> see ParserUtils.ABLUtils.OBIS_PATTERN
                case 12 :
                    holder.setDescription(value);
                    break;
            }
        }
        return holder;
    }

    public static boolean isInfoRecord(ObisHolder holder) {
        return holder.getUnit() == null;
    }

    public static ObisInfoRecord obisHolderToObisInfoRecord(ObisHolder holder) {
        return holder == null ? null : new ObisInfoRecord.Builder()
                .withC(holder.getC())
                .withD(holder.getD())
                .withE(holder.getE())
                .withF(holder.getF())
                .withDescription(holder.getDescription())
                .build();

    }

    public static ObisSingleMeasurementRecord obisHolderToObisSingleMeasurementRecord(ObisHolder holder) {
        return holder == null ? null : new ObisSingleMeasurementRecord.Builder<>()
                .withMedium(Integer.parseInt(holder.getA()))
                .withChannel(Integer.parseInt(holder.getB()))
                .withMeasurementVariable(Integer.parseInt(holder.getC()))
                .withMeasurementType(Integer.parseInt(holder.getD()))
                .withTariff(Integer.parseInt(holder.getE()))
                .withPreviousMeasurement(Integer.parseInt(holder.getF()))
                .withValue(Double.parseDouble(holder.getValue()))
                .withUnit(holder.getUnit())
                .withDescription(holder.getDescription())
                .withDateTime(LocalDateTime.now())
                .build();
    }

}
