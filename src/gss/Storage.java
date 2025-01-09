package gss;

import gss.math.*;
import java.util.*;

public class Storage
{
	public Data base;
	public int[] bShape;
	public int[] shape;
	public int[] sum;
	public int[] acc;

	public Storage(int...sh)
	{
		this.base = new Data(sh);
		this.bShape = Arrays.copyOf(sh, sh.length);
		this.shape = Arrays.copyOf(sh, sh.length);
		this.sum = Util.sumShapes(sh, null);
		this.acc = new int[shape.length];
		for (int i=0;i < acc.length;i++)
			acc[i] = i;
	}
	public float getScalar(int...index)
	{
		int ind=shapeToIndex(index);
		// System.out.println(".." + ind);
		return base.getData(ind);
	}
	public int shapeToIndex(int...index)
	{
		if (index.length != shape.length) // change this " != " to " > " and implement the if block. or use backward loop.
			throw new IndexOutOfBoundsException();
		// if (index.length < shape.length)
		// {
		// if the index length is less than the shape length. we fill the rest with (0). eg.
		// eg index =[5] -> we change into [0,0,5]  assume if the shape was [2,3,6]; this also adds overhead.
		// todo.
		// }
		// System.out.println("== " + Arrays.toString(sum));
		int newPos=0;
		// the loop have error when we use index.length < shape.length, so use backward looping.
		for (int i=0;i < index.length;i++)
		{
			int shapeInd = index[i]; // Math.min(index[i], shape[i] - 1);
			// baseShape[i] -1 ; because . the baseShape minimum value is 1, but 1 means it's acess index is 0, so to make it zero we need to -1;
			// the big issue for 2 days;
			// check if the index at i is not out of bound.
			// if (shapeInd >= shape[i])
			//	throw new IndexOutOfBoundsException();
			newPos += shapeInd *  sum[acc[i]];
		}
		// int finalIndex=(newPos);
		// System.out.println("off = " + offset + ", newP = " + newPos + ", final pos = " + finalIndex + ", len= " + length);
		// System.out.print("--" + finalIndex + "--" + offset + "--");
		return newPos;
	}
	public int[] getShape(int index)
	{
		// this function used to convert index (0-n) into shaps. by iterating all posible combination of shapes, and it returns the combination at the speciic index.
		if (index >= base.length || index < 0)
			throw new IndexOutOfBoundsException();
		// int ind=index;
		// computationally expensive. avoid it, if u can.
		// System.out.println("getting value at index =" + index);
		int[] indShape=new int[this.shape.length];
		for (int i=this.shape.length - 1;i >= 0;i--) // count down starts from shape.length -1 down to 0.
		{
			indShape[i] = index % this.shape[i];
			index = index / this.shape[i];
		}
		return indShape;
	}
	public void transpose()
	{
		int[] acSh=Arrays.copyOf(acc, acc.length);
		int[] sh=new int[shape.length];
		for (int i=0;i < acc.length;i++)
		{
			acc[i] = acSh[acSh.length - 1 - i];
			sh[i] = shape[acc[i]];
		}
		this.shape = sh;
		// this.sum = Util.sumShapes(sh, sum);
	}
}

