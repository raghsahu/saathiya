package com.prometteur.sathiya.utills;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser {
        private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
        private static DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");

        public static String convertDateToString(String strDate) {
            Date date= null;
            try {
                date = dateFormat.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String strDateFormatted = "";
            strDateFormatted = dateFormat1.format(date);
            return strDateFormatted;
        }
    }