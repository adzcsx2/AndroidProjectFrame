package com.iim.ego.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hoyn on 17/4/19.
 */

public class DateUtil {
    public static String dateToString(Date time){
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat ("yyyy-MM-dd");
        String ctime = formatter.format(time);

        return ctime;
    }
}
