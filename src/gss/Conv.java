package gss;

import java.io.*;
import java.util.*;

public class Conv implements Serializable
{
	public static final long serialVersionUID = -6761605287408719857L;

	public String body="";
	public String type=""; // 0 date, 1 received, 2 sent
	public String date="";
	public int mpos=0; // ( 1 || 2 ) 1 default, 2 top , 3 bottom

	public Conv(String b, String d, String t)
	{
		this.body = b;
		this.date = d;
		this.type = t;
	}
	public String toHtml()
	{
		if (type.equals("0"))
		{
			return "<div class=\"date-header\">" + body + "</div>\n";
		}
		else if (type.equals("1") || type.equals("2"))
		{
			Date d=new Date(Long.parseLong(date));
			String temp=
				"<div class=\"message " + (type.equals("1") ? "received": "sent") + " " + (mpos == 1 ?"group": mpos == 2 ?"group top": mpos == 3 ? "group bottom": "") + "\">\n" +
				"<div>" + body + "</div>\n" +
				"<div class=\"time\">" + formatTime(d) + "</div>\n" +
				"</div>\n";

			return temp;
		}
		return "";
	}
	public static String formatDateHeader(Date d)
	{
		String[] weeks={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
		String[] months={"January","February","March","April","Ma6","June","July","Augest","September","October","November","December"};
		String rt=weeks[d.getDay()] + " " + d.getDate() + " " + months[d.getMonth()];
		GregorianCalendar c=new GregorianCalendar();
		c.setTime(d);
		int k=c.get(c.YEAR);
		rt += ", " + k;
		return rt;
	}
	public static String formatTime(Date d)
	{
		String rt = String.format("%02d:%02d", d.getHours() % 12, d.getMinutes());
		rt += " " + ((d.getHours() / 12) == 1 ?"pm": "am");
		return rt;

	}
}
