package gss;

import gss.math.*;
import java.util.*;
import java.util.stream.*;

public class Storage
{
	public Data base;
	public int[] bShape; // it holds shape of the array within 0,position.  // i don't think it is usefull.
	public int[] shape; // no change when subdim.
	public int[] sum;
	public int[] acc;
	public int position;
	public int dim; // problem when subdim.
	public int length; // problem when subdim.

	/*
	 // transpose works.
	 -- getStorage(...)  check for indexOutofBound.
	 */
	// transpose ✓
	// get ✓
	// broadcast   // only "getScalar, setScalar" works.
	// view
	// reshape
	// stack
	// vstack
	// hstack

	public Storage(int...sh)
	{
		this.base = new Data(sh);
		// this.bShape = Arrays.copyOf(sh, sh.length);
		this.shape = Arrays.copyOf(sh, sh.length);
		this.sum = Util.sumShapes(sh, null);
		this.acc = new int[shape.length];
		this.bShape = new int[0];
		for (int i=0;i < acc.length;i++)
			acc[i] = i;
		this.dim = sh.length;
		this.length = Util.length(shape);
	}
	public Storage(float[] dt, int[]sh)
	{
		this.base = new Data(dt, sh);
		this.shape = Arrays.copyOf(sh, sh.length);
		this.sum = Util.sumShapes(sh, null);
		this.acc = new int[shape.length];
		this.bShape = new int[0];
		for (int i=0;i < acc.length;i++)
			acc[i] = i;
		this.dim = sh.length;
		this.length = Util.length(shape);
	}
	private Storage(Data d, int[]oShape, int[]sm, int[]ac, int[]sh, int pos)
	{
		this.base = d;
		init(oShape, sm, ac, sh, pos);

	}
	public void init(int[]oShape, int[]sm, int[]ac, int[]sh, int pos)
	{
		this.shape = oShape;
		this.sum = sm;
		this.acc = ac;
		this.bShape = sh;
		this.position = pos;
		this.dim = shape.length - bShape.length;
		this.length = Util.length(shape); // error with subdim array,
		// this.sum = Util.sumShapes(sh, null);
		// this.offset = offset;
		// transpose doesn't work for subdim arrays.
//		this.acc = new int[shape.length];
//		for (int i=0;i < acc.length;i++)
//		 	acc[i] = i;
	}
	public Storage getStorage(int...index)
	{
		// check if index is within range.
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
	public int shapeToIndex(int...index)
	{
		if (index.length != shape.length - position) // change this " != " to " > " and implement the if block. or use backward loop.
			throw new IndexOutOfBoundsException();
		int newPos=0;
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

		int[] indShape=new int[this.shape.length - position];
		for (int i=this.shape.length - 1;i >= position;i--) // count down starts from shape.length -1 down to 0.
		{
			indShape[i - position] = index % this.shape[i];
			index = index / this.shape[i];
		}
		return indShape;
	}
	public Storage transpose()
	{
		// check for deplicated item.
		// check for items are within range.
		// the axes must be less than the shape length - position.
		int[] accn=Arrays.copyOf(acc, acc.length);
		int[] sh=Arrays.copyOf(shape, shape.length);
		int p=0;
		for (int i=position;i < acc.length;i++)
		{
			accn[i] = acc[accn.length - 1 - p];
			sh[i] = shape[shape.length - 1 - p];
			p++;
		}
		return new Storage(base, sh, sum, accn, bShape, position);
	}
	public Storage transpose(int...axes)
	{
		// check for deplicates item.
		// check for items are within range.
		// the axes must be less than the shape length - position.
		if (axes.length != shape.length - position)
			throw new RuntimeException("invalid axes");
		int[] ac=Arrays.copyOf(acc, acc.length);
		int[] sh=Arrays.copyOf(shape, shape.length);
		int p=0;
		for (int i:axes)
		{
			if (i >= axes.length)
				throw new IndexOutOfBoundsException("index must not greater than the dimension o the array");
			ac[p + position] = acc[i + position];
			sh[p + position] = shape[i + position];
			p++;
		}
		// this.acc = ac;
		// this.shape = sh;
		return new Storage(base, sh, sum, ac, bShape, position);
	}
	public void view(int[]newSh, int...ac)
	{
		if (newSh.length == 0)
			throw new RuntimeException("invalid shape " + Arrays.toString(newSh));
		if (Util.length(shape) != Util.length(newSh))
			throw new RuntimeException("invalid shape: the total lemfth of the shape must be equal. :"   + Arrays.toString(newSh));
		int[]sm=Util.sumShapes(newSh, null);
		init(newSh, sm, ac, bShape, position);
	}
	public float[] toArray()
	{
		float[] ar=new float[base.length];
		for (int i=0;i < base.length;i++)
			ar[i] = getScalar(getShape(i));
		return ar;
	}
	public int[] getShape()
	{
		int[] s=new int[shape.length - position];
		for (int i=0;i < s.length;i++)
			s[i] = shape[i + position];
		return s;
	}
}

