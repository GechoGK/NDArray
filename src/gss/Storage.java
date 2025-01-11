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
	public int position;
	public int dim;

	public Storage(int...sh)
	{
		this.base = new Data(sh);
		// this.bShape = Arrays.copyOf(sh, sh.length);
		this.shape = Arrays.copyOf(sh, sh.length);
		this.sum = Util.sumShapes(sh, null);
		this.acc = new int[shape.length];
		for (int i=0;i < acc.length;i++)
			acc[i] = i;
		this.dim = sh.length;
	}
	public Storage(float[] dt, int[]sh)
	{
		this.base = new Data(dt, sh);
		this.shape = Arrays.copyOf(sh, sh.length);
		this.sum = Util.sumShapes(sh, null);
		this.acc = new int[shape.length];
		for (int i=0;i < acc.length;i++)
			acc[i] = i;
		this.dim = sh.length;
	}
	private Storage(Data d, int[]oShape, int[]sm, int[]ac, int[]sh, int pos)
	{
		this.base = d;
		this.shape = oShape;
		this.sum = sm;
		this.acc = ac;
		this.bShape = sh;
		this.position = pos;
		this.dim = shape.length - bShape.length;
		// this.sum = Util.sumShapes(sh, null);
		// this.offset = offset;
		// transpose doesn't work for subdim arrays.
//		this.acc = new int[shape.length];
//		for (int i=0;i < acc.length;i++)
//		 	acc[i] = i;
	}
	public Storage getStorage(int...index)
	{
		// int[] inf=getPosAndLength(index);
		// inf[0] = position;
		// inf[1...]= new shape;
		// return new Storage(base, Arrays.copyOfRange(inf, 1, inf.length), inf[0]);
		return new Storage(base, shape, sum, acc, index, index.length);
	}
	public float getScalar(int...index)
	{
		int ind=shapeToIndex(index);
		// System.out.println(".." + ind);
		return base.getData(ind);
	}
//	public int[] getPosAndLength(int...index)
//	{
//		{
//
//		}
//		if (index.length == shape.length)
//		{
//			return new int[]{shapeToIndex(index),0};
//		}
//		else
//		{
//			int newPos=0;
//			int length=shape.length - index.length;
//			int[] newShape=new int[length + 1];
//			int strPos=index.length;
//			for (int i=0;i < Math.max(length, index.length);i++)
//			{
//				if (i < length)
//				{
//					int sm=strPos + i;
//					newShape[i + 1] = shape[sm];
//				}
//				if (i < index.length)
//				{
//					// if (index[i] >= shape[i])
//					// 	throw new IndexOutOfBoundsException();
//					int shapeInd = index[i];
//					newPos += shapeInd * sum[acc[i]];
//				}
//				// System.out.println("== " + (sm));
//			}
//			newShape[0] = newPos; // + offset;
//			return newShape;
//		}
//	}
	public int shapeToIndex(int...index)
	{
		if (index.length != shape.length - position) // change this " != " to " > " and implement the if block. or use backward loop.
			throw new IndexOutOfBoundsException();
		int newPos=0;
		// the loop have error when we use index.length < shape.length, so use backward looping.
		for (int i=0;i < shape.length;i++)
		{
			int shapeInd = i >= position ? index[i - position]: bShape[i]; // Math.min(index[i], shape[i] - 1);
			// baseShape[i] -1 ; because . the baseShape minimum value is 1, but 1 means it's acess index is 0, so to make it zero we need to -1;
			// check if the index at i is not out of bound.
			// if (shapeInd >= shape[i])
			//	throw new IndexOutOfBoundsException();
			newPos += shapeInd *  sum[acc[i]];
		}
		int finalIndex=newPos; // + offset;
		// System.out.println("off = " + offset + ", newP = " + newPos + ", final pos = " + finalIndex + ", len= " + length);
		// System.out.print("--" + finalIndex + "--" + offset + "--");
		return finalIndex;
	}
	public int[] getShape(int index)
	{
		// this function used to convert index (0-n) into shaps. by iterating all posible combination of shapes, and it returns the combination at the speciic index.
		if (index >= base.length || index < 0)
			throw new IndexOutOfBoundsException();
		// int ind=index;
		// computationally expensive. avoid it, if u can.
		// System.out.println("getting value at index =" + index);
		int[] indShape=new int[this.shape.length - position];
		for (int i=this.shape.length - 1;i >= position;i--) // count down starts from shape.length -1 down to 0.
		{
			indShape[i - position] = index % this.shape[i];
			index = index / this.shape[i];
		}
		return indShape;
	}
	public void transpose()
	{
		// fix range of axis
		// the axes must be less than the shape length - position.
		int[] accn=Arrays.copyOf(acc, acc.length);
		int[] sh=Arrays.copyOf(shape, shape.length);
		for (int i=0;i < acc.length - position;i++)
		{
			acc[i + position] = accn[accn.length - 1 - i];
			shape[i + position] = sh[acc[i + position]];
		}

//		for (int i=position;i < acc.length;i++)
//		{
//			this.acc[i] = accn[(accn.length - 1 - i) + position];
//			this.shape[i] = sh[acc[i] + position];
//			System.out.println("..." + i + " , " + acc[i] + ", " + sh[i]);
//		}
		// this.acc = accn;
		// this.shape = sh;
		// this.sum = Util.sumShapes(sh, sum);
	}
	public void transpose(int...axes)
	{
		if (axes.length != shape.length - position)
			throw new RuntimeException("invalid axes");
		int[] sh=new int[shape.length];
		for (int i=position;i < axes.length;i++)
		{
			// accn[i] = acc[acc.length - 1 - i];
			sh[i] = shape[axes[i]];
		}
		this.acc = axes;
		this.shape = sh;
		// this.sum = Util.sumShapes(sh, sum);
	}
	public float[] toArray()
	{
		float[] ar=new float[base.length];
		for (int i=0;i < base.length;i++)
			ar[i] = getScalar(getShape(i));
		return ar;
	}
}

