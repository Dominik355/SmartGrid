package com.dominikbilik.smartgrid.fileService.utils;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserUtils {

    public static class ABLUtils {

        public static final String HEADER_TAG = "[HEADER]";
        // [PDATA] and [VDATA] is used, so instead of using pattern, i consider this to be enough (just use contains instead equals)
        public static final String BODY_TAG = "DATA]";
        // not every abl file does have ending tag, for example DR registers use it, so its optional
        public static final String OBIS_ENDING_TAG = "!";

        public static final int MINIMAL_FILE_SIZE = 7;

        public static boolean isHeaderTagLine(String line) {
            return line != null && line.strip().equals(HEADER_TAG);
        }

        public static boolean isBodyTagLine(String line) {
            return line != null && line.strip().contains(BODY_TAG);
        }

        public static boolean isEndingLine(String line) {
            return line != null && line.strip().equals(OBIS_ENDING_TAG);
        }

        public static final List<String> HEADERS = ListUtils.unmodifiableList(new ArrayList<>(){{
                add("PROT");
                add("MAN1");
                add("ZNR1");
                add("DATE");
                add("TIME");
        }});

        public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy");
        public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

        /**
         * This regex pattern should be able to recognize all possible variants of obis records.
         * Price for this are unnecesarry groups. It should be clear after reviewing these examples:
         * Input: "4-8:1.6.2*03(06.06*kW)(1210908210000)"
         * Result: Group 1: 4 = 7;
         *
         *         public static boolean isHeaderTagLine(String line) {
         *             return line != null && line.strip().equals(HEADER_TAG);
         *         }
         *
         *         public static boolean isBodyTagLine(String line) {
         *             return line != null && line.strip().contains(BODY_TAG);
         *         }
         *
         *         public static boolean isEndingLine(String line) {
         *             return line != null && line.strip().equals(OBIS_ENDING_TAG);
         *         }
         *
         *         public static final List<String> HEADERS = ListUtils.unmodifiableList(new ArrayList<>(){{
         *                 add("PROT");
         *                 add("MAN1");
         *                 add("ZNR1");
         *                 add("DATE");
         *                 add("TIME");
         *         }});
         *
         *         public static final DateTimeFormatter DATE_FOR
         *         Group 2: 8
         *         Group 3: 1
         *         Group 4: 6
         *         Group 5: 2
         *         Group 6: *03
         *         Group 7: 03
         *         Group 8: (06.06*kW)
         *         Group 9: 06.06
         *         Group 10: kW
         *         Group 11: (1210908210000)
         *         Group 12: 1210908210000
         * Input: "C.1.0(05725306)"
         * Result: Group 1:
         *         Group 2:
         *         Group 3: C
         *         Group 4: 1
         *         Group 5: 0
         *         Group 6: null
         *         Group 7: null
         *         Group 8: null
         *         Group 9: null
         *         Group 10: null
         *         Group 11: (05725306)
         *         Group 12: 05725306
         * Input: "F.F(00000000)"
         * Result: Group 1:
         *         Group 2:
         *         Group 3: F
         *         Group 4: F
         *         Group 5:
         *         Group 6: null
         *         Group 7: null
         *         Group 8: null
         *         Group 9: null
         *         Group 10: null
         *         Group 11: (00000000)
         *         Group 12: 00000000
         */
        public static final String OBIS_PATTERN = "^(\\d{0,3})-?(\\d{0,3}):?([0-9CFP]{1,3}).([0-9CFP]{1,3}).?(\\d{0,3})([*&](\\d{0,3}))?(\\((\\d*\\.?\\d*)\\*([a-zA-Z]+)\\))?\\s*(\\(([0-9]+)\\))?$";

        public static final String FILENAME_SEPARATOR = "_";
        public static final String FILENAME_PATTERN = "ID_DATASET_DATETIME";
        public static final DateTimeFormatter FILENAME_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmm");

    }

    public static class MeteoUtils {

        public static final String[] KNOWN_WORDS = {
                "Date", "Time", "Temp", "Out", "Hi", "Low", "Dew", "Wind", "Speed", "Pt.",
                "Dir", "Run", "Chill", "Heat", "THW", "Bar", "Rain", "Rate", "Solar", "Rad.", "Energy",
                "UV", "Index", "Dose", "Hi Solar", "D-D", "Cool", "In", "Hum", "THSW",
                "EMC", "Density", "In Air", "ET", "Samp", "Tx", "ISS", "Recept", "Arc.", "Int."
        };

        public static final String[]  KNOWN_VARIABLES = {
                "Date", "Time", "Temp Out", "Hi Temp", "Low Temp", "Out Hum", "Dew Pt.", "Wind Speed", "Wind Dir",
                "Wind Run", "Hi Speed", "Hi Dir", "Wind Chill", "Heat Index", "THW Index", "THSW IIndex", "Bar",
                "Rain", "Rain Rate", "Solar Rad.", "Solar Energy", "Hi Solar Rad.", "UV Index", "UV Dose", "HI UV",
                "Heat D-D", "Cool D-D", "In Temp", "In Hum", "In Dew", "In Heat", "In EMC", "In Air Density", "ET",
                "Wind Samp", "Wind TX", "ISS Recept", "Arc. Int."
        };

        public static final Map<String, Double> WIND_DIRECTIONS = MapUtils.unmodifiableMap(new HashMap<>(){{
                    put("N",0d);
                    put("NNe",22.5d);
                    put("NE",45d);
                    put("ENE",67.5d);
                    put("E",90d);
                    put("ESE",112.5d);
                    put("SE",135d);
                    put("SSE",157.5d);
                    put("S",180d);
                    put("SSW",202.5d);
                    put("SW",225d);
                    put("WSW",247.5d);
                    put("W",270d);
                    put("WNW",292.5d);
                    put("NW",315d);
                    put("NNW",337.5d);
        }});

        public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy");
        public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

        public static final int MINIMAL_FILE_SIZE = 4; // 2 lines in header, 1 separator and 1 values

        public static final String METEO_HEADER_BODY_SEPARATING_REGEX = "-";

        public static boolean isSeparatingLine(String line) {
            return (line.contains("-") && line.strip().split("-").length == 0);
        }

    }

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yy");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
}
