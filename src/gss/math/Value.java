package gss.math;

import java.util.*;

public class Value
{
	public Data data;
	public int index;
	public ValueGradFunc func;
	public List<Value> args=new ArrayList<>();

	public Value(Data d, int ind)
	{
		this.data = d;
		this.index = ind;
	}
	public void setOP(ValueGradFunc fn, Value...arg)
	{
		this.func = fn;
		args.clear(); // new start.
		for (Value a:arg)
		{
			args.add(a);
		}
	}

	public static abstract class ValueGradFunc
	{
		public abstract void backward(Value self, Value...args);
	}
}
