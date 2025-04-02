package gss;

import java.util.*;

public class Controll extends Thread
{
	public HashMap<String,Object> keys=new HashMap<>();

	public Controll(String...prms)
	{
		for (String p:prms)
			this.keys.put(p.trim(), false);
	}
	@Override
	public void run()
	{
		Scanner sc=new Scanner(System.in);
		String ln="";
		while ((ln = sc.nextLine().trim()) != null)
		{
			Object obj=keys.get(ln);
			if (obj != null)
			{
				keys.put(ln, !(Boolean)obj);
			}
		}
	}
	public boolean get(String s)
	{
		s = s.trim();
		return keys.containsKey(s) ?((boolean)keys.get(s)): false;
	}
	public boolean[] get(String...kys)
	{
		boolean[]b=new boolean[kys.length];
		String k="";
		for (int i=0;i < kys.length;i++)
		{
			k = kys[i].trim();
			b[i] = keys.containsKey(k) ?((boolean)keys.get(k)): false;
		}
		return b;
	}
	public Set<String> getControlls()
	{
		return keys.keySet();
	}
	public void set(String k)
	{
		keys.put(k.trim(), true);
	}
	public void unset(String k)
	{
		keys.put(k.trim(), false);
	}
	public void set(String k, boolean b)
	{
		keys.put(k.trim(), b);
	}
	public void toggle(String k)
	{
		boolean b=get(k.trim());
		set(k.trim(), !b);
	}
}
