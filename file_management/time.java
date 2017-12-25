/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file_management;

import java.util.Date;
import java.util.TreeMap;

/**
 *
 * @author urvish
 */
public class time {
 
    static String getCurrDateAndTime()
    {
    	Date d = new Date();
    	String[] dt = d.toString().split(" ");
    	String res = "";
    	TreeMap<String,String> tm = new TreeMap();
    	tm.put("Jan", "01");    	tm.put("Feb", "02");    	tm.put("Mar", "03");    	tm.put("Apr", "04");
    	tm.put("May", "05");    	tm.put("Jun", "06");    	tm.put("Jul", "07");    	tm.put("Aug", "08");
    	tm.put("Sep", "09");    	tm.put("Oct", "10");    	tm.put("Nov", "11");    	tm.put("Dec", "12");
    	res=dt[2]+tm.get(dt[1])+dt[5]+dt[3].charAt(0)+dt[3].charAt(1)+dt[3].charAt(3)+dt[3].charAt(4);
    	return res;
    }
}
