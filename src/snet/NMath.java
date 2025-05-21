package snet;

import java.util.concurrent.*;

public class NMath
{
	public static float[] dot(float[] x, float[][]y)
	{
		if (x.length != y.length)
			throw new IllegalArgumentException("x column must be equal to y row.");
		float[] out = new float[y[0].length];
		for (int r=0;r < y[0].length;r++)
		{
			float sum=0;
			for (int i=0;i < x.length;i++)
			{
				sum += x[i] * y[i][r];
			}
			out[r] = sum;
		}
		return out;
	}
	public static float[] add(float[] a, float[] b)
	{
		if (a.length != b.length)
			throw new IllegalArgumentException("two arrays must be equal to add them.");
		float[] out=new float[a.length];
		for (int i=0;i < out.length;i++)
			out[i] = a[i] + b[i];
		return out;
	}
	public static float[] sub(float[] a, float[] b)
	{
		if (a.length != b.length)
			throw new IllegalArgumentException("two arrays must be equal to add them.");
		float[] out=new float[a.length];
		for (int i=0;i < out.length;i++)
			out[i] = a[i] - b[i];
		return out;
	}
	public static float[] mul(float[] a, float[] b)
	{
		if (a.length != b.length)
			throw new IllegalArgumentException("two arrays must be equal to add them.");
		float[] out=new float[a.length];
		for (int i=0;i < out.length;i++)
			out[i] = a[i] * b[i];
		return out;
	}
	public static float[] div(float[] a, float[] b)
	{
		if (a.length != b.length)
			throw new IllegalArgumentException("two arrays must be equal to add them.");
		float[] out=new float[a.length];
		for (int i=0;i < out.length;i++)
			out[i] = a[i] / b[i];
		return out;
	}
	public static float mapFloat(float mn, float mx, float tmn, float tmx, float val)
	{
		return (tmx - tmn) / (mx - mn) * (val - mn) + tmn;
	}
	public static float[]randomize(float[] d1)
	{
		ThreadLocalRandom rand=ThreadLocalRandom.current();
		for (int i=0;i < d1.length;i++)
			d1[i] = rand.nextFloat();
		return d1;
	}
	public static float[]randomize(float[] d1, float min, float max)
	{
		ThreadLocalRandom rand=ThreadLocalRandom.current();
		for (int i=0;i < d1.length;i++)
			d1[i] = mapFloat(0, 1, min, max, rand.nextFloat());
		return d1;
	}
	public static float[][]randomize(float[][] d1)
	{
		ThreadLocalRandom rand=ThreadLocalRandom.current();
		for (int i=0;i < d1.length;i++)
			for (int j=0;j < d1[0].length;j++)
				d1[i][j] = rand.nextFloat();
		return d1;
	}
	public static float[][]randomize(float[][] d1, float min, float max)
	{
		ThreadLocalRandom rand=ThreadLocalRandom.current();
		for (int i=0;i < d1.length;i++)
			for (int j=0;j < d1[0].length;j++)
				d1[i][j] = mapFloat(0, 1, min, max, rand.nextFloat());
		return d1;
	}
	public static float[] mutuate(float[] x, float factor)
	{
		for (int i=0;i < x.length;i++)
		{
			x[i] += ((float)(Math.random() * 2 - 1) * factor);
		}
		return x;
	}
	public static float[][] mutuate(float[][] x, float factor)
	{
		for (int i=0;i < x.length;i++)
			for (int j=0;j < x[0].length;j++)
			{
				x[i][j] += ((float)(Math.random() * 2 - 1) * factor);
			}
		return x;
	}
	public static float[] sigmoid(float[] x)
	{
		for (int i=0;i < x.length;i++)
			x[i] = 1 / (1 + (float)Math.exp(-x[i]));
		return x;
	}
	public static float[] tanh(float[] x)
	{
		for (int i=0;i < x.length;i++)
			x[i] = (float)Math.tan(x[i]);
		return x;
	}
	public static float[] relu(float[] x)
	{
		for (int i=0;i < x.length;i++)
			x[i] = Math.max(0, x[i]);
		return x;
	}
}
