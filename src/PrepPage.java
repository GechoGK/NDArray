import gss.*;
import java.io.*;
import java.util.*;

public class PrepPage
{
	public static void prepareMessage(String[] args) throws Exception
	{

		new PrepPage().a();


	}
	void a() throws Exception
	{
		String num="0962701353";
		// num = "unknown SH";
		ArrayList<Conv> convs=(ArrayList<Conv>)read("/sdcard/converdations/" + num + ".txt");
		String hPage=preparePage(convs);
		savePage(hPage, num);
		System.out.println("page setup completed!");
	}
	void savePage(String cont, String num) throws Exception
	{
		FileOutputStream fos=new FileOutputStream("/sdcard/converdations/" + num + ".html");
		fos.write(cont.getBytes());
		fos.flush();
		fos.close();
		System.out.println("saved!");
	}
	String preparePage(ArrayList<Conv> convs)
	{
		for (Conv c:convs)
			add(c);
		convs.clear();
		for (Grp g:days)
		{
			g.prep();
			convs.addAll(g.convr);
		}

		StringBuilder sb=new StringBuilder();
		sb.append(getHeader());
		sb.append("\n<body>");
		sb.append("<div class=\"container\">");
		for (Conv c: convs)
		{
			sb.append(c.toHtml());
		}

		sb.append("</div>\n</body>\n</html>\n");
		return sb.toString();
	}
	public ArrayList<Grp> days=new ArrayList<>();
	public Grp gr=null;
	void add(Conv c)
	{
		Date d=new Date(Long.parseLong(c.date));
		if (gr == null)
		{
			gr = new Grp(d);
			days.add(gr);
		}
		if (d.getDate() != gr.date.getDate() || d.getDay() != gr.date.getDay())
		{
			gr = new Grp(d);
			days.add(gr);
		}
		gr.add(c);
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
	void pause()
	{
		new Scanner(System.in).nextLine();
	}
	public Object read(String p) throws Exception
	{
		ObjectInputStream ois=new ObjectInputStream(new FileInputStream(p));
		Object o=ois.readObject();
		ois.close();
		return o;
	}
	public class Grp
	{
		public List<Conv> convr=new ArrayList<>();
		public Date date;

		public Grp(Date d)
		{
			this.date = d;
		}

		public void add(Conv c)
		{
			convr.add(c);
		}
		public void prep()
		{
			if (convr.size() > 1)
			{
//				for (int i=0;i < convr.size();i++)
//				{
//					if (i == 0)
//						convr.get(i).mpos = 2;
//					else if (i == convr.size() - 1)
//						convr.get(i).mpos = 3;
//					else
//						convr.get(i).mpos = 1;
//				}
				ArrayList<Conv> cnv=new ArrayList<>();
				Conv tmp=null;
				for (Conv c:convr)
				{
					if (tmp == null)
					{
						tmp = c;
						cnv.add(c);
						// System.out.println(".. " + cnv.size());
						continue;
					}
					if (tmp.type.equals(c.type))
					{
						cnv.add(c);
						// System.out.println("++ " + cnv.size());
					}
					else
					{
						// System.out.println("== " + cnv.size());
						if (cnv.size() > 1)
						{
							for (int i=0;i < cnv.size();i++)
							{
								if (i == 0)
									cnv.get(i).mpos = 2;
								else if (i == cnv.size() - 1)
									cnv.get(i).mpos = 3;
								else 
									cnv.get(i).mpos = 1;
							}
						}
						cnv.clear();
						cnv.add(c);
					}
					tmp = c;
				}
				if (cnv.size() > 1)
				{
					for (int i=0;i < cnv.size();i++)
					{
						if (i == 0)
							cnv.get(i).mpos = 2;
						else if (i == cnv.size() - 1)
							cnv.get(i).mpos = 3;
						else 
							cnv.get(i).mpos = 1;
					}
				}
			}
			Conv c=new Conv(formatDateHeader(date), formatDateHeader(date), "0");
			convr.add(0, c);
		}
	}
	public String getHeader()
	{
		return 
			"<!DOCTYPE html>" +
			"<html lang=\"en\">\n" +
			"<head>" +
			"<meta charset=\"UTF-8\">" +
			"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
			"<title>MR.X to MS.Y  (unknown <-> unknown)</title>" +
			"</head>\n" +
			"<style>" +
			"body {" +
			"	font-family: Arial, sans-serif;" +
			"	margin:0;" +
			"	background-color: #e5ddd5;" +
			"	padding-top:20px;" +
			"	padding-bottom:20px;" +
			"}" +
			".container {" +
			"	max-width: 600px;" +
			"	margin: 0 auto;" +
			"	display: flex;" +
			"	flex-direction: column;" +
			"}" +
			".date-header {" +
			"	align-self: center;" +
			"	background-color: rgba(255,255,255,0.3);" +
			"	border-radius: 15px;" +
			"	padding: 4px 12px;" +
			"	font-size: 0.75rem;" +
			"	color: #666;" +
			"	margin: 10px 0;" +
			"	width: fit-content;" +
			"	max-width: 80%;" +
			"}" +
			".message {" +
			"	padding-top:10px;" +
			"	padding-bottom:5px;" +
			"	padding-left:15px;" +
			"	padding-right:15px;" +
			"	border-radius: 20px;" +
			"	position: relative;" +
			"	width: fit-content;" +
			"	max-width: 80%;" +
			"	min-width: 120px;" +
			"	font-size:0.8rem;" +
			"	margin:10px;" +
			"}" +
			".received {" +
			"	background-color: #ffffff;" +
			"	align-self: flex-start;" +
			"}" +
			".sent {" +
			"	background-color: #dcf8c6;" +
			"	align-self: flex-end;" +
			"}" +
			".time {" +
			"	font-size: 0.75rem;" +
			"	color: #666;" +
			"	margin-top: 4px;" +
			"	text-align: right;" +
			"}" +
			".sent.group {" +
			"	margin:2px 10px;" +
			"	border-bottom-right-radius: 3px;" +
			"	border-top-right-radius: 3px;" +
			"}" +
			".sent.group.top{" +
			"	border-bottom-right-radius:3px;" +
			"	border-top-right-radius: 20px;" +
			"}" +
			".sent.group.bottom{" +
			"	border-top-right-radius:3px;" +
			"	border-bottom-right-radius: 20px;" +
			"}" +
			".received.group {" +
			"	margin:2px 10px;" +
			"	border-bottom-left-radius: 3px;" +
			"	border-top-left-radius: 3px;" +
			"}" +
			".received.group.top{" +
			"	border-bottom-left-radius:3px;" +
			"	border-top-left-radius: 20px;" +
			"}" +
			".received.group.bottom{" +
			"	border-top-left-radius:3px;" +
			"	border-bottom-left-radius: 20px;" +
			"}" +
			"</style>\n";
	}
}
