package com.dominikbilik.smartgrid.fileService.parser.mappers;

import com.dominikbilik.smartgrid.fileService.parser.holders.ObisHolder;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.ObisInfoRecord;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.ObisSingleMeasurementRecord;

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
        return (holder == null) ? null : new ObisSingleMeasurementRecord.Builder()
                .withMedium(((holder.getA() != null) ? Integer.parseInt(holder.getA()) : null))
                .withChannel(((holder.getB() != null) ? Integer.parseInt(holder.getB()) : null))
                .withMeasurementVariable(((holder.getC() != null) ? Integer.parseInt(holder.getC()) : null))
                .withMeasurementType(((holder.getD() != null) ? Integer.parseInt(holder.getD()) : null))
                .withTariff(((holder.getE() != null) ? Integer.parseInt(holder.getE()) : null))
                .withPreviousMeasurement(((holder.getF() != null) ? Integer.parseInt(holder.getF()) : null))
                .withValue(((holder.getValue() != null) ? Double.parseDouble(holder.getValue()) : null))
                .withUnit(holder.getUnit())
                .withDescription(holder.getDescription())
                .build();
    }

}
