package gss.test;

import gss.math.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.util.concurrent.*;

public class Test4
{
	public static void main2(String...args)
	{

		test1();
		// test2();
	}
	static void test2()
	{
		float[][][][] dt=new float[100][100][100][60];
		System.out.println("memory allocated");

		while (true)
		{
			long tl=System.currentTimeMillis();
			float ff=0;
			for (int i=0;i < dt.length;i++)
				for (int j=0;j < dt[0].length;j++)
					for (int k=0;k < dt[0][0].length;k++)
						for (int l=0;l < dt[0][0][0].length;l++)
						{
							ff = dt[i][j][k][l]; // 3000-7000
						}
			tl = System.currentTimeMillis() - tl;
			System.out.println(tl + " millis");
		}
	}
	static void test1()
	{
		// float[] dt=new float[100 * 100 * 100 * 60];
		// Shape s=new Shape(new int[]{100, 100, 100, 60}, dt);
		// System.out.println("memory allocated");

		// loopOver(s);
		// loopShape(s);
		// loopThd();
		loopThW();

	}
	static void loopThW()
	{
		System.out.println(System.getProperties());
		System.out.println(System.getenv());
		String[] dt={"data1","data2","data3","data4","data5","data6","data7","data8","data9","data10"}; // without parallel. it must wait 5 sec.
		Stream.of(dt).parallel().forEach(new Consumer<String>(){
				@Override
				public void accept(String p1)
				{
					try
					{
						Thread.sleep(1000); // sleep for 1 sec.
						System.out.println(p1 + " >> hello after 1sec sleep.");	
					}
					catch (Exception e)
					{}
				}
				@Override
				public Consumer<String> andThen(Consumer<? super String> after)
				{
					return null;
				}
			});
		System.out.println("done!");
	}
	static void loopThd()
	{
		shape = new int[]{100,100,100,5};
		Float[] dt=new Float[100 * 100 * 100 * 5];
		for (int i=0;i < dt.length;i++)
			dt[i] = (float)i;
		System.out.println("memory allocated.");
		Stream.of(dt).parallel().forEach(new Consumer<Float>(){
				@Override
				public void accept(Float p1)
				{
					int[]p=getShape(p1.intValue());
				}
				@Override
				public Consumer<Float> andThen(Consumer<? super Float> after)
				{
					// TODO: Implement this method
					return null;
				}
			});
		System.out.println("done!");
	}
	static void loopShape(Shape s)
	{
		float[] dt=s.data.data;
		s = s.view(-1);
		shape = s.shape;
		stride = s.stride;
		System.out.println(Arrays.toString(stride));

		while (true)
		{
			long l=System.currentTimeMillis();
			float ff=0;
			for (int i=0;i < dt.length;i++)
			// ff = dt[i]; // 600-699 millis
				ff = dt[shapeToIndex(i)]; // 8300 - 9999, 5500 - 6590, 4500 - 5300,5500 - 6500
			l = System.currentTimeMillis() - l;
			System.out.println(l + " millis");
		}
	}
	static int[]shape=null,stride=null;
	public static int shapeToIndex(int...index)
	{
		int newPos=0;
		for (int i=0;i < index.length;i++)
		{
			int shapeInd = index[i];
			newPos += shapeInd * stride[i];
		}
		return newPos;
	}
	public static int[] getShape(int index)
	{
		int[] sh=shape;
		int[] indShape=new int[sh.length];
		for (int i=sh.length - 1;i >= 0;i--)
		{
			indShape[i] = index % sh[i];
			index = index / sh[i];
		}
		return indShape;
	}
	static void loopOver(Shape s)
	{
		float[] dt=s.data.data;
		shape = s.shape;
		stride = s.stride;

		while (true)
		{
			long tl=System.currentTimeMillis();
			float ff=0;
			int newPos=0;
			int[] index=new int[4];
			for (int i=0;i < 100;i++)
			{
				index[0] = i;
				for (int j=0;j < 100;j++)
				{
					index[1] = j;
					for (int k=0;k < 100;k++)
					{
						index[2] = k;
						for (int l=0;l < 60;l++)
						{
							// ff = dt[shapeToIndex(i, j, k, l)]; // 12000-12999,
							index[3] = l;
							newPos = 0;
							for (int p=0;p < shape.length;p++)
								newPos += index[p] * stride[p];
							ff = dt[newPos]; // 12400-13300,11500-12500
						}
					}
				}
			}
			tl = System.currentTimeMillis() - tl;
			System.out.println(tl + " millis"); /// 19000-19999 millis
		}
	}
}
