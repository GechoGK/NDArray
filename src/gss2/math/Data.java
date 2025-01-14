package gss2.math;

import gss.math.*;

public class Data
{
	// it just holds the flatten array that are used by many shape utils.
	public float[] data;
	public int length;

	public Data(int...shape)
	{
		this.data = new float[Util.length(shape)];
		this.length = data.length;
	}
}
