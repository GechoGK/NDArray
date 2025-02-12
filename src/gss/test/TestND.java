package gss.test;

import gss.arr.*;
import gss.math.*;

import static gss.math.Util.*;

public class TestND
{
	private static int[] getShapeForDot(int[]s1, int[]s2)
	{
		/*
		 output shape is determined by the given array's shape.
		 example  a.shape = (x,y,z)
		 ..       b.shape = (h,i,j) then
		 ..
		 ..       c = a.dot(b)
		 ..
		 .. first we need to check if "z" and "i" are equal if true.
		 the output shape(c.shape) would be (x,y,h,j) it increase by 1 dimension.

		 */
		if (getAtR(s1, 0) != getAtR(s2, 1))
			throw new RuntimeException("dimensions not equal to compute dot product (" + getAtR(s1, 0) + " != " + getAtR(s2, 1) + ")");
		int[] newShape=new int[s1.length + s2.length - 2];

		for (int i=0;i < s1.length - 1;i++)
			newShape[i] = s1[i];
		int str=s1.length - 1;
		for (int i=0;i < s2.length - 1;i++)
			newShape[str + i] = s2[i];
		newShape[newShape.length - 1] = s2[s2.length - 1];
		// System.out.println("new Shape =" + Arrays.toString(newShape));
		return newShape;
	}
	public static NDArray[]prepareArrayForDotProduct(NDArray a1, NDArray a2)
	{
		NDArray x=a1.view(-1, Util.getAtR(a1.getShape(), 0));
		// don't transpose because transposing and hview is computationally expensive, so we use only view.
		// and also we use anothe toe of dot product. by considering no transpose.
		NDArray y=a2.view(-1, Util.getAtR(a2.getShape(), 0));
		return new NDArray[]{x,y};
	}
	public static NDArray dot(NDArray a, NDArray b)
	{
		// peefect for dot product wothout transposing.
		if (b.getDim() == 1)
			b = b.view(b.getShape()[0], 1);
		int[] newShape=getShapeForDot(a.getShape(), b.getShape());
		// print("new shape =" + Arrays.toString(newShape));
		NDArray[] arrs=prepareArrayForDotProduct(a, b);
		a = arrs[0];
		b = arrs[1];
		float[][] af=a.base.to2DArray(null);
		float[][] bf=b.base.to2DArray(null);

		float[][] fout=sdot(af, bf);
		// print(fout.length + ", " + fout[0].length);
		NDArray out=new NDArray(newShape, fout).setEnableGradient(a.requiresGradient() | b.requiresGradient());
		out.setGradientFunction(GradFunc.dotGradient, a, b);
		return out;
	}
	private static float[][] sdot(float[][] a1, float[][] a2)
	{
		if (a2.length % a1[0].length != 0)
			throw new RuntimeException("unable to compute dot product");
		int cnt=a2.length / a1[0].length;
		float[][] m=new float[a1.length][a2[0].length * cnt];
		for (int xr=0;xr < a1.length;xr++)
			for (int yc=0;yc < a2[0].length;yc++)	
				for (int cn=0;cn < cnt;cn++)
				{
					float s=0;
					for (int xc=0;xc < a1[0].length;xc++)
					{
						int ps=cn * a1[0].length + xc;
						s += a1[xr][xc] * a2[ps][yc];
					}
					int ps=a2[0].length * cn + yc;
					m[xr][ps] = s;	
				}
		return m;
	}
}
