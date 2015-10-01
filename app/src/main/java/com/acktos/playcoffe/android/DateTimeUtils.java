package com.acktos.playcoffe.android;

import android.util.Log;

import com.acktos.playcoffe.controllers.BaseController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
	
	
	public static final String LATIN_FORMAT="LLL dd, yyyy hh:mm a";
	public static final String DB_FORMAT="yyyy-MM-dd HH:mm:ss"; 
	public static final String DIALOG_FORMAT="yyyy-MM-dd hh:mm a";
	
	/**
	 * Convert date time DB to human read date
	 */
	public static String toLatinDate(String stringDate){
		
		return convertDate(stringDate,DB_FORMAT,LATIN_FORMAT);
	}
	
	/**
	 * Convert human-read date to dbTime
	 */
	public static String toDBTime(String stringDate){
		
		return convertDate(stringDate,LATIN_FORMAT,DB_FORMAT);
	}
	
	public static String convertDate(String dateToConvert,String inFormat, String outFormat){
		
		String latinStringDate="";
		SimpleDateFormat dbFormat=new SimpleDateFormat(inFormat,Locale.getDefault());
		SimpleDateFormat latinFormat=new SimpleDateFormat(outFormat,Locale.getDefault());
		
		try {
			Date dbDate = dbFormat.parse(dateToConvert);
			latinStringDate=latinFormat.format(dbDate);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return latinStringDate;
	}

	public static String getCurrentTime(){

		final Calendar c=Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat(DB_FORMAT,Locale.getDefault());

		return sdf.format(c.getTime());
	}

	public static String millisecondsToMinutes(String milliseconds){



        Double millis=Double.parseDouble(milliseconds);
        int seconds=(int)(millis/1000)%60;
        int minutes=(int)((millis-seconds)/1000)/60;
        String secondsString=seconds+"";
        if(seconds<10){
            secondsString="0"+seconds;
        }

        return minutes+":"+secondsString;
    }

	public static final String timestampToDate(long timestamp){

        DateFormat sdf=new SimpleDateFormat("hh:mm:ss dd-MM-yyyy",Locale.getDefault());
        Date date=new Date(timestamp);
        return sdf.format(date);

    }

}
